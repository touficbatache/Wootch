package com.batache.wootch.app.home;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.batache.wootch.MobileButterApplication;
import com.batache.wootch.R;
import com.batache.wootch.app.base.ListFragment;
import com.batache.wootch.app.base.LoadingDetailDialogFragment;
import com.batache.wootch.ui.controller.WootchController;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butter.droid.base.ButterApplication;
import butter.droid.base.content.preferences.Prefs;
import butter.droid.base.manager.provider.ProviderManager;
import butter.droid.base.providers.media.MediaProvider;
import butter.droid.base.providers.media.models.Media;
import butter.droid.base.utils.LocaleUtils;
import butter.droid.base.utils.NetworkUtils;
import butter.droid.base.utils.PrefUtils;
import butter.droid.base.utils.ThreadUtils;

public class HomeMoreFragment extends ListFragment implements MediaProvider.Callback, LoadingDetailDialogFragment.Callback, WootchController.OnMediaItemClickListener {

//  public static final String EXTRA_SORT = "extra_sort";
//  public static final String EXTRA_ORDER = "extra_order";
//  public static final String EXTRA_MODE = "extra_mode";

  private GridLayoutManager layoutManager;

  @Inject
  ProviderManager providerManager;

  private MediaProvider.Filters filters = new MediaProvider.Filters();
  private MediaProvider.Filters.Sort sort;
  private boolean mEndOfListReached = false;
  private int columns = 3;
  private int mTotalItemCount = 0, mLoadingTreshold = columns * 3, mPreviousTotal = 0;
  private int mPage = 1;

  private State mState = State.UNINITIALISED;
//  private Mode mMode = Mode.NORMAL;

  private ArrayList<Media> mItems = new ArrayList<>();

  @Override
  public int getLayout() {
    return R.layout.fragment_list;
  }

  @Override
  public void onCreateView() {
    if (getArguments() != null) {
      sort = new Gson().fromJson(HomeMoreFragmentArgs.fromBundle(getArguments()).getSortGson(), MediaProvider.Filters.Sort.class);

//      String language = PrefUtils.get(getActivity(), Prefs.LOCALE, ButterApplication.getSystemLanguage());
//      filters.setSort((MediaProvider.Filters.Sort) getArguments().getSerializable(EXTRA_SORT));
//      filters.setOrder((MediaProvider.Filters.Order) getArguments().getSerializable(EXTRA_ORDER));
//      filters.setLangCode(LocaleUtils.toLocale(language).getLanguage());

//      mMode = (Mode) getArguments().getSerializable(EXTRA_MODE);
    }
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    layoutManager = new GridLayoutManager(getContext(), columns);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.addOnScrollListener(scrollListener);

    MobileButterApplication.getAppContext()
        .getComponent()
        .inject(this);

    if (sort != null) {
      getMediaFromProvider();
    }
  }

  private void getMediaFromProvider() {
    controller.reset();

    filters.setSort(sort);
    filters.setOrder(MediaProvider.Filters.Order.DESC);
    filters.setGenre(null);
    String language = PrefUtils.get(getActivity(), Prefs.LOCALE, ButterApplication.getSystemLanguage());
    filters.setLangCode(LocaleUtils.toLocale(language).getLanguage());

    setState(State.LOADING);
    providerManager.getCurrentMediaProvider().getList(new MediaProvider.Filters(filters), this);
  }

  @Override
  public void onSuccess(MediaProvider.Filters filters, ArrayList<Media> items) {
    int oldItemsSize = mItems.size();

    items.removeAll(mItems);
    mEndOfListReached = items.size() == 0;
    if (!mEndOfListReached) {
      mItems.addAll(items);
      if (isAdded()) {
        mPage = mPage + 1;
        ThreadUtils.runOnUiThread(() -> {
          controller.addMediaItems(
              getContext(),
              oldItemsSize,
              items,
              this
          );
          mPreviousTotal = mTotalItemCount = controller.getItemCount();
        });
      }
    }
    setState(State.LOADED);
  }

  @Override
  public void onItemClick(View v, List<Media> items, Media item, int position, ImageView coverImage) {
    if (coverImage.getDrawable() == null) {
      showLoadingDialog(position);
      return;
    }

    Bitmap cover = ((BitmapDrawable) coverImage.getDrawable()).getBitmap();
    new Palette.Builder(cover).generate(palette -> {
      int vibrantColor = palette.getVibrantColor(-1);
      int paletteColor;
      if (vibrantColor == -1) {
        paletteColor = palette.getMutedColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
      } else {
        paletteColor = vibrantColor;
      }
      item.color = paletteColor;
      showLoadingDialog(position);
    });
  }

  private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
      int visibleItemCount = layoutManager.getChildCount();
      mTotalItemCount = layoutManager.getItemCount() - (getState() == State.LOADING ? 1 : 0);
      int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

      if (getState() == State.LOADING_PAGE) {
        if (mTotalItemCount > mPreviousTotal) {
          mPreviousTotal = mTotalItemCount;
          mPreviousTotal = mTotalItemCount = layoutManager.getItemCount();
          setState(State.LOADED);
        }
      }

      if (!mEndOfListReached && getState() != State.SEARCHING && getState() != State.LOADING_PAGE && getState() != State.LOADING && (mTotalItemCount - visibleItemCount) <= (firstVisibleItem +
          mLoadingTreshold)) {

        filters.setPage(mPage);
        providerManager.getCurrentMediaProvider().getList(mItems, new MediaProvider.Filters(filters), HomeMoreFragment.this);

        mPreviousTotal = mTotalItemCount = layoutManager.getItemCount();
        setState(State.LOADING_PAGE);
      }
    }
  };

  private void setState(State state) {
    if (getState() != state) {
      mState = state;
    }
  }

  private State getState() {
    return mState;
  }

  @Override
  public void onFailure(Exception e) {

  }

  private void showLoadingDialog(Integer position) {
    LoadingDetailDialogFragment loadingFragment = LoadingDetailDialogFragment.newInstance(position);
    loadingFragment.setTargetFragment(this, HomeFragment.LOADING_DIALOG_FRAGMENT);
    loadingFragment.show(getParentFragmentManager(), HomeFragment.DIALOG_LOADING_DETAIL);
  }

//  public static HomeMoreFragment newInstance(Mode mode, MediaProvider.Filters.Sort sort, MediaProvider.Filters.Order order) {
//    HomeMoreFragment frag = new HomeMoreFragment();
//    Bundle args = new Bundle();
//    args.putSerializable(EXTRA_MODE, mode);
//    args.putSerializable(EXTRA_SORT, sort);
//    args.putSerializable(EXTRA_ORDER, order);
//    frag.setArguments(args);
//    return frag;
//  }

  public void triggerSearch(String searchQuery) {
    if (!isAdded()) return;
    if (null == controller) return;
    if (!NetworkUtils.isNetworkConnected(getActivity())) {
      Toast.makeText(getActivity(), R.string.network_message, Toast.LENGTH_SHORT).show();
      return;
    }

    providerManager.getCurrentMediaProvider().cancel();

    mEndOfListReached = false;

    mItems.clear();
    controller.clear();

    if (searchQuery.equalsIgnoreCase("")) {
      setState(State.LOADED);
      return; //don't do a search for empty queries
    }

    setState(State.SEARCHING);
    mPage = 1;
    filters.setPage(mPage);
    filters.setKeywords(searchQuery);

    providerManager.getCurrentMediaProvider().getList(new MediaProvider.Filters(filters), this);
  }

  @Override
  public void onDetailLoadFailure() {
    Snackbar.make(rootView, R.string.unknown_error, Snackbar.LENGTH_SHORT).show();
  }

  @Override
  public void onDetailLoadSuccess(Media item) {
    MediaDetailActivity.startActivity(getContext(), item);
  }

  @Override
  public ArrayList<Media> getCurrentList() {
    return mItems;
  }
}
