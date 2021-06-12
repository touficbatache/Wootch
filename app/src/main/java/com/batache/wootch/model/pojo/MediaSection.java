package com.batache.wootch.model.pojo;

import java.util.ArrayList;

import butter.droid.base.providers.media.MediaProvider;
import butter.droid.base.providers.media.models.Media;

public class MediaSection {

  private String label;
  private MediaProvider.Filters.Sort type;
  private ArrayList<Media> items;

  public MediaSection(String sectionLabel, MediaProvider.Filters.Sort sectionType, ArrayList<Media> sectionItems) {
    label = sectionLabel;
    type = sectionType;
    items = sectionItems;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public MediaProvider.Filters.Sort getType() {
    return type;
  }

  public void setType(MediaProvider.Filters.Sort type) {
    this.type = type;
  }

  public ArrayList<Media> getItems() {
    return items;
  }

  public void setItems(ArrayList<Media> items) {
    this.items = items;
  }
}
