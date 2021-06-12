package com.batache.wootch.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DeviceUtils {

  public final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

  public static boolean isQ() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
  }

  public static boolean isPie() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
  }

  public static boolean isOreo() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
  }

  public static boolean isNextNougat() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1;
  }

  public static boolean isNougat() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
  }

  public static boolean isMarshmallow() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
  }

  public static boolean isNextLollipop() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
  }

  public static boolean isLollipop() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
  }

  private static File getExternalFilesDir(Context context) {
    return context.getExternalFilesDir(null);
  }

  private static File getExternalFilesSongsDir(Context context) {
    return new File(getExternalFilesDir(context), "songs");
  }

  private static File getExternalFilesSongsDirFilePath(Context context, String filename) {
    return new File(getExternalFilesSongsDir(context), filename);
  }

  private static File getInternalFilesDir(Context context) {
    return context.getFilesDir();
  }

  private static File getInternalFilesSongsDir(Context context) {
    return new File(getInternalFilesDir(context), "songs");
  }

  private static File getInternalFilesSongsDirFilePath(Context context, String filename) {
    return new File(getInternalFilesSongsDir(context), filename);
  }

  public static File getDownloadSongsDir(Context context) {
    String songDownloadDir = PreferenceManager.getDefaultSharedPreferences(context).getString("download_location", "in_app");
    if ("in_app".equals(songDownloadDir)) {
      return getInternalFilesSongsDir(context);
    }
    return getExternalFilesSongsDir(context);
  }

  public static File getDownloadSongsDirFilePath(Context context, String fileName) {
    String songDownloadDir = PreferenceManager.getDefaultSharedPreferences(context).getString("download_location", "in_app");
    if ("in_app".equals(songDownloadDir)) {
      return getInternalFilesSongsDirFilePath(context, fileName);
    }
    return getExternalFilesSongsDirFilePath(context, fileName);
  }

  public static File getInternalCacheDir(Context context) {
    return context.getCacheDir();
  }

  public static File getInternalCacheDirFilePath(Context context, String filename) {
    return new File(getInternalCacheDir(context), filename);
  }

  public static boolean areFilesSame(String file_url, File theFile) throws IOException {
    long lastModified = theFile.lastModified();
    boolean are_the_same = false;
    try {
      URL url = new URL(file_url);
      URLConnection connection = url.openConnection();
      connection.connect();
      if (lastModified != 0) {

        are_the_same = (connection.getLastModified() < lastModified);

      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    return are_the_same;
  }

  public static void cacheData(Context context, String sharedPreferencesKey, String cache) {
    SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor prefsEditor = mPrefs.edit();
    prefsEditor.putString(sharedPreferencesKey, cache);
    prefsEditor.apply();
  }

  public static String getCachedData(Context context, String sharedPreferencesName) {
    SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    return mPrefs.getString(sharedPreferencesName, null);
  }

}
