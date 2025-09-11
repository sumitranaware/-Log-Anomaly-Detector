import numpy as np
import joblib
from sklearn.ensemble import IsolationForest
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline

class IsolationForestModel:
    def __init__(
        self,
        contamination: float = 0.05,
        n_estimators: int = 100,
        random_state: int = 42,
        scale: bool = True
    ):
        steps = []
        if scale:
            steps.append(("scaler", StandardScaler()))
        steps.append((
            "clf",
            IsolationForest(
                contamination=contamination,
                n_estimators=n_estimators,
                random_state=random_state
            )
        ))
        self.pipeline = Pipeline(steps)
        self.offset_: float | None = None
        self._is_trained = False

    def fit(self, X: np.ndarray) -> dict:
        self.pipeline.fit(X)
        self.offset_ = self.pipeline.named_steps["clf"].offset_
        self._is_trained = True
        return {
            "n_samples": X.shape[0],
            "n_features": X.shape[1],
            "offset": float(self.offset_)
        }

    def is_ready(self) -> bool:
        return self._is_trained

    def predict(self, X: np.ndarray) -> list[dict]:
        if not self._is_trained:
            raise RuntimeError("Model not trained")

        clf = self.pipeline.named_steps["clf"]
        if "scaler" in self.pipeline.named_steps:
            X_proc = self.pipeline.named_steps["scaler"].transform(X)
        else:
            X_proc = X

        raw_scores = clf.decision_function(X_proc)
        out = []
        for score in raw_scores:
            is_anom = score < 0
            dist = -score if is_anom else 0.0
            out.append({
                "is_anomaly": bool(is_anom),
                "score": float(dist),      # >= 0
                "raw_score": float(score)  # can be negative
            })
        return out

    def save(self, path: str) -> None:
        joblib.dump({
            "pipeline": self.pipeline,
            "offset": self.offset_,
            "trained": self._is_trained
        }, path)

    @classmethod
    def load(cls, path: str) -> "IsolationForestModel":
        data = joblib.load(path)
        obj = cls(scale=False)
        obj.pipeline = data["pipeline"]
        obj.offset_ = data["offset"]
        obj._is_trained = data.get("trained", True)
        return obj
