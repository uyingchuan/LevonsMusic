package com.example.levonsmusic.ui.page.login

import com.example.levonsmusic.model.LoginResult
import com.example.levonsmusic.util.MMKVOwner
import com.example.levonsmusic.util.MMKVParcelable

object LoginAccount : MMKVOwner {
    var data by MMKVParcelable("", LoginResult::class.java)
}