package com.mylaputa.beleco;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Color;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by dklap on 1/22/2017.
 */

public class MyViewPager extends ViewPager {
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    public MyViewPager(Context context) {
        super(context);
        init();
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // The majority of the magic happens here
        setPageTransformer(true, new PageTransformer());
        // The easiest way to get rid of the overscroll drawing that happens on the left and right
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if (position < 1) {
            getRootView().setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset,
                    Color.argb(153, 35, 35, 35), Color.argb(200, 0, 0, 0)));
        } else {
            getRootView().setBackgroundColor(Color.argb(200, 0, 0, 0));
        }
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    private class PageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
//                view.setAlpha((float) Math.tan(Math.PI / 4f * (1 - Math.abs(position))));
                view.setAlpha(1 - Math.abs(position));
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}