package com.mylaputa.beleco;

import android.app.WallpaperManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by dklap on 1/22/2017.
 */

public class MainActivity extends AppCompatActivity {
    boolean intro;
    WallpaperManager wm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wm = WallpaperManager.getInstance(this);
        if (savedInstanceState == null) {
            if (wm.getWallpaperInfo() == null || !wm.getWallpaperInfo().getPackageName().equals(this
                    .getPackageName())) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new
                        IntroductionFragment()).commit();
                intro = true;
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new
                        MainFragment()).commit();
                intro = false;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("intro", intro);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        intro = savedInstanceState.getBoolean("intro");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (intro && wm.getWallpaperInfo() != null && wm.getWallpaperInfo().getPackageName()
                .equals(this.getPackageName())) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim
                    .fragment_enter, R.anim.fragment_exit).replace(R.id.container, new
                    MainFragment()).commit();
            intro = false;
        } else if (!intro && (wm.getWallpaperInfo() == null || !wm.getWallpaperInfo()
                .getPackageName().equals(this.getPackageName()))) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new
                    IntroductionFragment()).commit();
            intro = true;
        }
    }
}