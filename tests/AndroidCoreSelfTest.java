import com.kroxaboom.skazka.android.awake.ScreenAwakeSnapshot;
import com.kroxaboom.skazka.android.awake.ScreenAwakeState;
import com.kroxaboom.skazka.android.awake.ScreenAwakeStateMachine;
import com.kroxaboom.skazka.android.telemetry.TelemetryEvent;
import com.kroxaboom.skazka.android.telemetry.TelemetryQueuePolicy;
import com.kroxaboom.skazka.android.theme.SkazkaThemeCore;
import com.kroxaboom.skazka.android.theme.ThemePalette;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AndroidCoreSelfTest {
    public static void main(String[] args) {
        screenAwakePolicy();
        telemetryPolicy();
        themePolicy();
        System.out.println("PASS: Skazka Android Core screen-awake and telemetry policies");
    }

    private static void screenAwakePolicy() {
        long now = 1_700_000_000_000L;

        check(
                ScreenAwakeStateMachine.current(
                        new ScreenAwakeSnapshot(false, false, true, false, true, false, 0, now)
                ) == ScreenAwakeState.READY,
                "ready state"
        );
        check(
                ScreenAwakeStateMachine.current(
                        new ScreenAwakeSnapshot(true, true, true, true, true, false, 0, now)
                ) == ScreenAwakeState.ON_INFINITE,
                "infinite state"
        );
        check(
                ScreenAwakeStateMachine.current(
                        new ScreenAwakeSnapshot(true, true, true, true, true, false, now + 1000, now)
                ) == ScreenAwakeState.ON_TIMER,
                "timer state"
        );
        check(
                ScreenAwakeStateMachine.current(
                        new ScreenAwakeSnapshot(true, true, true, true, true, true, 0, now)
                ) == ScreenAwakeState.ON_CHARGING,
                "charging state"
        );
        check(
                ScreenAwakeStateMachine.current(
                        new ScreenAwakeSnapshot(true, false, true, false, true, false, 0, now)
                ) == ScreenAwakeState.RECOVERY_REQUIRED,
                "recovery state"
        );
        check(
                ScreenAwakeStateMachine.current(
                        new ScreenAwakeSnapshot(false, false, false, false, true, false, 0, now)
                ) == ScreenAwakeState.NEEDS_WRITE_SETTINGS,
                "write-settings permission state"
        );
    }

    private static void telemetryPolicy() {
        List<TelemetryEvent> queue = new ArrayList<>();
        queue = TelemetryQueuePolicy.append(queue, usage("a", "app.started", 1));
        queue = TelemetryQueuePolicy.append(queue, usage("b", "app.started", 1));

        check(queue.size() == 1, "usage coalesced");
        check(((Number) queue.get(0).data().get("count")).intValue() == 2, "coalesced count");
        check("a".equals(queue.get(0).clientEventId()), "stable event id retained");
        check("b-time".equals(queue.get(0).timestamp()), "latest timestamp retained");

        queue = TelemetryQueuePolicy.append(queue, error("e1"));
        queue = TelemetryQueuePolicy.append(queue, error("e2"));
        check(queue.size() == 3, "errors stay distinct");

        TelemetryEvent old = usage("old", "app.started", 1);
        TelemetryEvent fresh = usage("new", "app.tap", 1);
        List<TelemetryEvent> merged = TelemetryQueuePolicy.mergeAfterFlush(
                List.of(),
                List.of(old, fresh),
                Set.of("old"),
                true,
                true
        );
        check(merged.size() == 1 && "new".equals(merged.get(0).clientEventId()),
                "event added during flush survives");

        List<TelemetryEvent> overflow = new ArrayList<>();
        overflow.add(error("critical"));
        for (int i = 0; i < 110; i++) {
            overflow = new ArrayList<>(
                    TelemetryQueuePolicy.append(
                            overflow,
                            usage("u" + i, "app.usage." + i, i)
                    )
            );
        }

        check(overflow.size() == TelemetryQueuePolicy.MAX_QUEUE, "queue bounded");
        check(
                overflow.stream().anyMatch(event -> "critical".equals(event.clientEventId())),
                "error survives usage overflow"
        );
    }

    private static TelemetryEvent usage(String id, String type, int value) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("value", value);
        return new TelemetryEvent(id, "usage", type, "info", "", id + "-time", data);
    }

    private static TelemetryEvent error(String id) {
        return new TelemetryEvent(
                id,
                "error",
                "app.error",
                "critical",
                "",
                id + "-time",
                Map.of("value", 1)
        );
    }

    private static void themePolicy() {
        ThemePalette dark = SkazkaThemeCore.resolve(SkazkaThemeCore.DARK, false);
        check(dark.primary == 0xff8e6af1, "signature primary");
        check(dark.primaryStrong == 0xff633acc, "signature primary strong");
        check(SkazkaThemeCore.resolve(SkazkaThemeCore.OLED, false).background == 0xff000000,
                "signature oled black");
        check(SkazkaThemeCore.signature(SkazkaThemeCore.SYSTEM, true),
                "system dark signature");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
