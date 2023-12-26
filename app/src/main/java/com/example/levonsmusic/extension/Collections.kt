package com.example.levonsmusic.extension

fun <T> List<T>.getNextIndex(index: Int): Int {
    return if (index == this.size - 1) {
        0
    } else {
        index + 1
    }
}

fun <T> List<T>.getPreIndex(index: Int): Int {
    return if (index == 0) {
        this.size - 1
    } else {
        index - 1
    }
}