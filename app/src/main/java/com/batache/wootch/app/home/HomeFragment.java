package com.batache.wootch.app.home;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.palette.graphics.Palette;

import com.batache.wootch.MobileButterApplication;
import com.batache.wootch.R;
import com.batache.wootch.api.ProviderMediaFetcher;
import com.batache.wootch.app.base.ListFragment;
import com.batache.wootch.app.base.LoadingDetailDialogFragment;
import com.batache.wootch.model.pojo.MediaSection;
import com.batache.wootch.ui.controller.WootchController;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butter.droid.base.content.preferences.Prefs;
import butter.droid.base.manager.provider.ProviderManager;
import butter.droid.base.providers.media.models.Media;
import butter.droid.base.utils.PrefUtils;

public class HomeFragment extends ListFragment implements ProviderManager.OnProviderChangeListener, ProviderMediaFetcher.OnFetchedListener, LoadingDetailDialogFragment.Callback, WootchController.OnMediaItemClickListener {

  @Inject
  ProviderManager providerManager;

  private ProviderMediaFetcher providerMediaFetcher;

  private ArrayList<Media> clickedItemList = new ArrayList<>();

  public static final String DIALOG_LOADING_DETAIL = "DIALOG_LOADING_DETAIL";
  public static final int LOADING_DIALOG_FRAGMENT = 1;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public int getLayout() {
    return R.layout.fragment_home;
  }

  @Override
  public void onResume() {
    super.onResume();

    providerManager.addProviderListener(this);
  }

  @Override
  public void onCreateView() {
    MobileButterApplication.getAppContext()
        .getComponent()
        .inject(this);

    providerMediaFetcher = new ProviderMediaFetcher(requireContext(), providerManager, this);

    providerManager.setCurrentProviderType(PrefUtils.get(requireContext(), Prefs.DEFAULT_PROVIDER, ProviderManager.PROVIDER_TYPE_MOVIE));
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    controller.addProviderChips(requireContext());
    getMediaItems();
  }

  private void getMediaItems() {
    providerMediaFetcher.cancel();

    controller.resetChangeableData();

    providerMediaFetcher.fetch();
  }

  @Override
  public void onSuccess(List<MediaSection> mediaSections) {
    controller.clearChangeableData();

    for (MediaSection mediaSection : mediaSections) {
      controller.addMediaSection(
          getContext(),
          mediaSection.getLabel(),
          view -> {
            HomeFragmentDirections.MoreAction action = HomeFragmentDirections.moreAction(mediaSection.getLabel(), new Gson().toJson(mediaSection.getType()));
            Navigation.findNavController(view).navigate(action);
          },
          this,
          mediaSection.getItems()
      );
    }
  }

  @Override
  public void onFailure(Exception e) {

  }

  @Override
  public void onItemClick(View v, List<Media> items, Media item, int position, ImageView coverImage) {
    clickedItemList = new ArrayList<>(items);

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

  private void showLoadingDialog(Integer position) {
    LoadingDetailDialogFragment loadingFragment = LoadingDetailDialogFragment.newInstance(position);
    loadingFragment.setTargetFragment(this, LOADING_DIALOG_FRAGMENT);
    loadingFragment.show(getParentFragmentManager(), DIALOG_LOADING_DETAIL);
  }

  @Override
  public void onProviderChanged(int provider) {
    getMediaItems();
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
    return clickedItemList;
  }

  @Override
  public void onPause() {
    super.onPause();

    providerManager.removeProviderListener(this);
  }
}
