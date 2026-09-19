package com.kroxaboom.skazka.android.telemetry;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * RU: Небольшой transport-neutral event. Data копируется, чтобы очередь не зависела
 * от последующих изменений объекта вызывающим кодом.
 *
 * EN: Small transport-neutral event. Data is copied so queue state is not affected
 * by later caller mutations.
 */
public record TelemetryEvent(
        String clientEventId,
        String kind,
        String type,
        String severity,
        String sourceId,
        String timestamp,
        Map<String, Object> data
) {
    public TelemetryEvent {
        clientEventId = clean(clientEventId);
        kind = clean(kind);
        type = clean(type);
        severity = clean(severity);
        sourceId = clean(sourceId);
        timestamp = clean(timestamp);
        data = data == null ? Map.of() : Map.copyOf(new LinkedHashMap<>(data));
    }

    @Override
    public Map<String, Object> data() {
        return new LinkedHashMap<>(data);
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
