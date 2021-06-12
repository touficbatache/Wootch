package com.batache.wootch.app.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.batache.wootch.R;
import com.batache.wootch.app.base.BaseDetailFragment;
import com.batache.wootch.ui.controller.WootchController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butter.droid.base.content.preferences.DefaultQuality;
import butter.droid.base.content.preferences.Prefs;
import butter.droid.base.providers.media.models.Episode;
import butter.droid.base.providers.media.models.Media;
import butter.droid.base.providers.media.models.Show;
import butter.droid.base.torrent.StreamInfo;
import butter.droid.base.utils.PrefUtils;
import butter.droid.base.utils.SortUtils;
import butter.droid.base.utils.VersionUtils;
import butterknife.ButterKnife;

public class MediaDetailShowFragment extends BaseDetailFragment {

  private static Show sShow;

  static MediaDetailShowFragment newInstance(Show show) {
    Bundle b = new Bundle();
    sShow = show;
    MediaDetailShowFragment showDetailFragment = new MediaDetailShowFragment();
    showDetailFragment.setArguments(b);
    return showDetailFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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

    if (sShow == null) {
      return;
    }

    controller.addMediaDetailInfo(getActivity(), sShow);

    final ArrayList<Integer> availableSeasons = new ArrayList<>();
    for (Episode episode : sShow.episodes) {
      if (!availableSeasons.contains(episode.season)) {
        availableSeasons.add(episode.season);
      }
    }
    Collections.sort(availableSeasons);

    boolean hasSpecial = availableSeasons.indexOf(0) > -1;
    if (hasSpecial)
      availableSeasons.remove(availableSeasons.indexOf(0));

    addSeason(availableSeasons.get(0));

    controller.addSeasonChooser(getActivity(), availableSeasons, season -> {
      controller.clearChangeableData();

      addSeason(season);
    });


//    for (int seasonInt : availableSeasons) {
//      fragments.add(ShowDetailSeasonFragment.newInstance(sShow, seasonInt));
//    }
//    if (hasSpecial)
//      fragments.add(ShowDetailSeasonFragment.newInstance(sShow, 0));

  }

  private void addSeason(Integer season) {
    List<Episode> episodes = new ArrayList<>();
    for (Episode episode : sShow.episodes) {
      if (episode.season == season) {
        episodes.add(episode);
      }
    }

    controller.addSeason(getContext(), episodes, episode -> {
      final String[] qualities = episode.torrents.keySet().toArray(new String[episode.torrents.size()]);
      SortUtils.sortQualities(qualities);
      String defaultQuality = DefaultQuality.get(mActivity, Arrays.asList(qualities));

      String defaultSubtitle = PrefUtils.get(getContext(), Prefs.SUBTITLE_DEFAULT, null);

      Media.Torrent torrent = episode.torrents.get(defaultQuality);
      StreamInfo streamInfo = new StreamInfo(episode, sShow, torrent.getUrl(), defaultSubtitle, defaultQuality);
      ((MediaDetailActivity) getActivity()).playStream(streamInfo);
    });
  }

  @Override
  public void onApplyAllWindowInsets() {

  }

}
