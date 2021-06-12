package com.batache.wootch.app.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.batache.wootch.R;
import com.batache.wootch.app.base.ButterBaseActivity;
import com.batache.wootch.app.home.HomeMoreFragment;
import com.batache.wootch.util.DimensionUtils;
import com.google.android.material.appbar.AppBarLayout;

import butterknife.BindView;

public class SearchActivity extends ButterBaseActivity {

  public static final String EXTRA_PROVIDER = "extra_provider";

  @BindView(R.id.app_bar)
  AppBarLayout appBar;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.searchview)
  SearchView mSearchview;

  private HomeMoreFragment mFragment;

  public static Intent startActivity(Activity activity) {
    Intent intent = new Intent(activity, SearchActivity.class);
    activity.startActivity(intent);
//		activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out,);
    return intent;
  }

  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public int getLayout() {
    return R.layout.activity_search;
  }

  @SuppressLint("MissingSuperCall")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    setShowCasting(true);

    toolbar.getLayoutParams().height = getResources().getDimensionPixelSize(
        R.dimen.abc_action_bar_default_height_material);

    mSearchview.onActionViewExpanded();
    mSearchview.setOnQueryTextListener(mSearchListener);

    //dont re add the fragment if it exists
    if (null != savedInstanceState) {
      mFragment = (HomeMoreFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
      return;
    }

    //create and add the media fragment
//    mFragment = HomeMoreFragment.newInstance(ListFragment.Mode.SEARCH, MediaProvider.Filters.Sort.POPULARITY,
//        MediaProvider.Filters.Order.DESC);
    mFragment = new HomeMoreFragment();
    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, mFragment).commit();
  }

  private SearchView.OnQueryTextListener mSearchListener = new SearchView.OnQueryTextListener() {
    @Override
    public boolean onQueryTextSubmit(String s) {
      if (null == mFragment) return false;//fragment not added yet.
      mFragment.triggerSearch(s);
      return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
      if (s.equals("")) {
        onQueryTextSubmit(s);
      }
      return false;
    }
  };

  @Override
  public boolean onSupportNavigateUp() {
    onHomePressed();
    return true;
  }

  @Override
  public void onApplyAllWindowInsets() {
    appBar.setPadding(0, DimensionUtils.TOP_INSET, 0, 0);
  }
}
