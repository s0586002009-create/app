package com.parevetimer.app;

import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class DairyTileService extends TileService {
    @Override public void onStartListening() { refresh(); }
    @Override public void onClick() {
        super.onClick();
        TimerManager.start(getApplicationContext(), TimerManager.DAIRY);
        refresh();
    }
    private void refresh() {
        Tile t = getQsTile();
        if (t == null) return;
        t.setLabel("חלבי");
        t.setIcon(Icon.createWithResource(this, R.drawable.ic_app_icon));
        t.setState(TimerManager.DAIRY.equals(Prefs.get(this).getString(Prefs.CATEGORY, ""))
                && TimerManager.isActive(this) ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        t.updateTile();
    }
}