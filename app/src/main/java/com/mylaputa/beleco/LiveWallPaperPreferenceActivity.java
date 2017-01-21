/**
 *
 */
package com.mylaputa.beleco;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;

import com.mylaputa.beleco.utils.CustomTypefaceSpan;
import com.mylaputa.beleco.utils.TypefaceUtil;

/**
 * @author dklap_000
 */
public class LiveWallPaperPreferenceActivity extends AppCompatActivity {

    private static final String TAG = "PreferenceActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final SpannableString spannableString = new SpannableString(
                getResources().getString(R.string.app_setting_title));

        spannableString.setSpan(
                new CustomTypefaceSpan(TypefaceUtil.getAndCache(this,
                        "Roboto-Thin.ttf")), 0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // spannableString.setSpan(new AbsoluteSizeSpan(24, true), 0,
        // spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(spannableString);
        // if (Build.VERSION.SDK_INT >= 21) {
        // tintManager.setNavigationBarTintEnabled(true);
        // tintManager
        // .setNavigationBarTintResource(R.color.actionbar_background_half_transparent);
        // tintManager.setNavigationBarAlpha(1);
        // }
        // if (Build.VERSION.SDK_INT >= 14) {
        // getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        // }

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        // adView.removeAllViews();
        TypefaceUtil.clearCache();
        super.onDestroy();

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
