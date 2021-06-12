package com.batache.wootch.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.batache.wootch.app.base.BaseController;
import com.batache.wootch.model.adapter.EpisodeItemModel_;
import com.batache.wootch.model.adapter.MediaDetailInfoItemModel_;
import com.batache.wootch.model.adapter.MediaItemModel_;
import com.batache.wootch.model.adapter.MediaSectionModel_;
import com.batache.wootch.model.adapter.PlayItemModel_;
import com.batache.wootch.model.adapter.ProviderChipModel_;
import com.batache.wootch.model.adapter.SeasonChooserItemModel_;

import java.util.ArrayList;
import java.util.List;

import butter.droid.base.providers.media.models.Episode;
import butter.droid.base.providers.media.models.Media;

public class WootchController extends BaseController {

  public void addProviderChips(Context context) {
    data.add(
        new ProviderChipModel_()
            .context(context)
    );

    requestModelBuild();
  }

  public void addMediaSection(Context context, String title, View.OnClickListener onClickListener, OnMediaItemClickListener onMediaItemClickListener, ArrayList<Media> items) {
    changeableData.add(
        new MediaSectionModel_()
            .context(context)
            .title(title)
            .onClickListener(onClickListener)
            .onMediaItemClickListener(onMediaItemClickListener)
            .items(items)
    );

    requestModelBuild();
  }

  public void addMediaItems(Context context, int oldItemsSize, ArrayList<Media> items, OnMediaItemClickListener onMediaItemClickListener) {
    for (int i = 0; i < items.size(); i++) {
      Media item = items.get(i);

      changeableData.add(
          new MediaItemModel_()
              .context(context)
              .item(item)
              .position(oldItemsSize + i)
              .onMediaItemClickListener((v, items1, item1, position, imageView) -> onMediaItemClickListener.onItemClick(v, items, item1, position, imageView))
      );
    }

    requestModelBuild();
  }

  public void addMediaDetailInfo(Activity activity, Media media) {
    data.add(
        new MediaDetailInfoItemModel_()
            .activity(activity)
            .media(media)
    );

    requestModelBuild();
  }

  public void addPlay(View.OnClickListener onClickListener) {
    data.add(
        new PlayItemModel_()
            .onClickListener(onClickListener)
    );

    requestModelBuild();
  }

  public void addSeasonChooser(FragmentActivity activity, ArrayList<Integer> seasons, OnSeasonChooseListener onSeasonChooseListener) {
    data.add(
        new SeasonChooserItemModel_()
            .activity(activity)
            .seasons(seasons)
            .onSeasonChooseListener(onSeasonChooseListener)
    );

    requestModelBuild();
  }

  public void addSeason(Context context, List<Episode> episodes, OnEpisodeClickListener onPlayListener) {
    for (Episode episode : episodes) {
      changeableData.add(
          new EpisodeItemModel_()
              .context(context)
              .episode(episode)
              .onPlayListener(onPlayListener)
      );
    }

    requestModelBuild();
  }

  public void clearChangeableData() {
    changeableData.clear();
  }

  public void resetChangeableData() {
    changeableData.clear();

    requestModelBuild();
  }

  public int getItemCount() {
    return data.size();
  }

//  public void addSection(List<Song> songs) {
//    for (Song song : songs) {
//      data.add(new SongModel_()
//          .song(song)
//      );
//    }
//
//    requestModelBuild();
//  }
//
//  public void addExploreSearchResultsSection(Context context, List<Song> songs) {
//    data.add(new SubheaderModel_()
//        .context(context)
//        .title("Top result"));
//
//    data.add(new SongModel_()
//        .song(songs.get(0))
//        .showYoutubeIcon(true)
//    );
//
//    data.add(new SubheaderModel_()
//        .context(context)
//        .title("Songs"));
//
//    for (Song song : songs) {
//      data.add(new SongModel_()
//          .song(song)
//          .showYoutubeIcon(true)
//      );
//    }
//
//    requestModelBuild();
//  }
//
//  public void addSeeMoreSection(Context context, String title, List<Song> songs, String playlistId, boolean showArtist) {
//    data.add(new SubheaderModel_()
//        .context(context)
//        .title(title)
//        .onClickListener(view -> {
//          ExploreFragmentDirections.PlaylistAction action = ExploreFragmentDirections.playlistAction(title, playlistId);
//          Navigation.findNavController(view).navigate(action);
//        })
//    );
//
//    for (Song song : songs) {
//      data.add(new SongModel_()
//          .song(song)
//          .showArtist(showArtist)
//      );
//    }
//
//    requestModelBuild();
//  }
//
//  public void addPlaylistsSection(Context context, String subheaderTitle, List<Playlist> playlists) {
//    data.add(new SubheaderModel_()
//        .context(context)
//        .title(subheaderTitle));
//
//    for (Playlist playlist : playlists) {
//      if (playlist.getId().equals(PLAYLIST_LIKES_ID)) {
//        data.add(new PlaylistModel_()
//            .context(context)
//            .icon(R.drawable.ic_favorite_outlined_black_24dp)
//            .playlist(playlist)
//        );
//      } else if (playlist.getId().equals(PLAYLIST_DOWNLOADS_ID)) {
//        data.add(new PlaylistModel_()
//            .context(context)
//            .icon(R.drawable.ic_cloud_download_outlined_black_24dp)
//            .playlist(playlist)
//        );
//      } else {
//        data.add(new PlaylistModel_()
//            .context(context)
//            .playlist(playlist)
//        );
//      }
//    }
//
//    requestModelBuild();
//  }
//
//  public void addPlaylistSection(String playlistId, List<Song> songs) {
//    for (int i = 0; i < songs.size(); i++) {
//      data.add(new SongModel_()
//          .song(songs.get(i))
//          .playlistId(playlistId)
//          .index(i)
//      );
//    }
//
//    requestModelBuild();
//  }
//
//  public void addDownloadingSection(Context context, List<Download> downloads) {
//    for (int i = 0; i < downloads.size(); i++) {
//      data.add(new DownloadingSongModel_()
//          .context(context)
//          .download(downloads.get(i))
//      );
//    }
//
//    requestModelBuild();
//  }
//
//  public void addSearchSection(String playlistId, List<Song> songs, String query) {
//    for (int i = 0; i < songs.size(); i++) {
//      Song song = songs.get(i);
//      if (song.getTitle().toLowerCase().contains(query.toLowerCase()) ||
//          song.getArtist().toLowerCase().contains(query.toLowerCase())) {
//        data.add(new SongModel_()
//            .song(songs.get(i))
//            .playlistId(playlistId)
//            .index(i)
//        );
//      }
//    }
//
//    requestModelBuild();
//  }

  public interface OnMediaItemClickListener {
    void onItemClick(View v, List<Media> items, Media item, int position, ImageView imageView);
  }

  public interface OnEpisodeClickListener {
    void onPlay(Episode episode);
  }

  public interface OnSeasonChooseListener {
    void onSeasonChoose(Integer season);
  }
}