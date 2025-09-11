import re
from typing import List
import numpy as np
import pandas as pd
from app.schemas.log_entry import LogEntry

_DIGITS_RE = re.compile(r"\d")
_ERROR_WORDS = re.compile(
    r"\b(error|exception|fail|timeout|unavailable|panic|crash|corruption|meltdown)\b",
    re.IGNORECASE
)

class Preprocessor:
    @staticmethod
    def to_dataframe(logs: List[LogEntry]) -> pd.DataFrame:
        rows = []
        for log in logs:
            rows.append({
                "timestamp": log.timestamp,
                "level": log.level.value,
                "message": log.message
            })

        df = pd.DataFrame(rows)
        df["timestamp"] = pd.to_datetime(df["timestamp"], errors="coerce")
        df["timestamp"] = df["timestamp"].astype(np.int64) // 10**9

        level_map = {"TRACE": 0, "DEBUG": 1, "INFO": 2, "WARN": 3, "ERROR": 4, "FATAL": 5}
        df["level_encoded"] = df["level"].map(level_map).fillna(-1).astype(int)

        df["message_length"] = df["message"].str.len().fillna(0).astype(int)
        df["digit_count"] = df["message"].apply(lambda x: len(_DIGITS_RE.findall(x)) if isinstance(x, str) else 0)
        df["has_error"] = df["message"].apply(lambda x: int(bool(_ERROR_WORDS.search(x))) if isinstance(x, str) else 0)
        df["upper_ratio"] = df["message"].apply(
            lambda x: sum(1 for c in x if c.isupper()) / (len(x) + 1) if isinstance(x, str) else 0
        )

        return df[["timestamp","level_encoded","message_length","digit_count","has_error","upper_ratio"]].fillna(0)
