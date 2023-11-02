package com.example.levonsmusic.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
class QRCodeKeyResult(val data: QRCodeKeyBean) : ApiResult()

@Keep
class QRCodeKeyBean(val unikey: String)

@Keep
class QRCodeValueResult(val data: QRCodeValueBean) : ApiResult()

@Keep
class QRCodeValueBean(val qrurl: String, val qrimg: String?)

@Keep
class QRCodeAuthResult(val cookie: String) : ApiResult() {
    override fun successful(): Boolean = code == 803
}

@Keep
class AccountInfoResult(val account: AccountBean, val profile: ProfileBean) : ApiResult()

@Keep
@Parcelize
class LoginResult(
    val account: AccountBean,
    val profile: ProfileBean,
    val cookie: String
) : Parcelable

@Keep
@Parcelize
data class AccountBean(
    val id: Long,
    val userName: String,
    val type: Int,
    val status: Int,
    val whitelistAuthority: Int,
    val createTime: Long,
    val tokenVersion: Int,
    val ban: Int,
    val baoyueVersion: Int,
    val donateVersion: Int,
    val vipType: Int,
    val viptypeVersion: Double,
    val anonimousUser: Boolean
) : Parcelable

@Keep
@Parcelize
data class ProfileBean(
    val followed: Boolean,
    val userId: Int,
    val defaultAvatar: Boolean,
    val avatarUrl: String?,
    val nickname: String,
    val birthday: Long,
    val province: Int,
    val accountStatus: Int,
    val vipType: Int,
    val gender: Int,
    val djStatus: Int,
    val mutual: Boolean,
    val authStatus: Int,
    val backgroundImgId: Long,
    val userType: Int,
    val city: Int,
    val backgroundUrl: String?,
    val followeds: Int,
    val follows: Int,
    val eventCount: Int,
    val playlistCount: Int,
    val playlistBeSubscribedCount: Int
) : Parcelable
