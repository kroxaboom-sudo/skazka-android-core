package com.kroxaboom.skazka.android.awake.system;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

/**
 * RU: Меняет системный SCREEN_OFF_TIMEOUT и гарантирует возможность вернуть
 * пользовательское значение, сохранённое до первого включения режима.
 *
 * EN: Changes the system SCREEN_OFF_TIMEOUT while preserving the user's
 * pre-existing value so it can be restored later.
 */
public final class SystemScreenTimeoutController {
    public static final int KEEP_AWAKE_TIMEOUT_MS = Integer.MAX_VALUE;

    private static final String KEY_ORIGINAL_TIMEOUT = "original_screen_timeout";
    private static final String KEY_ORIGINAL_SAVED = "original_screen_timeout_saved";
    private static final String KEY_LAST_ORIGINAL_TIMEOUT = "last_original_screen_timeout";

    private final Context context;
    private final SharedPreferences preferences;

    public SystemScreenTimeoutController(Context context, String preferencesName) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        if (preferencesName == null || preferencesName.trim().isEmpty()) {
            throw new IllegalArgumentException("Preferences name must not be empty");
        }

        this.context = context.getApplicationContext();
        this.preferences = this.context.getSharedPreferences(
                preferencesName.trim(),
                Context.MODE_PRIVATE
        );
    }

    public boolean canWrite() {
        return Settings.System.canWrite(context);
    }

    @SuppressLint("ApplySharedPref")
    public boolean enable() {
        if (!canWrite()) {
            return false;
        }

        /*
         * RU:
         * Сначала синхронно фиксируем исходное значение, и только потом меняем системную настройку.
         * Иначе аварийное завершение между этими операциями оставит приложение без точки восстановления.
         *
         * EN:
         * Persist the original value synchronously before changing the system setting.
         * Otherwise a crash between the two operations could leave no reliable restore point.
         */
        if (!hasSavedOriginal()) {
            int current = currentTimeout();
            int fallback = preferences.getInt(KEY_LAST_ORIGINAL_TIMEOUT, 30_000);
            int original = current > 0 ? current : fallback;

            boolean saved = preferences.edit()
                    .putInt(KEY_ORIGINAL_TIMEOUT, original)
                    .putInt(KEY_LAST_ORIGINAL_TIMEOUT, original)
                    .putBoolean(KEY_ORIGINAL_SAVED, true)
                    .commit();
            if (!saved) {
                return false;
            }
        }

        boolean written = Settings.System.putInt(
                context.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT,
                KEEP_AWAKE_TIMEOUT_MS
        );
        return written && isApplied();
    }

    public RestoreResult restore() {
        if (!hasSavedOriginal()) {
            return new RestoreResult(true, -1, currentTimeout());
        }

        int original = savedOriginalTimeout();
        if (!canWrite()) {
            return new RestoreResult(false, original, currentTimeout());
        }

        boolean written = Settings.System.putInt(
                context.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT,
                original
        );
        int actual = currentTimeout();
        boolean restored = written && actual == original;

        if (restored) {
            preferences.edit()
                    .remove(KEY_ORIGINAL_TIMEOUT)
                    .remove(KEY_ORIGINAL_SAVED)
                    .apply();
        }

        return new RestoreResult(restored, original, actual);
    }

    public boolean hasSavedOriginal() {
        return preferences.getBoolean(KEY_ORIGINAL_SAVED, false);
    }

    public int savedOriginalTimeout() {
        if (!hasSavedOriginal()) {
            return -1;
        }
        return preferences.getInt(KEY_ORIGINAL_TIMEOUT, 30_000);
    }

    public int lastOriginalTimeout() {
        if (hasSavedOriginal()) {
            return preferences.getInt(KEY_ORIGINAL_TIMEOUT, 30_000);
        }
        return preferences.getInt(KEY_LAST_ORIGINAL_TIMEOUT, -1);
    }

    public int currentTimeout() {
        return Settings.System.getInt(
                context.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT,
                -1
        );
    }

    public boolean isApplied() {
        return currentTimeout() == KEEP_AWAKE_TIMEOUT_MS;
    }

    public record RestoreResult(boolean restored, int expectedTimeout, int actualTimeout) {}
}
