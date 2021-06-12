package com.batache.wootch.model.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.airbnb.epoxy.EpoxyRecyclerView;
import com.batache.wootch.R;
import com.batache.wootch.ui.controller.WootchController;

import java.util.ArrayList;

import butter.droid.base.providers.media.models.Media;

@EpoxyModelClass(layout = R.layout.item_media_section)
public abstract class MediaSectionModel extends EpoxyModelWithHolder<MediaSectionModel.MediaSectionHolder> {

  @EpoxyAttribute
  Context context;

  @EpoxyAttribute
  String title;

  @EpoxyAttribute
  View.OnClickListener onClickListener;

  @EpoxyAttribute
  WootchController.OnMediaItemClickListener onMediaItemClickListener;

  @EpoxyAttribute
  ArrayList<Media> items;

  @Override
  public void bind(@NonNull MediaSectionHolder holder) {
    super.bind(holder);

    if (onClickListener != null) {
      holder.subheader.setOnClickListener(onClickListener);
    } else {
      holder.subheader.setOnClickListener(null);
    }

    holder.title.setText(title);

    WootchController controller = new WootchController();
    EpoxyRecyclerView recyclerView = holder.recyclerView;
    recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    recyclerView.setController(controller);

    controller.addMediaItems(context, 0, items, onMediaItemClickListener);
  }

  @Override
  public void unbind(@NonNull MediaSectionHolder holder) {
    super.unbind(holder);

    holder.subheader.setOnClickListener(null);
  }

  static class MediaSectionHolder extends EpoxyHolder {

    ConstraintLayout subheader;
    TextView title;
    ImageView more;
    EpoxyRecyclerView recyclerView;

    @Override
    protected void bindView(@NonNull View itemView) {
      subheader = itemView.findViewById(R.id.subheader);
      title = itemView.findViewById(R.id.title);
      more = itemView.findViewById(R.id.more);
      recyclerView = itemView.findViewById(R.id.recyclerView);
    }
  }

}
