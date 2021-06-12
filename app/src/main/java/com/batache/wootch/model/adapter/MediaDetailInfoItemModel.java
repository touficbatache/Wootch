package com.batache.wootch.model.adapter;

import android.app.Activity;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.batache.wootch.R;
import com.batache.wootch.util.DimensionUtils;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import butter.droid.base.content.preferences.DefaultQuality;
import butter.droid.base.providers.media.models.Media;
import butter.droid.base.providers.media.models.Movie;
import butter.droid.base.providers.media.models.Show;
import butter.droid.base.torrent.Magnet;
import butter.droid.base.torrent.TorrentHealth;
import butter.droid.base.utils.SortUtils;

@EpoxyModelClass(layout = R.layout.item_media_detail_info)
public abstract class MediaDetailInfoItemModel extends EpoxyModelWithHolder<MediaDetailInfoItemModel.MediaDetailInfoItemHolder> {

  @EpoxyAttribute
  Activity activity;

  @EpoxyAttribute
  Media media;

  private Magnet mMagnet;
  private int synopsisMaxLines;

  private boolean initialized = false;

  @Override
  public void bind(@NonNull MediaDetailInfoItemHolder holder) {
    super.bind(holder);

    new Picasso.Builder(activity)
        .build()
        .load(DimensionUtils.resizeImageUrl(media.image, DimensionUtils.ImageSize.MINI))
        .into(holder.coverImage);

    holder.title.setText(media.title);
    if (!media.rating.equals("-1")) {
      Double rating = Double.parseDouble(media.rating);
      holder.rating.setProgress(rating.intValue());
      holder.rating.setContentDescription("Rating: " + rating.intValue() + " out of 10");
      holder.rating.setVisibility(View.VISIBLE);
    } else {
      holder.rating.setVisibility(View.GONE);
    }

    if (media instanceof Show) {
      Show show = (Show) media;

      String metaDataStr = media.year;
      if (!TextUtils.isEmpty(show.runtime)) {
        metaDataStr += " • ";
        metaDataStr += (show.runtime + " " + activity.getString(R.string.minutes));
      }

      if (!TextUtils.isEmpty(media.genre)) {
        metaDataStr += " • ";
        metaDataStr += media.genre;
      }

      holder.meta.setText(metaDataStr);

      if (!initialized) {
        if (!TextUtils.isEmpty(show.synopsis)) {
          synopsisMaxLines = holder.synopsis.getMaxLines();

          holder.synopsis.setText(show.synopsis);
          holder.synopsis.post(() -> {
            boolean ellipsized = false;
            Layout layout = holder.synopsis.getLayout();
            if (layout == null) return;
            int lines = layout.getLineCount();
            if (lines > 0) {
              int ellipsisCount = layout.getEllipsisCount(lines - 1);
              if (ellipsisCount > 0) {
                ellipsized = true;
              }
            }
            holder.readMore.setVisibility(ellipsized ? View.VISIBLE : View.GONE);
          });
        } else {
          holder.synopsis.setClickable(false);
          holder.readMore.setVisibility(View.GONE);
        }
        
        initialized = true;
      }
    }

//      watchTrailer.setVisibility(media.trailer == null || media.trailer.isEmpty() ? View.GONE : View.VISIBLE);

//    mSubtitles.setFragmentManager(getActivity().getSupportFragmentManager());
//    mQuality.setFragmentManager(getActivity().getSupportFragmentManager());
//    mSubtitles.setTitle(R.string.subtitles);
//    mQuality.setTitle(R.string.quality);
//
//    mSubtitles.setText(R.string.loading_subs);
//    mSubtitles.setClickable(false);

//    if (providerManager.hasCurrentSubsProvider()) {
//      providerManager.getCurrentSubsProvider().getList(media, new SubsProvider.Callback() {
//        @Override
//        public void onSuccess(Map<String, String> subtitles) {
//          if (!mAttached) return;
//
//          if (subtitles == null) {
//            ThreadUtils.runOnUiThread(new Runnable() {
//              @Override
//              public void run() {
//                mSubtitles.setText(R.string.no_subs_available);
//              }
//            });
//            return;
//          }
//
//          media.subtitles = subtitles;
//
//          String[] languages = subtitles.keySet().toArray(new String[subtitles.size()]);
//          Arrays.sort(languages);
//          final String[] adapterLanguages = new String[languages.length + 1];
//          adapterLanguages[0] = "no-subs";
//          System.arraycopy(languages, 0, adapterLanguages, 1, languages.length);
//
//          String[] readableNames = new String[adapterLanguages.length];
//          for (int i = 0; i < readableNames.length; i++) {
//            String language = adapterLanguages[i];
//            if (language.equals("no-subs")) {
//              readableNames[i] = getString(R.string.no_subs);
//            } else {
//              Locale locale = LocaleUtils.toLocale(language);
//              readableNames[i] = locale.getDisplayName(locale);
//            }
//          }
//
//          mSubtitles.setListener(new OptionSelector.SelectorListener() {
//            @Override
//            public void onSelectionChanged(int position, String value) {
//              onSubtitleLanguageSelected(adapterLanguages[position]);
//            }
//          });
//          mSubtitles.setData(readableNames);
//          ThreadUtils.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//              mSubtitles.setClickable(true);
//            }
//          });
//
//          String defaultSubtitle = PrefUtils.get(mSubtitles.getContext(), Prefs.SUBTITLE_DEFAULT, null);
//          if (subtitles.containsKey(defaultSubtitle)) {
//            onSubtitleLanguageSelected(defaultSubtitle);
//            mSubtitles.setDefault(Arrays.asList(adapterLanguages).indexOf(defaultSubtitle));
//          } else {
//            onSubtitleLanguageSelected("no-subs");
//            mSubtitles.setDefault(Arrays.asList(adapterLanguages).indexOf("no-subs"));
//          }
//        }
//
//        @Override
//        public void onFailure(Exception e) {
//          mSubtitles.setData(new String[0]);
//          mSubtitles.setClickable(true);
//        }
//      });
//    } else {
//      mSubtitles.setClickable(false);
//      mSubtitles.setText(R.string.no_subs_available);
//    }

    if (media instanceof Movie) {
      Movie movie = (Movie) media;

      String metaDataStr = media.year;
      if (!TextUtils.isEmpty(movie.runtime)) {
        metaDataStr += " • ";
        metaDataStr += (movie.runtime + " " + activity.getString(R.string.minutes));
      }

      if (!TextUtils.isEmpty(media.genre)) {
        metaDataStr += " • ";
        metaDataStr += media.genre;
      }

      holder.meta.setText(metaDataStr);

      if (!TextUtils.isEmpty(movie.synopsis)) {
        synopsisMaxLines = holder.synopsis.getMaxLines();

        holder.synopsis.setText(movie.synopsis);
        holder.synopsis.post(() -> {
          boolean ellipsized = false;
          Layout layout = holder.synopsis.getLayout();
          if (layout == null) return;
          int lines = layout.getLineCount();
          if (lines > 0) {
            int ellipsisCount = layout.getEllipsisCount(lines - 1);
            if (ellipsisCount > 0) {
              ellipsized = true;
            }
          }
          holder.readMore.setVisibility(ellipsized ? View.VISIBLE : View.GONE);
        });
      } else {
        holder.synopsis.setClickable(false);
        holder.readMore.setVisibility(View.GONE);
      }

      if (!TextUtils.isEmpty(movie.certification)) {
        holder.certification.setVisibility(View.VISIBLE);
        holder.certification.setText(movie.certification);
      }

      if (movie.torrents.size() > 0) {
        final String[] qualities = movie.torrents.get("en").keySet().toArray(new String[movie.torrents.size()]);
        SortUtils.sortQualities(qualities);
        String defaultQuality = DefaultQuality.get(activity, Arrays.asList(qualities));

        if (holder.health.getVisibility() == View.GONE) {
          holder.health.setVisibility(View.VISIBLE);
        }

        TorrentHealth health = TorrentHealth.calculate(movie.torrents.get("en").get(defaultQuality).getSeeds(), movie.torrents.get("en").get(defaultQuality).getPeers());
        holder.health.setImageResource(health.getImageResource());

        if (mMagnet == null) {
          mMagnet = new Magnet(activity, movie.torrents.get("en").get(defaultQuality).getUrl());
        }
        mMagnet.setUrl(movie.torrents.get("en").get(defaultQuality).getUrl());

        holder.openMagnet.setVisibility(mMagnet.canOpen() ? View.VISIBLE : View.GONE);
        holder.openMagnet.setOnClickListener(view -> mMagnet.open(activity));

        holder.health.setOnClickListener(view -> {
          int seeds = movie.torrents.get("en").get(defaultQuality).getSeeds();
          int peers = movie.torrents.get("en").get(defaultQuality).getPeers();
          TorrentHealth health1 = TorrentHealth.calculate(seeds, peers);

          final Snackbar snackbar = Snackbar.make(holder.itemView, activity.getString(R.string.health_info, activity.getString(health1.getStringResource()), seeds, peers), Snackbar.LENGTH_LONG);
          snackbar.setAction(R.string.close, v -> snackbar.dismiss());
          snackbar.show();
        });
//
//      mQuality.setData(qualities);
//      mQuality.setListener(new OptionSelector.SelectorListener() {
//        @Override
//        public void onSelectionChanged(int position, String value) {
//          mSelectedQuality = value;
//          renderHealth();
//          updateMagnet();
//        }
//      });
//
//      String quality = DefaultQuality.get(mActivity, Arrays.asList(qualities));
//      int qualityIndex = Arrays.asList(qualities).indexOf(quality);
//      if (qualityIndex == -1) {
//        qualityIndex = 0;
//        quality = qualities[0];
//      }
//      mSelectedQuality = quality;
//      mQuality.setText(mSelectedQuality);
//      mQuality.setDefault(qualityIndex);
//
//      renderHealth();
//      updateMagnet();
      }
    }

    holder.readMore.setOnClickListener(view -> {
      if (holder.synopsis.getMaxLines() != Integer.MAX_VALUE) {
        holder.synopsis.setMaxLines(Integer.MAX_VALUE);
        holder.readMore.setText(activity.getResources().getText(R.string.less));
      } else {
        holder.synopsis.setMaxLines(synopsisMaxLines);
        holder.readMore.setText(activity.getResources().getText(R.string.more));
      }
    });
  }

  @Override
  public void unbind(@NonNull MediaDetailInfoItemHolder holder) {
    super.unbind(holder);

    holder.openMagnet.setOnClickListener(null);
    holder.health.setOnClickListener(null);
    holder.readMore.setOnClickListener(null);
  }

  static class MediaDetailInfoItemHolder extends EpoxyHolder {

    View itemView;
    ImageView coverImage;
    TextView title;
    ImageView health;
    TextView certification, meta, synopsis, readMore;
    ImageButton openMagnet;
    RatingBar rating;
//    OptionSelector subtitles;
//    OptionSelector quality;

    @Override
    protected void bindView(@NonNull View itemView) {
      this.itemView = itemView;
      coverImage = itemView.findViewById(R.id.cover_image);
      title = itemView.findViewById(R.id.title);
      health = itemView.findViewById(R.id.health);
      certification = itemView.findViewById(R.id.certification);
      meta = itemView.findViewById(R.id.meta);
      synopsis = itemView.findViewById(R.id.synopsis);
      readMore = itemView.findViewById(R.id.read_more);
      openMagnet = itemView.findViewById(R.id.magnet);
      rating = itemView.findViewById(R.id.rating);
    }
  }

}
