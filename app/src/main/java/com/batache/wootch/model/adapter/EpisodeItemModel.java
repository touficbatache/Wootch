package com.batache.wootch.model.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.batache.wootch.R;
import com.batache.wootch.ui.controller.WootchController;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;

import butter.droid.base.providers.media.models.Episode;
import butter.droid.base.utils.LocaleUtils;

@EpoxyModelClass(layout = R.layout.item_episode)
public abstract class EpisodeItemModel extends EpoxyModelWithHolder<EpisodeItemModel.EpisodeItemHolder> {

  @EpoxyAttribute
  Context context;

  @EpoxyAttribute
  Episode episode;

  @EpoxyAttribute
  WootchController.OnEpisodeClickListener onPlayListener;

  @Override
  public void bind(@NonNull EpisodeItemHolder holder) {
    super.bind(holder);

    holder.title.setText(String.format("%s. %s", episode.episode, episode.title));
    Date airedDate = new Date((long) episode.aired * 1000);
    holder.aired.setText(new SimpleDateFormat("MMM dd, yyyy", LocaleUtils.getCurrent()).format(airedDate));
    holder.synopsis.setText(episode.overview);

    holder.play.setOnClickListener(view -> onPlayListener.onPlay(episode));
  }

  @Override
  public void unbind(@NonNull EpisodeItemHolder holder) {
    super.unbind(holder);

    holder.play.setOnClickListener(null);
  }

  static class EpisodeItemHolder extends EpoxyHolder {

    View itemView;
    TextView title, aired, synopsis;
    MaterialButton play;

    @Override
    protected void bindView(@NonNull View itemView) {
      this.itemView = itemView;
      title = itemView.findViewById(R.id.title);
      aired = itemView.findViewById(R.id.aired);
      synopsis = itemView.findViewById(R.id.synopsis);
      play = itemView.findViewById(R.id.play);
    }
  }

}
