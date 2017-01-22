package com.mylaputa.beleco;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mylaputa.beleco.utils.CustomTypefaceSpan;
import com.mylaputa.beleco.utils.TypefaceUtil;

/**
 * Created by dklap on 1/22/2017.
 */

public class LiveWallpaperSettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.settings, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //What to do on back clicked
//            }
//        });

        final SpannableString spannableString = new SpannableString(
                getResources().getString(R.string.app_setting_title));

        spannableString.setSpan(
                new CustomTypefaceSpan(TypefaceUtil.getAndCache(getContext(),
                        "Roboto-Thin.ttf")), 0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // spannableString.setSpan(new AbsoluteSizeSpan(24, true), 0,
        // spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toolbar.setTitle(spannableString);


        toolbar.inflateMenu(R.menu.menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
