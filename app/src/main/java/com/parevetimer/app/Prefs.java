package com.parevetimer.app;

import android.content.Context;
import android.content.SharedPreferences;

public final class Prefs {
    private static final String FILE = "settings";
    public static final String ACTIVE = "active";
    public static final String CATEGORY = "category";
    public static final String START = "start";
    public static final String END = "end";
    public static final String DURATION = "duration";
    private Prefs() {}

    public static SharedPreferences get(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static long getBeefMs(Context c) {
        return get(c).getLong("beef_ms", 6L * 60L * 60L * 1000L);
    }

    public static long getDairyMs(Context c) {
        return get(c).getLong("dairy_ms", 60L * 60L * 1000L);
    }

    public static String getNotificationMode(Context c) {
        return get(c).getString("notification_mode", "system");
    }

    public static boolean isOnboarded(Context c) {
        return get(c).getBoolean("onboarded", false);
    }

    public static void setTimes(Context c, long beefMs, long dairyMs) {
        get(c).edit().putLong("beef_ms", beefMs).putLong("dairy_ms", dairyMs).apply();
    }
}
