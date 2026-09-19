package com.kroxaboom.skazka.android.theme;

public final class SkazkaThemeCore {
    public static final String SYSTEM = "system";
    public static final String LIGHT = "light";
    public static final String DARK = "dark";
    public static final String OLED = "oled";

    private static final ThemePalette LIGHT_PALETTE = new ThemePalette(
            0xfff4f6f8, 0xffffffff, 0xffe9eef5,
            0xff6f4fd4, 0xff5332b8, 0xff356fdb, 0xffffffff,
            0xff171a1f, 0xff5f6670, 0xffc9344a, 0xff9a6500,
            0xff167c5b, 0xffd7dbe3, 0xffc9ced8, 0xff8d93a0,
            0xff7d5ce0, 0xff4f82e5, 0x336f4fd4);

    private static final ThemePalette SIGNATURE_DARK_PALETTE = new ThemePalette(
            0xff0b0b13, 0xff131522, 0xff1b1f30,
            0xff8e6af1, 0xff633acc, 0xff5b8cff, 0xffffffff,
            0xfff4f2ff, 0xffb8b5c9, 0xffff6b7a, 0xfff0be5a,
            0xff4fd6a4, 0xff242738, 0xff34384a, 0xff6d7080,
            0xff8e6af1, 0xff5b8cff, 0x668e6af1);

    private static final ThemePalette SIGNATURE_OLED_PALETTE = new ThemePalette(
            0xff000000, 0xff050508, 0xff0e1019,
            0xff9a7af5, 0xff6a43d4, 0xff6695ff, 0xffffffff,
            0xfffbfaff, 0xffc0bdcf, 0xffff7180, 0xfff2c56a,
            0xff57dcae, 0xff171925, 0xff25283a, 0xff707383,
            0xff9a7af5, 0xff6695ff, 0x668e6af1);

    private SkazkaThemeCore() {}

    public static ThemePalette resolve(String mode, boolean systemDark) {
        if (OLED.equals(mode)) return SIGNATURE_OLED_PALETTE;
        if (DARK.equals(mode)) return SIGNATURE_DARK_PALETTE;
        if (LIGHT.equals(mode)) return LIGHT_PALETTE;
        return systemDark ? SIGNATURE_DARK_PALETTE : LIGHT_PALETTE;
    }

    public static boolean validMode(String mode) {
        return SYSTEM.equals(mode) || LIGHT.equals(mode)
                || DARK.equals(mode) || OLED.equals(mode);
    }

    public static boolean signature(String mode, boolean systemDark) {
        return OLED.equals(mode) || DARK.equals(mode)
                || (SYSTEM.equals(mode) && systemDark);
    }
}
