package com.batache.wootch.app.home.stream;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.batache.wootch.MobileButterApplication;
import com.batache.wootch.R;
import com.batache.wootch.app.base.ButterBaseActivity;
import com.batache.wootch.ui.view.OptionDialogFragment;

import butter.droid.base.beaming.BeamManager;
import butter.droid.base.torrent.StreamInfo;
import butter.droid.base.torrent.TorrentService;

public class BeamPlayerActivity extends ButterBaseActivity implements VideoPlayerFragment.Callback {

  private BeamPlayerFragment mFragment;
  private BeamManager mBeamManager = BeamManager.getInstance(this);
  private StreamInfo mStreamInfo;
  private Long mResumePosition;
  private String mTitle;

  public static Intent startActivity(Context context, @NonNull StreamInfo info) {
    return startActivity(context, info, 0);
  }

  public static Intent startActivity(Context context, @NonNull StreamInfo info, long resumePosition) {
    Intent i = new Intent(context, BeamPlayerActivity.class);

    if (info == null) {
      throw new IllegalArgumentException("StreamInfo must not be null");
    }

    i.putExtra(INFO, info);
    i.putExtra(RESUME_POSITION, resumePosition);
    context.startActivity(i);
    return i;
  }

  public final static String INFO = "stream_info";
  public final static String RESUME_POSITION = "resume_position";

  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public int getLayout() {
    return R.layout.activity_beamplayer;
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  @Override
  public void onCreate(Bundle savedInstanceState) {
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    MobileButterApplication.getAppContext()
        .getComponent()
        .inject(this);

    super.onCreate(savedInstanceState);

    setShowCasting(true);

    mStreamInfo = getIntent().getParcelableExtra(INFO);

    mResumePosition = getIntent().getLongExtra(RESUME_POSITION, 0);

    mTitle = mStreamInfo.getTitle() == null ? getString(R.string.the_video) : mStreamInfo.getTitle();

        /*
        File subsLocation = new File(SubsProvider.getStorageLocation(context), media.videoId + "-" + subLanguage + ".srt");
        BeamServer.setCurrentSubs(subsLocation);
         */

    mFragment = (BeamPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.beam_fragment);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (null != mService && mService.checkStopped())
      finish();
  }

  @Override
  public void onApplyAllWindowInsets() {

  }

  @Override
  protected void onStop() {
    if (null != mService)
      mService.removeListener(mFragment);
    super.onStop();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        showExitDialog();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    showExitDialog();
  }

  private void showExitDialog() {
    OptionDialogFragment.show(getSupportFragmentManager(), getString(R.string.leave_videoplayer_title), String.format(getString(R.string.leave_videoplayer_message), mTitle), getString(android.R.string.yes), getString(android.R.string.no), new OptionDialogFragment.Listener() {
      @Override
      public void onSelectionPositive() {
        mBeamManager.stopVideo();
        if (mService != null)
          mService.stopStreaming();
        finish();
      }

      @Override
      public void onSelectionNegative() {
      }
    });
  }

  @Override
  public StreamInfo getInfo() {
    return mStreamInfo;
  }

  @Override
  public TorrentService getService() {
    return mService;
  }

  public Long getResumePosition() {
    return mResumePosition;
  }

  @Override
  public void onTorrentServiceConnected() {
    super.onTorrentServiceConnected();

    if (mService.checkStopped()) {
      finish();
      return;
    }

    mService.addListener(mFragment);
  }
}
