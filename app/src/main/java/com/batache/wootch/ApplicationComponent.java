package com.batache.wootch;

import com.batache.wootch.app.MainActivity;
import com.batache.wootch.app.base.LoadingDetailDialogFragment;
import com.batache.wootch.app.home.HomeFragment;
import com.batache.wootch.app.home.HomeMoreFragment;
import com.batache.wootch.app.home.MediaDetailActivity;
import com.batache.wootch.app.home.MediaDetailMovieFragment;
import com.batache.wootch.app.home.stream.BeamPlayerActivity;
import com.batache.wootch.app.home.stream.StreamLoadingActivity;
import com.batache.wootch.app.home.stream.StreamLoadingFragment;
import com.batache.wootch.app.home.stream.VideoPlayerActivity;
import com.batache.wootch.app.home.stream.VideoPlayerFragment;
import com.batache.wootch.model.adapter.ProviderChipModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
    modules = ApplicationModule.class
)
public interface ApplicationComponent {

  void inject(MobileButterApplication application);

  void inject(MainActivity activity);

  void inject(HomeFragment fragment);

  void inject(HomeMoreFragment fragment);

  void inject(ProviderChipModel model);

//  void inject(PreferencesActivity activity);

//  void inject(AboutActivity activity);

  void inject(BeamPlayerActivity activity);

  void inject(MediaDetailActivity activity);

//  void inject(SearchActivity activity);

  void inject(StreamLoadingActivity activity);

//  void inject(TermsActivity activity);

  void inject(VideoPlayerActivity activity);

//  void inject(NavigationDrawerFragment fragment);

//  void inject(MediaContainerFragment fragment);

//  void inject(MediaListFragment fragment);

//  void inject(MediaGenreSelectionFragment fragment);

  void inject(LoadingDetailDialogFragment fragment);

  void inject(StreamLoadingFragment fragment);

//  void inject(EpisodeDialogFragment fragment);

  void inject(MediaDetailMovieFragment fragment);

  void inject(VideoPlayerFragment fragment);

}
