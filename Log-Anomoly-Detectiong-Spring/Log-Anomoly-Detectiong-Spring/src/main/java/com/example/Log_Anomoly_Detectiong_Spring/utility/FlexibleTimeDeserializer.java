package com.example.Log_Anomoly_Detectiong_Spring.utility;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;

public class FlexibleTimeDeserializer extends JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();

        try {
            long epoch = Long.parseLong(value);
            // treat as milliseconds
            if (String.valueOf(epoch).length() >= 13) {
                return Instant.ofEpochMilli(epoch);
            } else {
                return Instant.ofEpochSecond(epoch);
            }
        } catch (NumberFormatException ignored) {
        }

        return Instant.parse(value);
    }
}
