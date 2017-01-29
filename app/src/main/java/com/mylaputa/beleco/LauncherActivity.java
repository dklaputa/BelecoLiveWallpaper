package com.mylaputa.beleco;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class LauncherActivity extends LiveWallPaperPreferenceActivity {
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            LauncherActivity.this.finish();
        }
    };
    private boolean whetherSetWallpaper = true;
    private Dialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.mylaputa.beleco.LauncherActivity.finish");
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Log.d("LauncherActivity", "onStart" + whetherSetWallpaper);
        if (whetherSetWallpaper) {
            WallpaperManager wm = WallpaperManager.getInstance(this);
            if (wm.getWallpaperInfo() == null
                    || !wm.getWallpaperInfo().getPackageName()
                    .equals(this.getPackageName())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                mDialog = builder
                        .setMessage(R.string.wallpaper_chooser_dialog)
                        .setTitle(R.string.wallpaper_chooser_dialog_title)
                        .setNegativeButton(R.string.dialog_Cancel, null)
                        .setPositiveButton(R.string.dialog_OK,
                                new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        if (Build.VERSION.SDK_INT >= 16) {
                                            wallpaperChooser();
                                        } else {
                                            Toast.makeText(
                                                    LauncherActivity.this,
                                                    R.string.toast_2,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).show();
                mDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        whetherSetWallpaper = false;
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.done) {
            wallpaperChooser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT >= 16)
            getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(16)
    private void wallpaperChooser() {
        try {
            startActivity(new Intent(
                    WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(LauncherActivity.this,
                            LiveWallpaperService.class)).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(
                        WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(LauncherActivity.this, R.string.toast_2,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Log.d("LauncherActivity", "onSaveInstanceState" +
        // whetherSetWallpaper);
        if (!whetherSetWallpaper)
            outState.putBoolean("not_set_wallpaper", true);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {

        super.onRestoreInstanceState(outState);
        if (outState.getBoolean("not_set_wallpaper")) {
            whetherSetWallpaper = false;
        }
        // Log.d("LauncherActivity", "onRestoreInstanceState"
        // + whetherSetWallpaper);
    }

}
