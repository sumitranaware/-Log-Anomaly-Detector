import traceback
from typing import List
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware

from app.config import settings
from app.schemas.log_entry import LogEntry, LogLevel
from app.schemas.results import TrainResult, DetectResult, Anomaly
from app.utils.preprocess import Preprocessor
from app.models.isolation_forest import IsolationForestModel

app = FastAPI(title="Log Anomaly ML", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.ALLOW_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

model = IsolationForestModel(
    contamination=settings.CONTAMINATION,
    n_estimators=settings.N_ESTIMATORS,
    random_state=settings.RANDOM_STATE
)

# Define suspicious keywords for boosting anomaly detection
SUSPICIOUS_KEYWORDS = ["CRASHED", "FATAL", "ERROR", "MELTDOWN", "EXCEPTION", "FAILURE"]

@app.on_event("startup")
def load_model():
    try:
        globals()["model"] = IsolationForestModel.load(settings.MODEL_PATH)
    except Exception:
        pass

@app.get("/health")
def health():
    return {"status": "ok", "model_ready": model.is_ready()}

@app.post("/train", response_model=TrainResult)
def train(entries: List[LogEntry]):
    normal = [e for e in entries if e.level not in (LogLevel.ERROR, LogLevel.FATAL)]
    if not normal:
        raise HTTPException(400, "No normal logs to train on")

    try:
        df = Preprocessor.to_dataframe(normal)
        metrics = model.fit(df.values)
        if settings.AUTO_SAVE_MODEL:
            model.save(settings.MODEL_PATH)
        return TrainResult(message="Model trained on normal logs", metrics=metrics)
    except Exception as exc:
        traceback.print_exc()
        raise HTTPException(500, detail=str(exc))

@app.post("/detect", response_model=DetectResult)
def detect(entries: List[LogEntry]):
    if not model.is_ready():
        raise HTTPException(400, "Model not trained; call /train first")

    try:
        df = Preprocessor.to_dataframe(entries)
        raw = model.predict(df.values)
        feats = df.to_dict(orient="records")

        anomalies = []
        for entry, res, feat in zip(entries, raw, feats):
            # Base anomaly detection from Isolation Forest
            is_anom = res["score"] < 0.0
            dist = -res["score"] if is_anom else 0.0

            # Boost anomaly flag if message contains suspicious keywords
            if any(keyword in entry.message.upper() for keyword in SUSPICIOUS_KEYWORDS):
                is_anom = True
                dist = max(dist, 0.05)  # Boost minimum anomaly score

            anomalies.append(
                Anomaly(
                    is_anomaly=is_anom,
                    score=dist,
                    raw_score=res["raw_score"],
                    serviceName=entry.serviceName,
                    level=entry.level.value,
                    message=entry.message,
                    timestamp=entry.timestamp,
                    features=feat
                )
            )

        return DetectResult(count=len(anomalies), anomalies=anomalies)

    except Exception as exc:
        traceback.print_exc()
        raise HTTPException(500, detail=str(exc))
