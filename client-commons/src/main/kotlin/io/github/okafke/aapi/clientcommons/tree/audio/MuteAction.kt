package io.github.okafke.clientcommons.tree.audio

import android.content.Context
import android.media.AudioManager
import io.github.okafke.aapi.client.tree.AbstractAction
import io.github.okafke.aapi.client.tree.NodeInfo
import io.github.okafke.aapi.clientcommons.R
import io.github.okafke.clientcommons.util.getStreamTypes
import io.github.okafke.clientcommons.util.mute

object MuteAction: AbstractAction(
    NodeInfo(
    "Mute", R.drawable.ic_baseline_volume_off_24, null, "Mutes all sounds.")
) {
    override fun onSelected(ctx: Context) {
        val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        for (streamType in getStreamTypes()) {
            audioManager.mute(streamType)
        }
    }
}
