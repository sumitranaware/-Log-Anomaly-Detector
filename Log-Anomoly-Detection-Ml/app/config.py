from pydantic import BaseModel
from typing import List
import os

class _Settings(BaseModel):
    CONTAMINATION: float = float(os.getenv("CONTAMINATION", "0.05"))
    N_ESTIMATORS: int = int(os.getenv("N_ESTIMATORS", "200"))
    RANDOM_STATE: int = int(os.getenv("RANDOM_STATE", "42"))

    MODEL_PATH: str = os.getenv("MODEL_PATH", "model/insforest.joblib")
    AUTO_SAVE_MODEL: bool = os.getenv("AUTO_SAVE_MODEL", "true").lower() == "true"
    AUTO_TRAIN_ON_DETECT: bool = os.getenv("AUTO_TRAIN_ON_DETECT", "false").lower() == "true"

    ALLOW_ORIGINS: List[str] = os.getenv("ALLOW_ORIGINS", "*").split(",")

settings = _Settings()
