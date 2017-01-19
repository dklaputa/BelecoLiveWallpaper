/**
 *
 */
package com.mylaputa.beleco.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.mylaputa.beleco.R;
import com.mylaputa.beleco.utils.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author dklap_000
 */
public class LiveWallPaperPreferenceFragment extends PreferenceFragment {
    private WallpaperSelectorPreference ws;
    private LauncherIconPreference li;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getPreferenceManager().setSharedPreferencesName(
        // Constant.SHARED_PREFS_NAME);
        addPreferencesFromResource(R.xml.preferences);
        ws = (WallpaperSelectorPreference) findPreference("wallpaperPicker");
        ws.setFragment(this);
        li = (LauncherIconPreference) findPreference("showInLauncher");
    }

    @Override
    public void onDestroy() {
        Log.d("LiveWallPaperPreferenceFragment", "onDestroy");
        // ws.onActivityDestroy();
        // ws = null;
        li.onActivityDestory();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK
                && null != data) {
            // Uri selectedImage = data.getData();
            InputStream in = openUri(data.getData());
            if (in != null) {
                try {
                    FileOutputStream fos = getActivity().openFileOutput(
                            Constant.CACHE, Context.MODE_PRIVATE);
                    byte[] buffer = new byte[in.available()];
                    in.read(buffer);
                    fos.write(buffer);
                    in.close();
                    fos.flush();
                    fos.close();
                    ws.setCustomWallpaperSucceed();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.toast_4,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.toast_1,
                        Toast.LENGTH_LONG).show();
            }
            // String[] filePathColumn = { MediaStore.Images.Media.DATA };
            //
            // Cursor cursor = getActivity().getContentResolver().query(
            // selectedImage, filePathColumn, null, null, null);
            // cursor.moveToFirst();
            //
            // int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            // String mResult = cursor.getString(columnIndex);
            // cursor.close();
            // Log.i("path", mResult);
            //
            // Toast.makeText(getActivity(), mResult,
            // Toast.LENGTH_SHORT).show();
            // if (mResult != null) {
            // ws.setPath(mResult);
            // } else {
            // Toast.makeText(getActivity(), R.string.toast_1,
            // Toast.LENGTH_SHORT).show();
            // }
            // String picturePath contains the path of selected Image
        }
    }

    public InputStream openUri(Uri uri) {
        if (uri == null) {
            Log.e("open", "Uri cannot be empty");
            return null;
        }

        String scheme = uri.getScheme();
        if (scheme == null) {
            Log.e("open", "Uri had no scheme");
            return null;
        }

        // InputStream in = null;
        if ("content".equals(scheme)) {
            try {
                return getActivity().getContentResolver().openInputStream(uri);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        } else if ("file".equals(scheme)) {
            List<String> segments = uri.getPathSegments();
            if (segments != null && segments.size() > 1
                    && "android_asset".equals(segments.get(0))) {
                AssetManager assetManager = getActivity().getAssets();
                StringBuilder assetPath = new StringBuilder();
                for (int i = 1; i < segments.size(); i++) {
                    if (i > 1) {
                        assetPath.append("/");
                    }
                    assetPath.append(segments.get(i));
                }
                try {
                    return assetManager.open(assetPath.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                try {
                    return new FileInputStream(new File(uri.getPath()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } else
            return null;
        // } else if ("http".equals(scheme) || "https".equals(scheme)) {
        // OkHttpClient client = new OkHttpClient();
        // HttpURLConnection conn = null;
        // int responseCode = 0;
        // String responseMessage = null;
        // try {
        // conn = new OkUrlFactory(client).open(new URL(uri.toString()));
        // conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        // conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
        // responseCode = conn.getResponseCode();
        // responseMessage = conn.getResponseMessage();
        // if (!(responseCode >= 200 && responseCode < 300)) {
        // throw new IOException("HTTP error response.");
        // }
        // if (reqContentTypeSubstring != null) {
        // String contentType = conn.getContentType();
        // if (contentType == null
        // || contentType.indexOf(reqContentTypeSubstring) < 0) {
        // throw new IOException("HTTP content type '"
        // + contentType + "' didn't match '"
        // + reqContentTypeSubstring + "'.");
        // }
        // }
        // in = conn.getInputStream();
        //
        // } catch (MalformedURLException e) {
        // throw new OpenUriException(false, e);
        // } catch (IOException e) {
        // if (conn != null && responseCode > 0) {
        // throw new OpenUriException(500 <= responseCode
        // && responseCode < 600, responseMessage, e);
        // } else {
        // throw new OpenUriException(false, e);
        // }
        //
        // }
        // }

    }
    //
    // @Override
    // public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // inflater.inflate(R.menu.ads, menu);
    // super.onCreateOptionsMenu(menu, inflater);
    // }
}
