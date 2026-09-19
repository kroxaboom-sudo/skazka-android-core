package com.kroxaboom.skazka.android.theme;

public final class ThemePalette {
    public final int background, surface, surfaceVariant;
    public final int primary, primaryStrong, secondary, onPrimary;
    public final int onSurface, onSurfaceSecondary;
    public final int error, warning, success, divider, outline, disabled;
    public final int gradientStart, gradientEnd, glow;

    ThemePalette(int background, int surface, int surfaceVariant,
            int primary, int primaryStrong, int secondary, int onPrimary,
            int onSurface, int onSurfaceSecondary, int error, int warning,
            int success, int divider, int outline, int disabled,
            int gradientStart, int gradientEnd, int glow) {
        this.background = background;
        this.surface = surface;
        this.surfaceVariant = surfaceVariant;
        this.primary = primary;
        this.primaryStrong = primaryStrong;
        this.secondary = secondary;
        this.onPrimary = onPrimary;
        this.onSurface = onSurface;
        this.onSurfaceSecondary = onSurfaceSecondary;
        this.error = error;
        this.warning = warning;
        this.success = success;
        this.divider = divider;
        this.outline = outline;
        this.disabled = disabled;
        this.gradientStart = gradientStart;
        this.gradientEnd = gradientEnd;
        this.glow = glow;
    }
}
