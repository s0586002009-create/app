package com.parevetimer.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public final class TimerManager {
    public static final String BEEF = "beef";
    public static final String DAIRY = "dairy";
    private static final int REQUEST_ALARM = 7193;
    private TimerManager() {}

    public static void start(Context context, String category) {
        Context app = context.getApplicationContext();
        long duration = BEEF.equals(category) ? Prefs.getBeefMs(app) : Prefs.getDairyMs(app);
        long start = System.currentTimeMillis();
        long end = start + duration;
        Prefs.get(app).edit()
                .putBoolean(Prefs.ACTIVE, true)
                .putString(Prefs.CATEGORY, category)
                .putLong(Prefs.START, start)
                .putLong(Prefs.END, end)
                .putLong(Prefs.DURATION, duration)
                .apply();
        scheduleAlarm(app, end);
    }

    public static void cancel(Context context) {
        Context app = context.getApplicationContext();
        AlarmManager am = (AlarmManager) app.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = alarmIntent(app);
        if (am != null) am.cancel(pi);
        Prefs.get(app).edit()
                .putBoolean(Prefs.ACTIVE, false)
                .remove(Prefs.CATEGORY).remove(Prefs.START)
                .remove(Prefs.END).remove(Prefs.DURATION).apply();
    }

    public static boolean isActive(Context context) {
        long end = Prefs.get(context).getLong(Prefs.END, 0L);
        return Prefs.get(context).getBoolean(Prefs.ACTIVE, false) && System.currentTimeMillis() < end;
    }

    private static void scheduleAlarm(Context c, long triggerAt) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        PendingIntent pi = alarmIntent(c);
        am.cancel(pi);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && am.canScheduleExactAlarms()) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            }
        } catch (SecurityException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            }
        }
    }

    public static void restore(Context c) {
        if (!Prefs.get(c).getBoolean(Prefs.ACTIVE, false)) return;
        long end = Prefs.get(c).getLong(Prefs.END, 0L);
        if (end <= 0) return;
        if (System.currentTimeMillis() >= end) {
            NotificationHelper.notifyFinished(c);
            Prefs.get(c).edit().putBoolean(Prefs.ACTIVE, false).apply();
            return;
        }
        scheduleAlarm(c, end);
    }

    private static PendingIntent alarmIntent(Context c) {
        Intent i = new Intent(c, TimerAlarmReceiver.class);
        return PendingIntent.getBroadcast(c, REQUEST_ALARM, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}