package com.batache.wootch.model.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.batache.wootch.R;
import com.google.android.material.button.MaterialButton;

@EpoxyModelClass(layout = R.layout.item_play)
public abstract class PlayItemModel extends EpoxyModelWithHolder<PlayItemModel.PlayItemHolder> {

  @EpoxyAttribute
  View.OnClickListener onClickListener;

  @Override
  public void bind(@NonNull PlayItemHolder holder) {
    super.bind(holder);

    holder.play.setOnClickListener(onClickListener);
  }

  @Override
  public void unbind(@NonNull PlayItemHolder holder) {
    super.unbind(holder);

    holder.play.setOnClickListener(null);
  }

  static class PlayItemHolder extends EpoxyHolder {

    MaterialButton play;

    @Override
    protected void bindView(@NonNull View itemView) {
      play = itemView.findViewById(R.id.play);
    }
  }

}
