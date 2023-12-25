package com.example.levonsmusic.ui.page.playlist

import androidx.lifecycle.MutableLiveData
import com.example.levonsmusic.model.PlaylistBean
import com.example.levonsmusic.model.PlaylistDetailResult
import com.example.levonsmusic.model.SongDetail
import com.example.levonsmusic.network.MusicApiService
import com.example.levonsmusic.util.BaseViewModel
import com.example.levonsmusic.util.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(private val api: MusicApiService) : BaseViewModel() {
    lateinit var playlistBean: PlaylistBean
    val liveData = MutableLiveData<RequestState<PlaylistDetailResult>>()

    val songList = mutableListOf<SongDetail>()

    fun getPlaylist() {
        launch(
            liveData = liveData,
            requestBlock = {
                api.getPlaylistDetail(playlistBean.id)
            }
        ) { result ->
            val trackIds = result.playlist.trackIds
            val ids = StringBuffer()
            if (trackIds != null) {
                val size = trackIds.size
                for (i in 0 until size) {
                    if (i == size - 1) {
                        ids.append(trackIds[i].id)
                    } else {
                        ids.append(trackIds[i].id).append(",")
                    }
                }
            }
            songList.addAll(api.getSongDetail(ids.toString()).songs)
        }
    }
}
