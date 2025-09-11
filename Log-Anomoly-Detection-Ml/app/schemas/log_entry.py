from enum import Enum
from typing import Optional, Dict, Any, Union
from pydantic import BaseModel, Field
from datetime import datetime

class LogLevel(str, Enum):
    TRACE = "TRACE"
    DEBUG = "DEBUG"
    INFO = "INFO"
    WARN = "WARN"
    ERROR = "ERROR"
    FATAL = "FATAL"

class LogEntry(BaseModel):
    serviceName: Optional[str] = None
    timestamp: Union[str, int, float, datetime]
    level: LogLevel
    message: str
    metadata: Optional[Dict[str, Any]] = None

    class Config:
        extra = "ignore"
