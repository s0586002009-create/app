package com.parevetimer.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MainActivity extends Activity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (!Prefs.isOnboarded(this)) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        }

        TimerManager.restore(this);

        if (TimerManager.isActive(this)) {
            openTimer();
            return;
        }

        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 700);
        }

        findViewById(R.id.beef_card).setOnClickListener(v -> startTimer(TimerManager.BEEF));
        findViewById(R.id.dairy_card).setOnClickListener(v -> startTimer(TimerManager.DAIRY));
        findViewById(R.id.settings_button).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void startTimer(String category) {
        TimerManager.start(this, category);
        openTimer();
    }

    private void openTimer() {
        Intent i = new Intent(this, TimerActivity.class);
        i.putExtra(TimerActivity.EXTRA_CATEGORY,
                Prefs.get(this).getString(Prefs.CATEGORY, TimerManager.BEEF));
        startActivity(i);
    }
}