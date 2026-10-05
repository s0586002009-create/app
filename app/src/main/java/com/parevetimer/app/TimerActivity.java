package com.parevetimer.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class TimerActivity extends Activity {
    public static final String EXTRA_CATEGORY = "category";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView categoryText, countdown, percent, finishedText;
    private ProgressRingView ring;
    private String category;

    private final Runnable tick = new Runnable() {
        @Override public void run() {
            updateUi();
            if (!isFinishing()) handler.postDelayed(this, 500);
        }
    };

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_timer);

        categoryText = findViewById(R.id.timer_category);
        countdown = findViewById(R.id.countdown_text);
        percent = findViewById(R.id.progress_percent);
        finishedText = findViewById(R.id.finished_text);
        ring = findViewById(R.id.progress_ring);

        category = getIntent().getStringExtra(EXTRA_CATEGORY);
        if (category == null) category = Prefs.get(this).getString(Prefs.CATEGORY, TimerManager.BEEF);

        findViewById(R.id.cancel_button).setOnClickListener(v -> {
            TimerManager.cancel(this);
            finish();
        });
        findViewById(R.id.home_button).setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        });

        updateUi();
    }

    @Override protected void onResume() {
        super.onResume();
        handler.post(tick);
    }

    @Override protected void onPause() {
        handler.removeCallbacks(tick);
        super.onPause();
    }

    private void updateUi() {
        boolean active = TimerManager.isActive(this);
        categoryText.setText(TimerManager.BEEF.equals(category) ? "בשרי" : "חלבי");

        if (!active) {
            ring.setProgress(0);
            countdown.setText("00:00:00");
            percent.setVisibility(View.GONE);
            finishedText.setVisibility(View.VISIBLE);
            findViewById(R.id.cancel_button).setVisibility(View.GONE);
            return;
        }

        long start = Prefs.get(this).getLong(Prefs.START, System.currentTimeMillis());
        long end = Prefs.get(this).getLong(Prefs.END, System.currentTimeMillis());
        long duration = Math.max(1, Prefs.get(this).getLong(Prefs.DURATION, end - start));
        long remaining = Math.max(0, end - System.currentTimeMillis());
        float progress = remaining / (float) duration;

        countdown.setText(hms(remaining));
        ring.setProgress(progress);
        percent.setText(String.format(Locale.ROOT, "%d%% נשארו", Math.round(progress * 100)));
        percent.setVisibility(View.VISIBLE);
        finishedText.setVisibility(View.GONE);
        findViewById(R.id.cancel_button).setVisibility(View.VISIBLE);
    }

    private String hms(long ms) {
        long t = (ms + 999) / 1000;
        return String.format(Locale.ROOT, "%02d:%02d:%02d", t / 3600, (t % 3600) / 60, t % 60);
    }
}