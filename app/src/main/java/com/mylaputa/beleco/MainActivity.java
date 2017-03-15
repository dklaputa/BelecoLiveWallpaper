package com.mylaputa.beleco;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import com.mylaputa.beleco.sensor.RotationSensor;
import com.mylaputa.beleco.utils.TypefaceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dklap on 1/22/2017.
 */

public class MainActivity extends AppCompatActivity {
    private float biasRange;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private MyViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSectionsPagerAdapter = new SectionsPagerAdapter
                (getSupportFragmentManager());

        mViewPager = (MyViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        biasRange = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources()
                .getDisplayMetrics());
    }


    @Override
    protected void onDestroy() {
        TypefaceUtil.clearCache();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RotationSensor.SensorChangedEvent event) {
        float[] values = event.getAngle();
        if (getResources().getConfiguration().orientation == Configuration
                .ORIENTATION_LANDSCAPE) {
            mViewPager.setTranslationX((float) Math.sin(-values[1]) * biasRange);
            mViewPager.setTranslationY((float) Math.sin(-values[2]) * biasRange);
        } else {
            mViewPager.setTranslationX((float) Math.sin(values[2]) * biasRange);
            mViewPager.setTranslationY((float) Math.sin(-values[1]) * biasRange);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() != 0) mViewPager.setCurrentItem(0, true);
        else super.onBackPressed();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0)
                return new LiveWallpaperSettingsFragment();
            else return new LiveWallpaperAboutFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
