package com.parevetimer.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimerAlarmReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (!Prefs.get(context).getBoolean(Prefs.ACTIVE, false)) return;
        long end = Prefs.get(context).getLong(Prefs.END, 0L);
        if (end > 0 && System.currentTimeMillis() + 1000L >= end) {
            Prefs.get(context).edit().putBoolean(Prefs.ACTIVE, false).apply();
            NotificationHelper.notifyFinished(context);
        } else {
            TimerManager.restore(context);
        }
    }
}