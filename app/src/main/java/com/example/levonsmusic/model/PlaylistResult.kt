package com.example.levonsmusic.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class PlaylistResult(
    val playlist: List<PlaylistBean>
) : ApiResult()

@Parcelize
data class PlaylistBean(
    val tracks: List<TrackBean>,
    val trackIds: List<TrackIdBean>?,
    val creator: CreatorBean,
    val name: String,
    val coverImgUrl: String,
    val trackCount: Int,
    val id: Long,
    val playCount: Long,
    val description: String?,
    val shareCount: Int,
    val commentCount: Int
) : Parcelable

@Keep
data class TrackBean(
    val name: String,
    val id: Long,
    val mv: Long,
    val ar: List<Ar>,
    val al: Al,
) : Serializable

@Keep
data class TrackIdBean(
    val id: Int = 0,
    val v: Int = 0,
    val alg: String? = null
) : Serializable

@Keep
data class CreatorBean(
    val userId: Long,
    val avatarUrl: String,
    val nickname: String
) : Serializable
