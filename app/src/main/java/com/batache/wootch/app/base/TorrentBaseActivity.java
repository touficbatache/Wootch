package com.batache.wootch.app.base;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstreamserver.TorrentServerListener;

import butter.droid.base.ButterApplication;
import butter.droid.base.activities.TorrentActivity;
import butter.droid.base.content.preferences.Prefs;
import butter.droid.base.torrent.TorrentService;
import butter.droid.base.utils.LocaleUtils;
import butter.droid.base.utils.PrefUtils;
import butterknife.ButterKnife;

public abstract class TorrentBaseActivity extends BaseActivity implements TorrentServerListener, TorrentActivity {

  protected Handler mHandler;
  protected TorrentService mService;

  protected void onCreate(Bundle savedInstanceState) {
    String language = PrefUtils.get(this, Prefs.LOCALE, ButterApplication.getSystemLanguage());
    LocaleUtils.setCurrent(this, LocaleUtils.toLocale(language));
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);
    ButterKnife.setDebug(true);
    mHandler = new Handler(getMainLooper());
  }

  @Override
  protected void onStart() {
    super.onStart();
    TorrentService.bindHere(this, mServiceConnection);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (null != mService) {
      mService.addListener(this);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (null != mService) {
      mService.removeListener(this);
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (null != mService) {
      unbindService(mServiceConnection);
      mService = null;
    }
  }

  @Override
  public void setContentView(int layoutResID) {
    String language = PrefUtils.get(this, Prefs.LOCALE, ButterApplication.getSystemLanguage());
    LocaleUtils.setCurrent(this, LocaleUtils.toLocale(language));
    super.setContentView(layoutResID);
  }

  protected ButterApplication getApp() {
    return (ButterApplication) getApplication();
  }

  public TorrentService getTorrentService() {
    return mService;
  }

  public void onTorrentServiceConnected() {
    // Placeholder
  }

  public void onTorrentServiceDisconnected() {
    // Placeholder
  }

  private ServiceConnection mServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mService = ((TorrentService.ServiceBinder) service).getService();
      mService.addListener(TorrentBaseActivity.this);
      mService.setCurrentActivity(TorrentBaseActivity.this);
      onTorrentServiceConnected();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mService.removeListener(TorrentBaseActivity.this);
      mService = null;
      onTorrentServiceDisconnected();
    }
  };

  @Override
  public void onStreamPrepared(Torrent torrent) {

  }

  @Override
  public void onStreamStarted(Torrent torrent) {

  }

  @Override
  public void onStreamReady(Torrent torrent) {

  }

  @Override
  public void onStreamError(Torrent torrent, Exception e) {

  }

  @Override
  public void onStreamProgress(Torrent torrent, StreamStatus streamStatus) {

  }

  @Override
  public void onStreamStopped() {

  }

  @Override
  public void onServerReady(String url) {

  }
}
