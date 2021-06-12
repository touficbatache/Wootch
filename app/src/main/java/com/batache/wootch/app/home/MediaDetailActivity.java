package com.batache.wootch.app.home;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.batache.wootch.MobileButterApplication;
import com.batache.wootch.R;
import com.batache.wootch.app.base.BaseDetailFragment;
import com.batache.wootch.app.base.ButterBaseActivity;
import com.batache.wootch.app.home.stream.StreamLoadingActivity;
import com.batache.wootch.app.home.stream.VideoPlayerActivity;
import com.batache.wootch.ui.view.ObservableParallaxScrollView;
import com.batache.wootch.util.DimensionUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butter.droid.base.beaming.BeamPlayerNotificationService;
import butter.droid.base.content.preferences.Prefs;
import butter.droid.base.manager.youtube.YouTubeManager;
import butter.droid.base.providers.media.models.Media;
import butter.droid.base.providers.media.models.Movie;
import butter.droid.base.providers.media.models.Show;
import butter.droid.base.torrent.StreamInfo;
import butter.droid.base.utils.AnimUtils;
import butter.droid.base.utils.NetworkUtils;
import butter.droid.base.utils.PixelUtils;
import butter.droid.base.utils.PrefUtils;
import butterknife.BindView;
import butterknife.OnClick;

public class MediaDetailActivity extends ButterBaseActivity implements BaseDetailFragment.FragmentListener, ObservableParallaxScrollView.Listener {

  @Inject
  YouTubeManager youTubeManager;

  private static Media sMedia;
  private Integer headerHeight = 0, toolbarHeight = 0, topHeight;
  private Boolean transparentBar = true, isTablet = false;

  @BindView(R.id.status_bar_background)
  View statusBarColor;

  @BindView(R.id.back_btn)
  ImageView backBtn;
  @BindView(R.id.scrollview)
  ObservableParallaxScrollView scrollView;
  @Nullable
  @BindView(R.id.parallax)
  RelativeLayout parallaxLayout;
  //  @BindView(R.id.content)
//  FrameLayout content;
  @BindView(R.id.nestedScrollView)
  NestedScrollView nestedScrollView;
  @BindView(R.id.bg_image)
  ImageView bgImage;
  @BindView(R.id.trailer_btn)
  Button watchTrailer;

  public static void startActivity(Context context, Media media) {
    Intent intent = new Intent(context, MediaDetailActivity.class);
    if (media != null) {
      sMedia = media;
    }
    context.startActivity(intent);
  }

  @Override
  public Context getContext() {
    return MediaDetailActivity.this;
  }

  @Override
  public int getLayout() {
    return R.layout.activity_media_detail;
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  @Override
  public void onCreate(Bundle savedInstanceState) {
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    MobileButterApplication.getAppContext()
        .getComponent()
        .inject(this);
    super.onCreate(savedInstanceState);

    backBtn.setOnClickListener(view -> onSupportNavigateUp());

//    setSupportActionBar(toolbar);
//    setShowCasting(true);

    nestedScrollView.setNestedScrollingEnabled(false);

//    // Set transparent toolbar
//    // Hacky empty string to make sure title textview is added to the toolbar
//    if (getSupportActionBar() != null) {
//      getSupportActionBar().setTitle("   ");
//      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }
//    ActionBarBackground.fadeOut(this);

    // Get Title TextView from the Toolbar
//    if (toolbar.getChildAt(0) instanceof TextView) {
//      toolbarTitle = (TextView) toolbar.getChildAt(0);
//    } else {
//      toolbarTitle = (TextView) toolbar.getChildAt(1);
//    }
//    toolbarTitle.setVisibility(View.INVISIBLE);

    // parallaxLayout doesn't exist? Then this is a tablet or big screen device
    isTablet = parallaxLayout == null;

    if (sMedia == null) {
      finish();
      return;
    }

//    getSupportActionBar().setTitle(sMedia.title);

//    scrollView.setListener(mOnScrollListener);
    scrollView.setListener(this);
    scrollView.setOverScrollEnabled(false);
    // Calculate toolbar scrolling variables
    if (!isTablet) {
      int parallaxHeight = parallaxLayout.getLayoutParams().height;
      topHeight = (parallaxHeight / 3) * 2;
      ((LinearLayout.LayoutParams) nestedScrollView.getLayoutParams()).topMargin = -(parallaxHeight / 3);
      nestedScrollView.setMinimumHeight(topHeight / 3);

//      toolbar.setBackgroundColor(getResources().getColor(R.color.colorBackground));
//      toolbar.getBackground().setAlpha(0);
    } else {
      topHeight = (PixelUtils.getScreenHeight(this) / 2);
      ((LinearLayout.LayoutParams) nestedScrollView.getLayoutParams()).topMargin = topHeight;
      nestedScrollView.setMinimumHeight(topHeight);
    }

    watchTrailer.setVisibility(getTrailer() == null || getTrailer().isEmpty() ? View.GONE : View.VISIBLE);

    Fragment fragment = null;
    if (sMedia.isMovie) {
      fragment = MediaDetailMovieFragment.newInstance((Movie) sMedia);
    } else if (sMedia instanceof Show) {
      fragment = MediaDetailShowFragment.newInstance((Show) sMedia);
    }

    if (fragment != null) {
      FragmentManager fragmentManager = getSupportFragmentManager();
      fragmentManager.beginTransaction().replace(R.id.nestedScrollView, fragment).commit();
    }

    String imageUrl = sMedia.headerImage;
    if (isTablet || !PixelUtils.screenIsPortrait(this)) {
      imageUrl = sMedia.headerImage;
    }
    new Picasso.Builder(this)
        .build()
        .load(imageUrl).into(bgImage, new Callback() {
      @Override
      public void onSuccess() {
        mHandler.post(() -> AnimUtils.fadeIn(bgImage));
      }

      @Override
      public void onError(Exception e) {

      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    supportInvalidateOptionsMenu();

    if (null != mService) {
      mService.stopStreaming();
    }
    BeamPlayerNotificationService.cancelNotification();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void playStream(StreamInfo streamInfo) {
    if (PrefUtils.get(this, Prefs.WIFI_ONLY, true) &&
        !NetworkUtils.isWifiConnected(this) &&
        !NetworkUtils.isEthernetConnected(this) &&
        NetworkUtils.isNetworkConnected(this)) {
      //TODO: MAKE THIS WORK
//      MessageDialogFragment.show(getActivity().getSupportFragmentManager(), R.string.wifi_only, R.string.wifi_only_message);
    } else {
      if (mService != null) {
        mService.startForeground();
      }

      scrollView.smoothScrollTo(0, 0);
      StreamLoadingActivity.startActivity(this, streamInfo, Pair.create(bgImage, ViewCompat.getTransitionName(bgImage)));
    }
  }

  @OnClick(R.id.trailer_btn)
  public void openTrailer(View v) {
    Movie movie = (Movie) sMedia;
    if (!youTubeManager.isYouTubeUrl(getTrailer())) {
      VideoPlayerActivity.startActivity(this, new StreamInfo(movie, null, null, null, null, movie.trailer));
    } else {
      String id = youTubeManager.getYouTubeVideoId(getTrailer());
      Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
      try {
        startActivity(appIntent);
      } catch (ActivityNotFoundException ex) {
        //TODO: MAKE THIS WORK
//        TrailerPlayerActivity.startActivity(mActivity, sMovie.trailer, sMovie);
      }
    }
  }

  private String getTrailer() {
    if (sMedia.isMovie) {
      return ((Movie) sMedia).trailer;
    }

    return null;
  }

  @Override
  public void onScroll(int scrollY, ObservableParallaxScrollView.Direction direction) {
    int scrollLimit = Resources.getSystem().getDisplayMetrics().heightPixels * 10 / 100; // 10% of screen height
    float alpha = ((float) scrollY) / ((float) scrollLimit);
    statusBarColor.setAlpha(alpha);
  }

  @Override
  public void onApplyAllWindowInsets() {
    findViewById(R.id.back_btn_container).setPadding(0, DimensionUtils.TOP_INSET, 0, 0);
  }

  @Override
  public boolean onSupportNavigateUp() {
    finish();
    return true;
  }
}