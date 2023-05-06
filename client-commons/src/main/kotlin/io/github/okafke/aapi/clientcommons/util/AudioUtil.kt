package io.github.okafke.clientcommons.util

import android.media.AudioManager

fun getStreamTypes(): List<Int> {
    return listOf(
        AudioManager.STREAM_NOTIFICATION,
        AudioManager.STREAM_ALARM,
        AudioManager.STREAM_MUSIC,
        AudioManager.STREAM_RING,
        AudioManager.STREAM_DTMF,
        AudioManager.STREAM_VOICE_CALL)
}

fun AudioManager.mute(streamType: Int) {
    if (!this.isStreamMute(streamType)) {
        this.adjustStreamVolume(streamType, AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI)
    }
}

fun AudioManager.unmute(streamType: Int) {
    if (this.isStreamMute(streamType)) {
        this.adjustStreamVolume(streamType, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI)
    }
}
