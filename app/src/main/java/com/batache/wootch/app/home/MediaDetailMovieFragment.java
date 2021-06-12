package com.batache.wootch.app.home;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.batache.wootch.MobileButterApplication;
import com.batache.wootch.R;
import com.batache.wootch.app.base.BaseDetailFragment;

import java.util.Arrays;

import javax.inject.Inject;

import butter.droid.base.content.preferences.DefaultQuality;
import butter.droid.base.manager.provider.ProviderManager;
import butter.droid.base.manager.youtube.YouTubeManager;
import butter.droid.base.providers.media.models.Movie;
import butter.droid.base.torrent.StreamInfo;
import butter.droid.base.utils.SortUtils;
import butterknife.ButterKnife;

public class MediaDetailMovieFragment extends BaseDetailFragment {

  @Inject
  ProviderManager providerManager;
  @Inject
  YouTubeManager youTubeManager;

  private static Movie sMovie;

  static MediaDetailMovieFragment newInstance(Movie movie) {
    sMovie = movie;
    return new MediaDetailMovieFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MobileButterApplication.getAppContext()
        .getComponent()
        .inject(this);
  }

  @Override
  public int getLayout() {
    return R.layout.fragment_list;
  }

  @Override
  public void onCreateView() {
    ButterKnife.bind(this, rootView);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (sMovie == null) {
      return;
    }

    controller.addMediaDetailInfo(getActivity(), sMovie);
    controller.addPlay(view1 -> {
      if (sMovie.torrents.size() > 0) {
        final String[] qualities = sMovie.torrents.get("en").keySet().toArray(new String[sMovie.torrents.size()]);
        SortUtils.sortQualities(qualities);

        String defaultQuality = DefaultQuality.get(requireContext(), Arrays.asList(qualities));

        String streamUrl = sMovie.torrents.get("en").get(defaultQuality).getUrl();
        StreamInfo streamInfo = new StreamInfo(sMovie, streamUrl, null, defaultQuality);
        mCallback.playStream(streamInfo);
      }
    });
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof FragmentListener)
      mCallback = (FragmentListener) context;
  }
}
