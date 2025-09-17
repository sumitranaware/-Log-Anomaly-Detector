package com.example.Log_Anomoly_Detectiong_Spring.utility;

import com.example.Log_Anomoly_Detectiong_Spring.dto.LogRequest;
import com.example.Log_Anomoly_Detectiong_Spring.entity.LogEntry;

import java.time.Instant;
import java.util.*;

public class LogFeatureExtractor {


    private static final Map<String, Instant> lastLogTime = new HashMap<>();

    private static final Map<String, Integer> consecutiveHighLevel = new HashMap<>();

    private static final Set<String> rareServices = new HashSet<>(Arrays.asList("NotificationService", "CacheService"));

    private static final List<String> errorKeywords = Arrays.asList("error", "fail", "exception", "crash");
    private static final List<String> criticalKeywords = Arrays.asList("timeout", "null pointer", "disk full");

    public static Map<String, Object> extractFeatures(LogRequest log) {
        Map<String, Object> features = new HashMap<>();
        String message = log.getMessage() != null ? log.getMessage() : "";


        int levelEncoded = encodeLevel(log.getLogLevel());
        features.put("level_encoded", levelEncoded);


        features.put("message_length", message.length());
        features.put("digit_count", countDigits(message));
        features.put("upper_ratio", calculateUpperRatio(message));
        features.put("has_error", containsKeywords(message, errorKeywords) ? 1 : 0);
        features.put("keyword_count", countKeywords(message, criticalKeywords));
        features.put("special_char_ratio", calculateSpecialCharRatio(message));
        features.put("word_count", message.trim().isEmpty() ? 0 : message.trim().split("\\s+").length);


        Instant timestamp = log.getTimestamp() != null ? log.getTimestamp() : Instant.now();
        long timeDiffPrev = lastLogTime.containsKey(log.getServiceName())
                ? timestamp.getEpochSecond() - lastLogTime.get(log.getServiceName()).getEpochSecond()
                : 0;
        features.put("time_diff_prev", timeDiffPrev);


        int consecutive = consecutiveHighLevel.getOrDefault(log.getServiceName(), 0);
        if (levelEncoded >= 3) {
            consecutive += 1;
        } else {
            consecutive = 0;
        }
        consecutiveHighLevel.put(log.getServiceName(), consecutive);
        features.put("consecutive_level_spike", consecutive);


        features.put("service_frequency", 1);


        features.put("rare_service_flag", rareServices.contains(log.getServiceName()) ? 1 : 0);


        lastLogTime.put(log.getServiceName(), timestamp);

        return features;
    }

    private static int encodeLevel(String level) {
        if (level == null) return 0;
        return switch (level.toUpperCase()) {
            case "DEBUG" -> 1;
            case "INFO" -> 2;
            case "WARN" -> 3;
            case "ERROR" -> 4;
            case "FATAL" -> 5;
            default -> 0;
        };
    }

    private static int countDigits(String s) {
        return (int) s.chars().filter(Character::isDigit).count();
    }

    private static double calculateUpperRatio(String s) {
        if (s.isEmpty()) return 0.0;
        long upperCount = s.chars().filter(Character::isUpperCase).count();
        return (double) upperCount / s.length();
    }

    private static int countKeywords(String s, List<String> keywords) {
        int count = 0;
        String lower = s.toLowerCase();
        for (String kw : keywords) {
            if (lower.contains(kw)) count++;
        }
        return count;
    }

    private static boolean containsKeywords(String s, List<String> keywords) {
        String lower = s.toLowerCase();
        for (String kw : keywords) {
            if (lower.contains(kw)) return true;
        }
        return false;
    }

    private static double calculateSpecialCharRatio(String s) {
        if (s.isEmpty()) return 0.0;
        long specialCount = s.chars().filter(c -> !Character.isLetterOrDigit(c) && !Character.isWhitespace(c)).count();
        return (double) specialCount / s.length();
    }
    public static Map<String, Object> extractFeatures(LogEntry entry) {
        // reuse fields from LogEntry
        LogRequest req = new LogRequest();
        req.setServiceName(entry.getServiceName());
        req.setLogLevel(entry.getLogLevel());
        req.setMessage(entry.getMessage());
        req.setTimestamp(entry.getTimestamp());
        return extractFeatures(req);
    }
}
