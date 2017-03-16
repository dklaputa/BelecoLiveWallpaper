package com.mylaputa.beleco;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dklap on 3/16/2017.
 */

public class MainFragment extends Fragment {
    private float biasRange;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private MyViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        MyViewPager mViewPager = (MyViewPager) inflater.inflate(R.layout.fragment_main,
                container, false);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter
                (getChildFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        biasRange = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources()
                .getDisplayMetrics());
        return mViewPager;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LiveWallpaperRenderer.BiasChangeEvent event) {
        mViewPager.setTranslationX(-event.getX() * biasRange);
        mViewPager.setTranslationY(-event.getY() * biasRange);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public boolean onBackPressed() {
        if (mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(0, true);
            return true;
        } else return false;
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
