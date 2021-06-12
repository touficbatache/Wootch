package com.batache.wootch.app.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.batache.wootch.util.DimensionUtils;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

  public abstract Context getContext();

  @LayoutRes
  public abstract int getLayout();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayout());
    ButterKnife.bind(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    setSystemUiVisibility();
    setInsetListener();
  }

  protected void setSystemUiVisibility() {
    View view = getWindow().getDecorView();
    view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
  }

  protected void setInsetListener() {
    View rootView = getWindow().getDecorView();
    ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
      DimensionUtils.LEFT_INSET = insets.getSystemWindowInsetLeft();
      DimensionUtils.TOP_INSET = insets.getSystemWindowInsetTop();
      DimensionUtils.RIGHT_INSET = insets.getSystemWindowInsetRight();
      if (insets.getSystemWindowInsetBottom() < getResources().getDisplayMetrics().heightPixels / 5) {
        DimensionUtils.BOTTOM_INSET = insets.getSystemWindowInsetBottom();
      }
      onApplyAllWindowInsets();
      removeInsetListener();
      return insets;
    });
  }

  protected void removeInsetListener() {
    ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), null);
  }

  public abstract void onApplyAllWindowInsets();

}
