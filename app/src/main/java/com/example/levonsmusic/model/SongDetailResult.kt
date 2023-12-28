package com.example.levonsmusic.model

import androidx.annotation.Keep

data class SongDetailResult(
    val songs: List<SongDetail>
) : ApiResult()

data class HeartbeatListResult(
    val data: List<HeartbeatDetail>
) : ApiResult()

data class HeartbeatDetail(
    val songInfo: SongDetail
)

data class SongDetail(
    val id: Long,
    val name: String,
    val al: Al,
    val ar: List<Ar>,
)

@Keep
data class Ar(
    val id: Long,
    val name: String,
)

@Keep
data class Al(
    val id: Long,
    val name: String,
    val picUrl: String,
)

data class SongUrlResult(
    val data: List<SongUrl>
)

data class SongUrl(val url: String)
