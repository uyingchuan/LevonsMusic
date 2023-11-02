package com.example.levonsmusic.util

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface MMKVOwner {
    val kv: MMKV get() = defaultMMKV

    companion object {
        private val defaultMMKV = MMKV.defaultMMKV()
    }
}

class MMKVParcelable<T : Parcelable?>(private val key: String, private val valueRawType: Class<T>) :
    ReadWriteProperty<MMKVOwner, T?> {

    override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): T? {
        return thisRef.kv.decodeParcelable(findKey(property), valueRawType)
    }

    override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: T?) {
        thisRef.kv.encode(findKey(property), value)
    }

    private fun findKey(property: KProperty<*>): String {
        return key.ifEmpty { property.name }
    }
}