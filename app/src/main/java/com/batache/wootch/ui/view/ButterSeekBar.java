package com.batache.wootch.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSeekBar;

public class ButterSeekBar extends AppCompatSeekBar {

  private Drawable mThumb;

  public ButterSeekBar(Context context) {
    super(context);
  }

  public ButterSeekBar(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ButterSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void setThumb(Drawable thumb) {
    super.setThumb(thumb);
    mThumb = thumb;
  }

  public Drawable getThumbDrawable() {
    return mThumb;
  }

}
