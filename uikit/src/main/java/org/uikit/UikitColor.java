package org.uikit;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;

public final class UikitColor {
    // ===== 改动 1：兜底色由绿色改为蓝色系 =====
    public static final int FALLBACK_PRIMARY      = 0xFF1976D2; // Blue 700
    public static final int FALLBACK_ACCENT_LIGHT = 0xFF0D47A1; // Blue 900
    public static final int FALLBACK_ACCENT_DARK  = 0xFF90CAF9; // Blue 200

    private UikitColor() {}

    public static boolean isDynamicColorEnabled(Context ctx) {
        return Build.VERSION.SDK_INT >= 31 && UikitConfig.isDynamicColorEnabled(ctx);
    }
    public static int getSystemColor(Context ctx, String resName, int fallback) {
        try {
            int colorId = ctx.getResources().getIdentifier(resName, "color", "android");
            if (colorId != 0) {
                if (Build.VERSION.SDK_INT >= 23) return ctx.getColor(colorId);
                return ctx.getResources().getColor(colorId);
            }
        } catch (Exception ignored) {}
        return fallback;
    }
    public static int getPrimaryColor(Context ctx) {
        // ===== 改动 2：优先使用用户选择的主题色 =====
        int custom = UikitConfig.getThemeColor(ctx);
        if (custom != 0) return custom;

        if (isDynamicColorEnabled(ctx)) {
            try {
                TypedValue tv = new TypedValue();
                if (ctx.getTheme().resolveAttribute(android.R.attr.colorAccent, tv, true)) {
                    if (tv.resourceId != 0) {
                        if (Build.VERSION.SDK_INT >= 23) return ctx.getColor(tv.resourceId);
                        return ctx.getResources().getColor(tv.resourceId);
                    }
                    if (tv.data != 0) return tv.data;
                }
            } catch (Exception ignored) {}
            int c = getSystemColor(ctx, "system_accent1_600", 0);
            if (c != 0) return c;
        }
        return FALLBACK_PRIMARY;
    }
    public static int getAccentColor(Context ctx, boolean isDark) {
        if (isDynamicColorEnabled(ctx)) {
            return getSystemColor(ctx,
                    isDark ? "system_accent1_200" : "system_accent1_800",
                    isDark ? FALLBACK_ACCENT_DARK : FALLBACK_ACCENT_LIGHT);
        }
        return isDark ? FALLBACK_ACCENT_DARK : FALLBACK_ACCENT_LIGHT;
    }
    public static int getOnPrimaryColor(Context ctx) {
        int p = getPrimaryColor(ctx);
        int r = (p >> 16) & 0xFF, g = (p >> 8) & 0xFF, b = p & 0xFF;
        int y = (r * 299 + g * 587 + b * 114) / 1000;
        return y > 160 ? 0xFF1C1B1F : 0xFFFFFFFF;
    }
    public static int getCardBgColor(Context ctx) {
        return UikitTheme.isDarkMode(ctx) ? 0xFF1E1E1E : 0xFFFFFFFF;
    }
    public static int getCardStrokeColor(Context ctx) {
        return UikitTheme.isDarkMode(ctx) ? 0xFF3E3E3E : 0xFFE0E0E0;
    }
}
