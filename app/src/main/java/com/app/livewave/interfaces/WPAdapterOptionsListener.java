package com.app.livewave.interfaces;

import com.app.livewave.models.ResponseModels.PlayListModel;
import com.app.livewave.models.ResponseModels.Track;

public interface WPAdapterOptionsListener {
    void onTrackOptionsEvent(Track track, boolean isOwner);

    void onTrackListUpdateEvent();

    void onPlaylistUpdateEvent(PlayListModel playListModel);

    void onPlaylistSelectEvent(int position);

    void onPlaylistOptionsEvent(PlayListModel playListModel);
}
