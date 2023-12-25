package com.example.levonsmusic.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class PlaylistResult(
    val playlist: List<PlaylistBean>
) : ApiResult()

@Keep
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
) : Parcelable {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlaylistBean

        if (tracks != other.tracks) return false
        if (trackIds != other.trackIds) return false
        if (creator != other.creator) return false
        if (name != other.name) return false
        if (coverImgUrl != other.coverImgUrl) return false
        if (trackCount != other.trackCount) return false
        if (id != other.id) return false
        if (playCount != other.playCount) return false
        if (description != other.description) return false
        if (shareCount != other.shareCount) return false
        if (commentCount != other.commentCount) return false

        return true
    }
}

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
