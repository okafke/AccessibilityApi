package io.github.okafke.clientcommons.tree.audio

import android.content.Context
import android.media.AudioManager
import io.github.okafke.aapi.client.tree.AbstractAction
import io.github.okafke.aapi.client.tree.NodeInfo
import io.github.okafke.aapi.clientcommons.R
import io.github.okafke.clientcommons.util.getStreamTypes

object VolumeDownAction : AbstractAction(
    NodeInfo(
        "VolDown", R.drawable.ic_baseline_volume_down_24, null, "Decreases audio volume."
    )
) {
    override fun onSelected(ctx: Context) {
        val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        for (streamType in getStreamTypes()) {
            audioManager.adjustStreamVolume(streamType, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
        }
    }
}
