package com.parevetimer.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.content.Intent;

public class SettingsActivity extends Activity {
    protected boolean onboardingMode() { return false; }

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_settings);

        NumberPicker bh = findViewById(R.id.beef_hours);
        NumberPicker bm = findViewById(R.id.beef_minutes);
        NumberPicker dh = findViewById(R.id.dairy_hours);
        NumberPicker dm = findViewById(R.id.dairy_minutes);

        long beef = Prefs.getBeefMs(this);
        long dairy = Prefs.getDairyMs(this);
        setPicker(bh, (int)(beef / 3600000L), 0, 24);
        setPicker(bm, (int)((beef / 60000L) % 60), 0, 59);
        setPicker(dh, (int)(dairy / 3600000L), 0, 24);
        setPicker(dm, (int)((dairy / 60000L) % 60), 0, 59);

        RadioGroup g = findViewById(R.id.notification_group);
        g.check("vibrate".equals(Prefs.getNotificationMode(this)) ? R.id.radio_vibrate :
                "custom1".equals(Prefs.getNotificationMode(this)) ? R.id.radio_custom1 :
                "custom2".equals(Prefs.getNotificationMode(this)) ? R.id.radio_custom2 :
                R.id.radio_system);

        findViewById(R.id.save_button).setOnClickListener(v -> {
            long bms = (bh.getValue() * 60L + bm.getValue()) * 60000L;
            long dms = (dh.getValue() * 60L + dm.getValue()) * 60000L;
            if (bms <= 0 || dms < 0) {
                Toast.makeText(this, "נא לבחור זמן תקין", Toast.LENGTH_SHORT).show();
                return;
            }
            String selected = g.getCheckedRadioButtonId() == R.id.radio_vibrate ? "vibrate" :
                    g.getCheckedRadioButtonId() == R.id.radio_custom1 ? "custom1" :
                    g.getCheckedRadioButtonId() == R.id.radio_custom2 ? "custom2" : "system";

            Prefs.setTimes(this, bms, dms);
            Prefs.get(this).edit()
                    .putString("notification_mode", selected)
                    .putBoolean("onboarded", true)
                    .apply();

            Toast.makeText(this, "ההגדרות נשמרו", Toast.LENGTH_SHORT).show();
            if (onboardingMode()) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                finish();
            }
        });

        if (onboardingMode()) {
            ((TextView)findViewById(R.id.settings_title)).setText("בוא נגדיר את ההמתנה");
            ((TextView)findViewById(R.id.settings_intro)).setText("בחר את הזמנים שנוחים לך — אחר כך לחיצה אחת תפעיל את הטיימר");
        }
    }

    private void setPicker(NumberPicker p, int value, int min, int max) {
        p.setMinValue(min);
        p.setMaxValue(max);
        p.setValue(Math.max(min, Math.min(max, value)));
        p.setWrapSelectorWheel(true);
        p.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }
}