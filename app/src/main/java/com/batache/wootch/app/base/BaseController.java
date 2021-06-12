package com.batache.wootch.app.base;

import com.airbnb.epoxy.EpoxyController;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseController extends EpoxyController {

  protected List<EpoxyModelWithHolder> data = new ArrayList<>();
  protected List<EpoxyModelWithHolder> changeableData = new ArrayList<>();

  public void reset() {
    data = new ArrayList<>();
    changeableData = new ArrayList<>();
  }

  public void clear() {
    data = new ArrayList<>();
    changeableData = new ArrayList<>();
    requestModelBuild();
  }

  @Override
  protected void buildModels() {
    long i = 0;

    for (EpoxyModel model : data) {
      model.id(i++);
      model.addTo(this);
    }

    for (EpoxyModel model : changeableData) {
      model.id(i++);
      model.addTo(this);
    }
  }

}