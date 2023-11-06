package com.example.levonsmusic.model

import androidx.annotation.Keep

data class SongResult(
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
