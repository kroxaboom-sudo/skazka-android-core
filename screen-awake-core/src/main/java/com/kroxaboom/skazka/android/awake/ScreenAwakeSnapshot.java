package com.kroxaboom.skazka.android.awake;

/**
 * RU: Снимок входных условий state machine без зависимости от Android API.
 * EN: State-machine input snapshot with no Android API dependency.
 */
public record ScreenAwakeSnapshot(
        boolean requested,
        boolean savedOriginalTimeout,
        boolean canWriteSettings,
        boolean timeoutApplied,
        boolean notificationsGranted,
        boolean chargingOwned,
        long timerEndAt,
        long now
) {}
