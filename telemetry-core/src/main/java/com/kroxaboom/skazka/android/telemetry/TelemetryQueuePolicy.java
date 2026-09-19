package com.kroxaboom.skazka.android.telemetry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RU: Coalesce применяется только к low-priority usage. Ошибки и source-события
 * сохраняются отдельно, а enqueue во время flush не теряется.
 *
 * EN: Coalescing applies only to low-priority usage. Errors and source events
 * stay distinct, and events queued during a flush are preserved.
 */
public final class TelemetryQueuePolicy {
    public static final int MAX_QUEUE = 100;

    private TelemetryQueuePolicy() {}

    public static List<TelemetryEvent> append(
            List<TelemetryEvent> queue,
            TelemetryEvent incoming
    ) {
        List<TelemetryEvent> items = copy(queue);
        if (incoming == null) {
            return bounded(items);
        }

        String key = coalesceKey(incoming);
        if (!key.isEmpty()) {
            for (int i = items.size() - 1; i >= 0; i--) {
                if (key.equals(coalesceKey(items.get(i)))) {
                    items.set(i, mergeCount(items.get(i), incoming));
                    return bounded(items);
                }
            }
        }

        items.add(incoming);
        return bounded(items);
    }

    public static List<TelemetryEvent> mergeAfterFlush(
            List<TelemetryEvent> remaining,
            List<TelemetryEvent> current,
            Set<String> snapshotIds,
            boolean usageEnabled,
            boolean errorEnabled
    ) {
        List<TelemetryEvent> merged = new ArrayList<>();
        Set<String> ids = new HashSet<>();

        addEligible(merged, ids, remaining, usageEnabled, errorEnabled, null);
        addEligible(merged, ids, current, usageEnabled, errorEnabled, snapshotIds);

        return bounded(merged);
    }

    public static boolean lowPriorityUsage(TelemetryEvent event) {
        if (event == null
                || !"usage".equals(event.kind())
                || !"info".equals(event.severity())
                || !event.sourceId().isEmpty()
                || event.type().isEmpty()) {
            return false;
        }

        String type = event.type().toLowerCase(java.util.Locale.ROOT);
        return !type.contains("error")
                && !type.contains("critical")
                && !type.contains("security");
    }

    private static String coalesceKey(TelemetryEvent event) {
        if (!lowPriorityUsage(event)) {
            return "";
        }
        return event.type() + "|" + stableWithoutCount(event.data());
    }

    private static TelemetryEvent mergeCount(
            TelemetryEvent existing,
            TelemetryEvent incoming
    ) {
        Map<String, Object> data = existing.data();
        int count = positiveCount(data.get("count"));
        int incomingCount = positiveCount(incoming.data().get("count"));
        long sum = (long) count + incomingCount;
        data.put("count", (int) Math.min(Integer.MAX_VALUE, sum));

        return new TelemetryEvent(
                existing.clientEventId(),
                existing.kind(),
                existing.type(),
                existing.severity(),
                existing.sourceId(),
                incoming.timestamp().isEmpty() ? existing.timestamp() : incoming.timestamp(),
                data
        );
    }

    private static int positiveCount(Object value) {
        if (value instanceof Number number) {
            return Math.max(1, number.intValue());
        }
        return 1;
    }

    private static void addEligible(
            List<TelemetryEvent> output,
            Set<String> ids,
            Collection<TelemetryEvent> source,
            boolean usageEnabled,
            boolean errorEnabled,
            Set<String> excludedIds
    ) {
        if (source == null) {
            return;
        }

        for (TelemetryEvent event : source) {
            if (event == null || !kindEnabled(event.kind(), usageEnabled, errorEnabled)) {
                continue;
            }

            String id = event.clientEventId();
            if (excludedIds != null && !id.isEmpty() && excludedIds.contains(id)) {
                continue;
            }
            if (id.isEmpty() || ids.add(id)) {
                output.add(event);
            }
        }
    }

    private static boolean kindEnabled(String kind, boolean usageEnabled, boolean errorEnabled) {
        if ("usage".equals(kind)) {
            return usageEnabled;
        }
        if ("error".equals(kind)) {
            return errorEnabled;
        }
        return true;
    }

    private static List<TelemetryEvent> bounded(List<TelemetryEvent> source) {
        List<TelemetryEvent> items = new ArrayList<>(source);
        while (items.size() > MAX_QUEUE) {
            int drop = -1;
            for (int i = 0; i < items.size(); i++) {
                if (lowPriorityUsage(items.get(i))) {
                    drop = i;
                    break;
                }
            }
            items.remove(drop >= 0 ? drop : 0);
        }
        return List.copyOf(items);
    }

    private static List<TelemetryEvent> copy(List<TelemetryEvent> source) {
        return source == null ? new ArrayList<>() : new ArrayList<>(source);
    }

    private static String stableWithoutCount(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Map<?, ?> map) {
            List<Map.Entry<?, ?>> entries = new ArrayList<>(map.entrySet());
            entries.removeIf(entry -> "count".equals(String.valueOf(entry.getKey())));
            entries.sort(Comparator.comparing(entry -> String.valueOf(entry.getKey())));

            StringBuilder output = new StringBuilder("{");
            for (Map.Entry<?, ?> entry : entries) {
                output.append(entry.getKey())
                        .append('=')
                        .append(stableWithoutCount(entry.getValue()))
                        .append(';');
            }
            return output.append('}').toString();
        }
        if (value instanceof Collection<?> collection) {
            StringBuilder output = new StringBuilder("[");
            for (Object item : collection) {
                output.append(stableWithoutCount(item)).append(';');
            }
            return output.append(']').toString();
        }
        return String.valueOf(value);
    }
}
