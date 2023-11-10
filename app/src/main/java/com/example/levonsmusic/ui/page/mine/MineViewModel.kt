package com.example.levonsmusic.ui.page.mine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import com.example.levonsmusic.model.PlaylistBean
import com.example.levonsmusic.model.PlaylistResult
import com.example.levonsmusic.network.MusicApiService
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
}