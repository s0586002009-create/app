package com.parevetimer.app;

import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class BeefTileService extends TileService {
    @Override public void onStartListening() { refresh(); }
    @Override public void onClick() {
        super.onClick();
        TimerManager.start(getApplicationContext(), TimerManager.BEEF);
        refresh();
    }
    private void refresh() {
        Tile t = getQsTile();
        if (t == null) return;
        t.setLabel("בשרי");
        t.setIcon(Icon.createWithResource(this, R.drawable.ic_app_icon));
        t.setState(TimerManager.BEEF.equals(Prefs.get(this).getString(Prefs.CATEGORY, ""))
                && TimerManager.isActive(this) ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        t.updateTile();
    }
}