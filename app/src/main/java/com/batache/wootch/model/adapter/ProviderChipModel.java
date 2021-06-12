package com.batache.wootch.model.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.batache.wootch.MobileButterApplication;
import com.batache.wootch.R;
import com.batache.wootch.util.PrefUtils;
import com.google.android.material.chip.ChipGroup;

import javax.inject.Inject;

import butter.droid.base.content.preferences.Prefs;
import butter.droid.base.manager.provider.ProviderManager;

@EpoxyModelClass(layout = R.layout.item_provider_chips)
public abstract class ProviderChipModel extends EpoxyModelWithHolder<ProviderChipModel.ProviderChipHolder> {

  @EpoxyAttribute
  Context context;

  @Inject
  ProviderManager providerManager;

  private boolean initialized = false;

  @Override
  public void bind(@NonNull ProviderChipHolder holder) {
    MobileButterApplication.getAppContext()
        .getComponent()
        .inject(this);

    super.bind(holder);

    ChipGroup chipGroup = holder.chipGroup;
    chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
      @ProviderManager.ProviderType int providerType;
      switch (checkedId) {
        default:
          providerType = ProviderManager.PROVIDER_TYPE_MOVIE;
          break;
        case R.id.shows_chip:
          providerType = ProviderManager.PROVIDER_TYPE_SHOW;
          break;
        case R.id.anime_chip:
          providerType = ProviderManager.PROVIDER_TYPE_ANIME;
          break;
      }
      PrefUtils.save(context, Prefs.DEFAULT_PROVIDER, providerType);
      providerManager.setCurrentProviderType(providerType);
    });

    if (!initialized) {
      int chipId;
      switch (providerManager.getCurrentMediaProviderType()) {
        default:
          chipId = R.id.movies_chip;
          break;
        case ProviderManager.PROVIDER_TYPE_SHOW:
          chipId = R.id.shows_chip;
          break;
        case ProviderManager.PROVIDER_TYPE_ANIME:
          chipId = R.id.anime_chip;
          break;
      }
      chipGroup.check(chipId);

      initialized = true;
    }
  }

  @Override
  public void unbind(@NonNull ProviderChipHolder holder) {
    super.unbind(holder);

    holder.chipGroup.setOnCheckedChangeListener(null);
  }

  static class ProviderChipHolder extends EpoxyHolder {

    ChipGroup chipGroup;

    @Override
    protected void bindView(@NonNull View itemView) {
      chipGroup = itemView.findViewById(R.id.chip_group);
    }
  }

}
