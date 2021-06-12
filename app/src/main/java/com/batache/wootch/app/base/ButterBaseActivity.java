package com.batache.wootch.app.base;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.FileProvider;

import com.batache.wootch.BuildConfig;
import com.batache.wootch.R;

import java.io.File;

import javax.inject.Inject;

import butter.droid.base.ButterApplication;
import butter.droid.base.beaming.BeamManager;
import butter.droid.base.content.preferences.Prefs;
import butter.droid.base.manager.updater.ButterUpdateManager;
import butter.droid.base.utils.LocaleUtils;
import butter.droid.base.utils.PrefUtils;
import butter.droid.base.utils.VersionUtils;
import butter.droid.base.vpn.VPNManager;

public abstract class ButterBaseActivity extends TorrentBaseActivity implements BeamManager.BeamListener, VPNManager.Listener {

  @Inject
  ButterUpdateManager updateManager;

  protected Boolean mShowCasting = false;
  protected VPNManager mVPNManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!VersionUtils.isUsingCorrectBuild()) {
      new AlertDialog.Builder(this)
          .setMessage(butter.droid.base.R.string.wrong_abi)
          .setCancelable(false)
          .show();


      updateManager.setListener(new ButterUpdateManager.Listener() {
        @Override
        public void updateAvailable(String updateFile) {
          Uri uri = FileProvider.getUriForFile(ButterBaseActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(updateFile));
          Intent installIntent = new Intent(Intent.ACTION_VIEW);
          installIntent.setDataAndType(uri, ButterUpdateManager.ANDROID_PACKAGE);
          installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
          installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(installIntent);
        }
      });
      updateManager.checkUpdatesManually();
    }
  }

  @Override
  protected void onResume() {
    String language = PrefUtils.get(this, Prefs.LOCALE, ButterApplication.getSystemLanguage());
    LocaleUtils.setCurrent(this, LocaleUtils.toLocale(language));
    super.onResume();
    BeamManager.getInstance(this).addListener(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    BeamManager.getInstance(this).removeListener(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mVPNManager != null)
      mVPNManager.stop();
  }

  protected void onHomePressed() {
    Intent upIntent = NavUtils.getParentActivityIntent(this);
    if (upIntent != null && NavUtils.shouldUpRecreateTask(this, upIntent)) {
      // This activity is NOT part of this app's task, so create a new task
      // when navigating up, with a synthesized back stack.
      TaskStackBuilder.create(this)
          // Add all of this activity's parents to the back stack
          .addNextIntentWithParentStack(upIntent)
          // Navigate up to the closest parent
          .startActivities();
    } else {
      finish();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    getMenuInflater().inflate(R.menu.activity_base, menu);

    BeamManager beamManager = BeamManager.getInstance(this);
    Boolean castingVisible = mShowCasting && beamManager.hasCastDevices();
    MenuItem item = menu.findItem(R.id.action_casting);
    item.setVisible(castingVisible);
    item.setIcon(beamManager.isConnected() ? R.drawable.ic_av_beam_connected : R.drawable.ic_av_beam_disconnected);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onHomePressed();
        return true;
      case R.id.action_casting:
        //TODO: MAKE THIS WORK
//                BeamDeviceSelectorDialogFragment.show(getActivity().getSupportFragmentManager());
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void updateBeamIcon() {
    supportInvalidateOptionsMenu();
  }

  public void setShowCasting(boolean b) {
    mShowCasting = b;
  }

  @Override
  public void onVPNServiceReady() {

  }

  @Override
  public void onVPNStatusUpdate(VPNManager.State state, String message) {


  }

}