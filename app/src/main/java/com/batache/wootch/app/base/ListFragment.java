package com.batache.wootch.app.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.epoxy.EpoxyRecyclerView;
import com.batache.wootch.R;
import com.batache.wootch.ui.controller.WootchController;

public abstract class ListFragment extends BaseFragment {

  protected EpoxyRecyclerView recyclerView;
  protected WootchController controller;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    rootView = inflater.inflate(getLayout(), null);

    onCreateView();

    return rootView;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (rootView.findViewById(R.id.recyclerview) != null) {
      controller = new WootchController();
      recyclerView = rootView.findViewById(R.id.recyclerview);
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
      recyclerView.setController(controller);
    }
  }

  @Override
  public void onApplyAllWindowInsets() {

  }

  public enum Mode {
    NORMAL, SEARCH
  }

  public enum State {
    UNINITIALISED, LOADING, SEARCHING, LOADING_PAGE, LOADED, LOADING_DETAIL
  }
}
