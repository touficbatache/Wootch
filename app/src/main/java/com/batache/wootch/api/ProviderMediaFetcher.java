package com.batache.wootch.api;

import android.content.Context;

import com.batache.wootch.app.home.MediaDetailMovieFragment;
import com.batache.wootch.model.pojo.MediaSection;
import com.batache.wootch.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

import butter.droid.base.ButterApplication;
import butter.droid.base.content.preferences.Prefs;
import butter.droid.base.manager.provider.ProviderManager;
import butter.droid.base.providers.media.MediaProvider;
import butter.droid.base.providers.media.models.Media;
import butter.droid.base.providers.media.models.Movie;
import butter.droid.base.utils.LocaleUtils;
import butter.droid.base.utils.PrefUtils;

public class ProviderMediaFetcher implements MediaProvider.Callback {

  private Context context;
  private ProviderManager providerManager;
  private OnFetchedListener onFetchedListener;

  private List<MediaProvider.NavInfo> sections = new ArrayList<>();
  private List<MediaProvider.Filters> sectionsToFetch = new ArrayList<>();

  private List<MediaSection> mediaSections = new ArrayList<>();

  public ProviderMediaFetcher(Context context, ProviderManager providerManager, OnFetchedListener onFetchedListener) {
    this.context = context;
    this.providerManager = providerManager;
    this.onFetchedListener = onFetchedListener;
  }

  public void fetch() {
    sections.clear();
    sectionsToFetch.clear();
    mediaSections.clear();

    MediaProvider mediaProvider = providerManager.getCurrentMediaProvider();
    sections = mediaProvider.getNavigation();

    for (int i = 0; i < sections.size(); i++) {
      MediaProvider.NavInfo tab = sections.get(i);

      MediaProvider.Filters filters = new MediaProvider.Filters();
      filters.setSort(tab.getFilter());
      filters.setOrder(tab.getOrder());
      filters.setGenre(null);
      String language = PrefUtils.get(context, Prefs.LOCALE, ButterApplication.getSystemLanguage());
      filters.setLangCode(LocaleUtils.toLocale(language).getLanguage());

      sectionsToFetch.add(filters);
    }

    getNextSection();
  }

  private void getNextSection() {
    if (sectionsToFetch.size() == 0) {
      if(mediaSections.size() > 0) {
        onFetchedListener.onSuccess(mediaSections);
      } else {
        onFetchedListener.onFailure(new Exception("An error occurred."));
      }
      return;
    }
    providerManager.getCurrentMediaProvider().getList(new MediaProvider.Filters(sectionsToFetch.get(0)), this);
  }

  @Override
  public void onSuccess(MediaProvider.Filters filters, ArrayList<Media> items) {
    mediaSections.add(new MediaSection(getLabel(filters.getSort()), filters.getSort(), items));

    if (sectionsToFetch.size() > 0) {
      sectionsToFetch.remove(0);
      getNextSection();
    }
  }

  private String getLabel(MediaProvider.Filters.Sort sort) {
    for (MediaProvider.NavInfo section : sections) {
      if (sort == section.getFilter()) {
        return TextUtils.capitalize(section.getLabel().toLowerCase());
      }
    }
    return null;
  }

  @Override
  public void onFailure(Exception e) {
    onFetchedListener.onFailure(e);
  }

  public void cancel() {
    providerManager.getCurrentMediaProvider().cancel();
  }

  public interface OnFetchedListener {
    void onSuccess(List<MediaSection> mediaSections);

    void onFailure(Exception e);
  }
}
