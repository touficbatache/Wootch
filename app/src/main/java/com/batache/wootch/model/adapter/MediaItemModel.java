package com.batache.wootch.model.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.batache.wootch.R;
import com.batache.wootch.ui.controller.WootchController;
import com.batache.wootch.util.DimensionUtils;
import com.squareup.picasso.Picasso;

import butter.droid.base.providers.media.models.Media;

@EpoxyModelClass(layout = R.layout.item_media_item)
public abstract class MediaItemModel extends EpoxyModelWithHolder<MediaItemModel.MediaItemHolder> {

  @EpoxyAttribute
  Context context;

  @EpoxyAttribute
  Media item;

  @EpoxyAttribute
  int position;

  @EpoxyAttribute
  WootchController.OnMediaItemClickListener onMediaItemClickListener;

  private boolean initialized = false;

  @Override
  public void bind(@NonNull MediaItemHolder holder) {
    super.bind(holder);

    if(initialized) {
      return;
    }

    if (item.image != null && !item.image.isEmpty()) {
      new Picasso.Builder(context)
          .build()
          .load(DimensionUtils.resizeImageUrl(item.image, DimensionUtils.ImageSize.TINY))
          .into(holder.coverImage);
    }

    holder.title.setText(item.title);
    holder.year.setText(item.year);

    holder.itemView.setOnClickListener(view -> onMediaItemClickListener.onItemClick(view, null, item, position, holder.coverImage));

    initialized = true;
  }

  @Override
  public void unbind(@NonNull MediaItemHolder holder) {
    super.unbind(holder);

    holder.itemView.setOnClickListener(null);
  }

  static class MediaItemHolder extends EpoxyHolder {

    View itemView;
    ImageView coverImage;
    TextView title, year;

    @Override
    protected void bindView(@NonNull View itemView) {
      this.itemView = itemView;
      coverImage = itemView.findViewById(R.id.cover_image);
      title = itemView.findViewById(R.id.title);
      year = itemView.findViewById(R.id.year);
    }
  }

}
