package com.batache.wootch.app.library;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.batache.wootch.R;
import com.batache.wootch.app.base.ListFragment;

public class LibraryMoreFragment extends ListFragment {

//  private final static String MAX_RESULTS = "50";
//
//  private String playlistId;

  @Override
  public int getLayout() {
    return R.layout.fragment_list;
  }

  @Override
  public void onCreateView() {
//    if (getArguments() != null) {
//      playlistId = HomeMoreFragmentArgs.fromBundle(getArguments()).getPlaylistId();
//    }
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

//    if (!CodeUtils.isEmpty(playlistId)) {
//      getExploreSongs();
//    }
  }

//  private void getExploreSongs() {
//    SongsAPI.getInstance().getSongsFromPlaylist(playlistId, MAX_RESULTS, new SongsAPI.OnSongsFetchedListener() {
//      @Override
//      public void onSuccess(List<Song> fetchedSongs) {
//        mController.reset();
//        mController.addSection(fetchedSongs);
//      }
//
//      @Override
//      public void onFailure(Throwable t) {
//        Toast.makeText(getContext(), "Failed fetching playlist", Toast.LENGTH_SHORT).show();
//      }
//    });
//  }
}
