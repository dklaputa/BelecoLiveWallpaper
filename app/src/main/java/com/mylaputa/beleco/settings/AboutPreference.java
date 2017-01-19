package com.mylaputa.beleco.settings;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.DialogPreference;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.mylaputa.beleco.R;
import com.mylaputa.beleco.utils.CustomTypefaceSpan;
import com.mylaputa.beleco.utils.TypefaceUtil;

class AboutPreference extends DialogPreference {
    private final Context mContext;

    public AboutPreference(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public AboutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onBindView(View view) {
        final String appName = mContext.getResources().getString(
                R.string.setting3_title);
        String ver = "v";
        try {
            ver += mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final SpannableString spannableString = new SpannableString(appName
                + " " + ver);
        spannableString.setSpan(
                new CustomTypefaceSpan(TypefaceUtil.getAndCache(mContext,
                        "Roboto-Thin.ttf")), 0, appName.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setTitle(spannableString);
        super.onBindView(view);
    }

    // @Override
    // protected void onClick() {
    //
    // final View view = View.inflate(mContext, R.layout.about, null);
    // final TextView aboutTextView = (TextView) view
    // .findViewById(R.id.textView);
    // aboutTextView.setTypeface(TypefaceUtil.getAndCache(mContext,
    // "RobotoCondensed-Regular.ttf"));
    // final SpannableString spannableString = new SpannableString(
    // Html.fromHtml(mContext.getResources().getString(R.string.about)));
    // spannableString.setSpan(
    // new CustomTypefaceSpan(TypefaceUtil.getAndCache(mContext,
    // "Roboto-Thin.ttf")), 17, 23,
    // Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    // aboutTextView.setText(spannableString);
    //
    // aboutTextView.setMovementMethod(new LinkMovementMethod());
    // new AlertDialog.Builder(mContext).setView(view).show();
    //
    // }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);
        final View view = View.inflate(mContext, R.layout.about, null);
        final TextView aboutTextView = (TextView) view
                .findViewById(R.id.textView);
        aboutTextView.setTypeface(TypefaceUtil.getAndCache(mContext,
                "RobotoCondensed-Regular.ttf"));
        final SpannableString spannableString = new SpannableString(
                Html.fromHtml(mContext.getResources().getString(R.string.about)));
        // spannableString.setSpan(
        // new CustomTypefaceSpan(TypefaceUtil.getAndCache(mContext,
        // "Roboto-Thin.ttf")), 17, 23,
        // Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        aboutTextView.setText(spannableString);

        aboutTextView.setMovementMethod(new LinkMovementMethod());
        builder.setView(view).setTitle(null).setPositiveButton(null, null)
                .setNegativeButton(null, null);
    }

}
