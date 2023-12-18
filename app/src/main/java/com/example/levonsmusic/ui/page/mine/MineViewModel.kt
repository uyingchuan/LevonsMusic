package com.example.levonsmusic.ui.page.mine

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import com.example.levonsmusic.component.DragStatus
import com.example.levonsmusic.model.PlaylistBean
import com.example.levonsmusic.model.PlaylistResult
import com.example.levonsmusic.model.SongDetail
import com.example.levonsmusic.network.MusicApiService
import com.example.levonsmusic.player.LevonsPlayerController
import com.example.levonsmusic.player.MusicPlayerMode
import com.example.levonsmusic.ui.page.login.LoginAccount
import com.example.levonsmusic.util.BaseViewModel
import com.example.levonsmusic.util.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(private val api: MusicApiService) : BaseViewModel() {

    // 歌单接口请求状态
    val liveData = MutableLiveData<RequestState<PlaylistResult>>()

    // 创建的歌单
    val personalPlaylist = mutableListOf<PlaylistBean>()

    // 喜欢的音乐歌单
    var favoritePlaylist: PlaylistBean? by mutableStateOf(null)

    // 收藏的歌单
    val collectPlaylist = mutableListOf<PlaylistBean>()

    // 主页顶部拖动的状态
    var dragStatus by mutableStateOf<DragStatus>(DragStatus.Idle)

    // 选中的tab下标
    var selectedTabIndex by mutableIntStateOf(0)

    fun getPlaylist() {
        val uid = LoginAccount.data!!.account.id.toString()
        launch(
            liveData = liveData,
            requestBlock = {
                api.getPlaylist(uid)
            }
        ) { result ->
            val personalList = mutableListOf<PlaylistBean>()
            val collectList = mutableListOf<PlaylistBean>()
            result.playlist.forEach { item ->
                if (item.creator.userId.toString() == uid) {
                    if (item.name == item.creator.nickname + "喜欢的音乐") {
                        favoritePlaylist = item
                    } else {
                        personalList.add(item)
                    }
                } else {
                    collectList.add(item)
                }
            }
            personalPlaylist.addAll(personalList)
            collectPlaylist.addAll(collectList)
        }
    }

    suspend fun playHeartBeatMode() {
        if (favoritePlaylist == null) return
        val trackId = favoritePlaylist!!.id
        val playlistBean = api.getPlaylistDetail(trackId).playlist
        val trackIds = playlistBean.trackIds
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
        val playlist: List<SongDetail> = api.getSongDetail(ids.toString()).songs
        LevonsPlayerController.playMode = MusicPlayerMode.HEARTBEAT
        LevonsPlayerController.startPlaylist(playlist, 0)
        LevonsPlayerController.showMiniPlayer = true
        Log.d("LEVONS", "OVER")
    }
}
