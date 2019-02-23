package com.mylaputa.beleco;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mylaputa.beleco.utils.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dklap on 1/22/2017.
 */

public class LiveWallpaperSettingsFragment extends Fragment {
    private final int REQUEST_CHOOSE_PHOTOS = 0;

    private int oldPicture = 0;
    private SharedPreferences.Editor editor;
    private TabLayout tabLayoutPictureChoose;
    private Cube cube;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        TextView textViewIntroduction = (TextView) view.findViewById(R.id.introduction);
        SpannableString spannableString = new SpannableString(Html.fromHtml(getResources()
                .getString(R.string.introduction2)));
        textViewIntroduction.setText(spannableString);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();
        if (Build.VERSION.SDK_INT >= 21) {
            CheckBox checkBoxPowerSaver = (CheckBox) view.findViewById(R.id.checkBoxPower);
            checkBoxPowerSaver.setVisibility(View.VISIBLE);
            checkBoxPowerSaver.setChecked(preferences.getBoolean("power_saver", true));
            checkBoxPowerSaver.setOnCheckedChangeListener(new CompoundButton
                    .OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    editor.putBoolean("power_saver", isChecked);
                    editor.apply();
                }
            });
        }

        tabLayoutPictureChoose = (TabLayout) view.findViewById(R.id.tabLayoutPictureChoose);
        oldPicture = preferences.getInt("default_picture", 0);
        TabLayout.Tab tab = tabLayoutPictureChoose.getTabAt(oldPicture == 0 ? 0 : 1);
        if (tab != null) tab.select();
        tabLayoutPictureChoose.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    Intent intent = Build.VERSION.SDK_INT >= 19 ? new Intent(Intent
                            .ACTION_OPEN_DOCUMENT) : new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, REQUEST_CHOOSE_PHOTOS);
                } else {
                    editor.putInt("default_picture", 0);
                    editor.apply();
                    oldPicture = 0;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    Intent intent = Build.VERSION.SDK_INT >= 19 ? new Intent(Intent
                            .ACTION_OPEN_DOCUMENT) : new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, REQUEST_CHOOSE_PHOTOS);
                }
            }
        });

        SeekBar seekBarRange = (SeekBar) view.findViewById(R.id.seekBarRange);
        seekBarRange.setProgress(preferences.getInt("range", 10));
        seekBarRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    editor.putInt("range", progress);
                    editor.apply();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SeekBar seekBarDelay = (SeekBar) view.findViewById(R.id.seekBarDelay);
        seekBarDelay.setProgress(preferences.getInt("delay", 10));
        seekBarDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    editor.putInt("delay", progress);
                    editor.apply();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        CheckBox checkBoxScroll = (CheckBox) view.findViewById(R.id.checkBoxScroll);
        checkBoxScroll.setChecked(preferences.getBoolean("scroll", true));
        checkBoxScroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("scroll", isChecked);
                editor.apply();
            }
        });
        cube = (Cube) view.findViewById(R.id.cube);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_PHOTOS) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                InputStream in = openUri(data.getData());
                if (in != null) {
                    try {
                        FileOutputStream fos = getActivity().openFileOutput(
                                Constant.CACHE, Context.MODE_PRIVATE);
                        byte[] buffer = new byte[1024];
                        int bytes;
                        while ((bytes = in.read(buffer)) > 0) {
                            fos.write(buffer, 0, bytes);
                        }
                        in.close();
                        fos.flush();
                        fos.close();
                        oldPicture = oldPicture % 2 + 1;
                        editor.putInt("default_picture", oldPicture);
                        editor.apply();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), R.string.toast_failed_set_picture,
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.toast_invalid_pic_path,
                            Toast.LENGTH_LONG).show();
                }
            }
            if (oldPicture == 0) {
                TabLayout.Tab tab = tabLayoutPictureChoose.getTabAt(0);
                if (tab != null) tab.select();
            }
        }
    }

    public InputStream openUri(Uri uri) {
        if (uri == null) return null;
        try {
            return getActivity().getContentResolver().openInputStream(uri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LiveWallpaperRenderer.BiasChangeEvent event) {
        cube.setRotation(event.getY(), event.getX());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
