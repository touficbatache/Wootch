package com.batache.wootch.app.home.stream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.batache.wootch.MobileButterApplication;
import com.batache.wootch.R;
import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import butter.droid.base.beaming.BeamManager;
import butter.droid.base.content.preferences.DefaultPlayer;
import butter.droid.base.fragments.BaseStreamLoadingFragment;
import butter.droid.base.fragments.dialog.StringArraySelectorDialogFragment;
import butter.droid.base.providers.media.models.Episode;
import butter.droid.base.providers.media.models.Media;
import butter.droid.base.providers.media.models.Movie;
import butter.droid.base.torrent.StreamInfo;
import butter.droid.base.utils.FragmentUtil;
import butter.droid.base.utils.ThreadUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;

public class StreamLoadingFragment extends BaseStreamLoadingFragment {

  private Context context;
  private Torrent currentTorrent;

  View root;
  @BindView(R.id.title_textview)
  TextView titleTextView;
  @BindView(R.id.progress_indicator)
  ProgressBar progressIndicator;
  @BindView(R.id.primary_textview)
  TextView primaryTextView;
  @BindView(R.id.secondary_textview)
  TextView secondaryTextView;
  @BindView(R.id.tertiary_textview)
  TextView tertiaryTextView;
  @BindView(R.id.background_imageview)
  ImageView backgroundImageView;
  @BindView(R.id.startexternal_button)
  Button startExternalButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    MobileButterApplication.getAppContext()
        .getComponent()
        .inject(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    root = inflater.inflate(R.layout.fragment_streamloading, container, false);
    ButterKnife.bind(this, root);

    //postpone the transitions until after the view is layed out.
    getActivity().postponeEnterTransition();

    root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      public boolean onPreDraw() {
        root.getViewTreeObserver().removeOnPreDrawListener(this);
        getActivity().startPostponedEnterTransition();
        return true;
      }
    });

    return root;
  }

  public void setTitle(String title) {
    titleTextView.setText(title);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mPlayingExternal)
      setState(State.STREAMING);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    context = getActivity();
    loadBackgroundImage();
  }

  @Override
  public void onStreamPrepared(Torrent torrent) {
    currentTorrent = torrent;

    if (TextUtils.isEmpty(mStreamInfo.getTitle())) {
      StringArraySelectorDialogFragment.show(getChildFragmentManager(), R.string.select_file, currentTorrent.getFileNames(), -1, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int position) {
          currentTorrent.setSelectedFileIndex(position);
          StreamLoadingFragment.super.onStreamPrepared(currentTorrent);
        }
      });
      return;
    }

    super.onStreamPrepared(currentTorrent);
  }

  private void loadBackgroundImage() {
    StreamInfo info = mCallback.getStreamInformation();
    /* attempt to load background image */
    if (null != info) {
      String url = info.getHeaderImageUrl();

      if (!TextUtils.isEmpty(url))
        Picasso.get().load(url).error(R.color.colorBackground).into(backgroundImageView);
    }
  }

  private void updateStatus(final StreamStatus status) {
    if (FragmentUtil.isNotAdded(this)) return;

    final DecimalFormat df = new DecimalFormat("#############0.00");
    ThreadUtils.runOnUiThread(new Runnable() {
      @SuppressLint("DefaultLocale")
      @Override
      public void run() {
        progressIndicator.setIndeterminate(false);
        if (!mPlayingExternal) {
          progressIndicator.setProgress(status.bufferProgress);
          primaryTextView.setText(status.bufferProgress + "%");
        } else {
          int progress = ((Float) status.progress).intValue();
          progressIndicator.setProgress(progress);
          primaryTextView.setText(progress + "%");
        }

        if (status.downloadSpeed / 1024 < 1000) {
          secondaryTextView.setText(String.format("%s KB/s", df.format(status.downloadSpeed / 1024)));
        } else {
          secondaryTextView.setText(String.format("%s MB/s", df.format(status.downloadSpeed / 1048576)));
        }
        tertiaryTextView.setText(String.format("%d %s", status.seeds, getString(R.string.seeds)));
      }
    });
  }

  @Override
  protected void updateView(State state, Object extra) {
    switch (state) {
      case UNINITIALISED:
        tertiaryTextView.setText(null);
        primaryTextView.setText(null);
        secondaryTextView.setText(null);
        progressIndicator.setIndeterminate(true);
        progressIndicator.setProgress(0);
        break;
      case ERROR:
        if (extra instanceof String)
          primaryTextView.setText((String) extra);
        secondaryTextView.setText(null);
        tertiaryTextView.setText(null);
        progressIndicator.setIndeterminate(true);
        progressIndicator.setProgress(0);
        break;
      case BUFFERING:
        primaryTextView.setText(R.string.starting_buffering);
        tertiaryTextView.setText(null);
        secondaryTextView.setText(null);
        progressIndicator.setIndeterminate(true);
        progressIndicator.setProgress(0);
        break;
      case STREAMING:
        if (extra instanceof StreamStatus)
          updateStatus((StreamStatus) extra);
        break;
      case WAITING_SUBTITLES:
        primaryTextView.setText(R.string.waiting_for_subtitles);
        tertiaryTextView.setText(null);
        secondaryTextView.setText(null);
        progressIndicator.setIndeterminate(true);
        progressIndicator.setProgress(0);
        break;
      case WAITING_TORRENT:
        primaryTextView.setText(R.string.waiting_torrent);
        tertiaryTextView.setText(null);
        secondaryTextView.setText(null);
        progressIndicator.setIndeterminate(true);
        progressIndicator.setProgress(0);
        break;

    }
  }

  @Override
  @DebugLog
  protected void startPlayerActivity(String location, int resumePosition) {
    if (!FragmentUtil.isNotAdded(this) && !mPlayerStarted) {
      mStreamInfo.setVideoLocation(location);
      if (BeamManager.getInstance(context).isConnected()) {
        BeamPlayerActivity.startActivity(context, mStreamInfo, resumePosition);
      } else {
        mPlayingExternal = DefaultPlayer.start(mStreamInfo.getMedia(), mStreamInfo.getSubtitleLanguage(), location);
        if (!mPlayingExternal) {
          VideoPlayerActivity.startActivity(context, mStreamInfo, resumePosition);
        }
      }

      if (!mPlayingExternal) {
        getActivity().finish();
      } else {
        startExternalButton.setVisibility(View.VISIBLE);
      }
    }
  }

  @OnClick(R.id.startexternal_button)
  public void externalClick(View v) {
    DefaultPlayer.start(mStreamInfo.getMedia(), mStreamInfo.getSubtitleLanguage(), mStreamInfo.getVideoLocation());
  }
}
