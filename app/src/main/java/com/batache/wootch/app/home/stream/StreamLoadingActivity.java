package com.batache.wootch.app.home.stream;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.batache.wootch.MobileButterApplication;
import com.batache.wootch.R;
import com.batache.wootch.app.base.ButterBaseActivity;
import com.batache.wootch.util.TextUtils;

import butter.droid.base.torrent.StreamInfo;

public class StreamLoadingActivity extends ButterBaseActivity implements StreamLoadingFragment.FragmentListener {

  public final static String EXTRA_INFO = "mInfo";

  private StreamInfo mInfo;
  private StreamLoadingFragment mFragment;

  public static Intent startActivity(Activity activity, StreamInfo info) {
    Intent i = new Intent(activity, StreamLoadingActivity.class);
    i.putExtra(EXTRA_INFO, info);
    activity.startActivity(i);
    return i;
  }

  public static Intent startActivity(Activity activity, StreamInfo info, Pair<View, String>... elements) {
    Intent i = new Intent(activity, StreamLoadingActivity.class);
    i.putExtra(EXTRA_INFO, info);

    ActivityOptionsCompat options =
        ActivityOptionsCompat.makeSceneTransitionAnimation(activity, elements);
    ActivityCompat.startActivity(activity, i, options.toBundle());
    return i;
  }

  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public int getLayout() {
    return R.layout.activity_streamloading;
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  @Override
  public void onCreate(Bundle savedInstanceState) {
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    getWindow().setBackgroundDrawableResource(R.color.colorBackground);

    MobileButterApplication.getAppContext()
        .getComponent()
        .inject(this);

    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    if (!getIntent().hasExtra(EXTRA_INFO)) finish();

    mInfo = getIntent().getParcelableExtra(EXTRA_INFO);

    mFragment = (StreamLoadingFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

    mFragment.setTitle(TextUtils.getTitle(mInfo.getMedia()));
  }

  @Override
  public void onApplyAllWindowInsets() {

  }

  @Override
  public void onTorrentServiceConnected() {
    super.onTorrentServiceConnected();
    if (null != mFragment) {
      mFragment.onTorrentServiceConnected();
    }
  }

  @Override
  public void onTorrentServiceDisconnected() {
    super.onTorrentServiceDisconnected();
    if (null != mFragment) {
      mFragment.onTorrentServiceDisconnected();
    }
  }

  @Override
  public StreamInfo getStreamInformation() {
    return mInfo;
  }

  @Override
  public void onBackPressed() {
    if (mFragment != null) {
      mFragment.cancelStream();
    }
    super.onBackPressed();
  }
}