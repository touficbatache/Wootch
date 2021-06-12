package com.batache.wootch.util;

import android.annotation.SuppressLint;

import butter.droid.base.providers.media.models.Episode;
import butter.droid.base.providers.media.models.Media;
import butter.droid.base.providers.media.models.Movie;

public class TextUtils {

  public static String capitalize(String text) {
    StringBuilder sb = new StringBuilder(text);
    sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
    return sb.toString();
  }

  @SuppressLint("DefaultLocale")
  public static String getTitle(Media media) {
    String title = "";

    if (media instanceof Movie) {
      title = ((Movie) media).title;
    } else if (media instanceof Episode) {
      Episode episode = (Episode) media;
      title = String.format("S%d:E%d \"%s\"", episode.season, episode.episode, episode.title);
    }

    return title;
  }

}
