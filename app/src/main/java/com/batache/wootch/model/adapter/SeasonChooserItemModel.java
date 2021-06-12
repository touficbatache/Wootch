package com.batache.wootch.model.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.batache.wootch.R;
import com.batache.wootch.app.home.SeasonChooserFragment;
import com.batache.wootch.ui.controller.WootchController;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

@EpoxyModelClass(layout = R.layout.item_season_chooser)
public abstract class SeasonChooserItemModel extends EpoxyModelWithHolder<SeasonChooserItemModel.SeasonChooserItemHolder> {

  @EpoxyAttribute
  FragmentActivity activity;

  @EpoxyAttribute
  ArrayList<Integer> seasons;

  @EpoxyAttribute
  WootchController.OnSeasonChooseListener onSeasonChooseListener;

  private boolean initialized = false;

  private int selectedPosition = 0;

  @Override
  public void bind(@NonNull SeasonChooserItemHolder holder) {
    super.bind(holder);

    if (!initialized) {
      updateSeasonTitle(holder, seasons.get(selectedPosition));

      initialized = true;
    }
    holder.selectSeason.setOnClickListener(view -> {
      SeasonChooserFragment seasonChooserFragment = new SeasonChooserFragment(seasons, selectedPosition, season -> {
        updateSeasonTitle(holder, season);

        selectedPosition = seasons.indexOf(season);

        onSeasonChooseListener.onSeasonChoose(season);
      });
      seasonChooserFragment.show(activity.getSupportFragmentManager(), seasonChooserFragment.getTag());
    });
  }

  private void updateSeasonTitle(SeasonChooserItemHolder holder, int season) {
    holder.season.setText(String.format("%s %d", activity.getResources().getString(R.string.season), season));
  }

  @Override
  public void unbind(@NonNull SeasonChooserItemHolder holder) {
    super.unbind(holder);

    holder.selectSeason.setOnClickListener(null);
  }

  static class SeasonChooserItemHolder extends EpoxyHolder {

    TextView season;
    MaterialButton selectSeason;

    @Override
    protected void bindView(@NonNull View itemView) {
      season = itemView.findViewById(R.id.season);
      selectSeason = itemView.findViewById(R.id.select_season);
    }
  }

}
