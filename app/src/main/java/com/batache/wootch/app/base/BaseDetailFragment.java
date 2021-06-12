package com.batache.wootch.app.base;

import android.content.Context;
import android.view.View;

import com.batache.wootch.app.home.MediaDetailActivity;

import butter.droid.base.torrent.StreamInfo;

public abstract class BaseDetailFragment extends ListFragment {

  protected FragmentListener mCallback;
  protected MediaDetailActivity mActivity;
  protected View rootView;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MediaDetailActivity)
      mActivity = (MediaDetailActivity) context;
  }

  public interface FragmentListener {
    void playStream(StreamInfo streamInfo);
  }

}
