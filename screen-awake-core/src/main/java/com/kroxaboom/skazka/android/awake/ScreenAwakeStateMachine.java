package com.kroxaboom.skazka.android.awake;

/**
 * RU: Решение о состоянии режима отделено от UI, уведомлений и телеметрии.
 * EN: Mode-state decisions are separated from UI, notifications, and telemetry.
 */
public final class ScreenAwakeStateMachine {
    private ScreenAwakeStateMachine() {}

    public static ScreenAwakeState current(ScreenAwakeSnapshot value) {
        if (value == null) {
            throw new IllegalArgumentException("Snapshot must not be null");
        }

        if (value.requested()) {
            if (!value.canWriteSettings()) {
                return ScreenAwakeState.NEEDS_WRITE_SETTINGS;
            }
            if (!value.savedOriginalTimeout() || !value.timeoutApplied()) {
                return ScreenAwakeState.RECOVERY_REQUIRED;
            }
            if (value.chargingOwned()) {
                return ScreenAwakeState.ON_CHARGING;
            }
            if (value.timerEndAt() > value.now()) {
                return ScreenAwakeState.ON_TIMER;
            }
            return ScreenAwakeState.ON_INFINITE;
        }

        if (value.savedOriginalTimeout()) {
            return ScreenAwakeState.RECOVERY_REQUIRED;
        }
        if (!value.canWriteSettings()) {
            return ScreenAwakeState.NEEDS_WRITE_SETTINGS;
        }
        if (!value.notificationsGranted()) {
            return ScreenAwakeState.NEEDS_NOTIFICATIONS;
        }
        return ScreenAwakeState.READY;
    }

    public static boolean isRunning(ScreenAwakeState state) {
        return state == ScreenAwakeState.ON_INFINITE
                || state == ScreenAwakeState.ON_TIMER
                || state == ScreenAwakeState.ON_CHARGING;
    }
}
