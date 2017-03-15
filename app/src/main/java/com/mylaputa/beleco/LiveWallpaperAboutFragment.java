package com.mylaputa.beleco;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mylaputa.beleco.utils.CustomTypefaceSpan;
import com.mylaputa.beleco.utils.TypefaceUtil;

/**
 * Created by dklap on 1/22/2017.
 */

public class LiveWallpaperAboutFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        final TextView aboutTextView = (TextView) view
                .findViewById(R.id.textView);
        aboutTextView.setTypeface(TypefaceUtil.getAndCache(getContext(),
                "RobotoCondensed-Regular.ttf"));
        final SpannableString spannableString = new SpannableString(
                Html.fromHtml(getContext().getResources().getString(R.string.about)));
        spannableString.setSpan(
                new CustomTypefaceSpan(TypefaceUtil.getAndCache(getContext(),
                        "Roboto-Thin.ttf"), 50), 0, 21,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        aboutTextView.setText(spannableString);

        aboutTextView.setMovementMethod(new LinkMovementMethod());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
