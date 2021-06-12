package com.batache.wootch.util;

public class DimensionUtils {
  public static int LEFT_INSET = 0;
  public static int TOP_INSET = 0;
  public static int RIGHT_INSET = 0;
  public static int BOTTOM_INSET = 0;

  public static String resizeImageUrl(String oldUrl, ImageSize imageSize) {
    if(oldUrl == null) {
      return null;
    }

    String newSize = "w92";

    switch (imageSize) {
      case MINI:
        newSize = "w92";
        break;
      case TINY:
        newSize = "w154";
        break;
      case SMALL:
        newSize = "w185";
        break;
      case NORMAL:
        newSize = "w342";
        break;
      case BIG:
        newSize = "w500";
        break;
      case LARGE:
        newSize = "w780";
        break;
      case ORIGINAL:
        newSize = "original";
        break;
    }

    return oldUrl.replace("w500", newSize);
  }

  public enum ImageSize {
    MINI, TINY, SMALL, NORMAL, BIG, LARGE, ORIGINAL
  }
}
