package com.batache.wootch.app.home;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.batache.wootch.R;
import com.batache.wootch.ui.controller.WootchController;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class SeasonChooserFragment extends BottomSheetDialogFragment {

  private ListView seasonsLv;

  private ArrayList<Integer> seasonsIntegers;
  private List<String> seasons = new ArrayList<>();
  private int selectedPosition;
  private WootchController.OnSeasonChooseListener onSeasonChooseListener;

  public SeasonChooserFragment(ArrayList<Integer> seasonsIntegers, int selectedPosition, WootchController.OnSeasonChooseListener onSeasonChooseListener) {
    this.seasonsIntegers = seasonsIntegers;

    for (Integer season : seasonsIntegers) {
      seasons.add("Season " + season);
    }

    this.selectedPosition = selectedPosition;

    this.onSeasonChooseListener = onSeasonChooseListener;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_season_chooser, container, false);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    seasonsLv = view.findViewById(R.id.seasons_list);

    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter(requireContext(), R.layout.item_season, seasons) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        final View renderer = super.getView(position, convertView, parent);
        if (selectedPosition == position) {
          renderer.setBackgroundResource(R.color.colorOnBackgroundSelection);
        } else {
          TypedValue outValue = new TypedValue();
          getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
          renderer.setBackgroundResource(outValue.resourceId);
        }
        return renderer;
      }
    };
    seasonsLv.setAdapter(arrayAdapter);

    seasonsLv.setOnItemClickListener((adapterView, view1, i, l) -> {
      onSeasonChooseListener.onSeasonChoose(seasonsIntegers.get(i));

      dismiss();
    });
  }
}