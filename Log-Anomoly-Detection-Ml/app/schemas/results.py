from typing import Any, Dict, List, Optional, Union
from pydantic import BaseModel, Field

class TrainResult(BaseModel):
    message: str
    metrics: Dict[str, Any]

class Anomaly(BaseModel):
    is_anomaly: bool
    score: float
    raw_score: float
    serviceName: Optional[str]
    level: str
    message: str
    timestamp: Optional[Union[str, int, float]]
    features: Dict[str, Any]

class DetectResult(BaseModel):
    count: int
    anomalies: List[Anomaly]
