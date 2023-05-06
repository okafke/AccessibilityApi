package io.github.okafke.clientcommons.tree.audio

import android.content.Context
import android.media.AudioManager
import io.github.okafke.aapi.client.tree.AbstractAction
import io.github.okafke.aapi.client.tree.NodeInfo
import io.github.okafke.aapi.clientcommons.R
import io.github.okafke.clientcommons.util.getStreamTypes
import io.github.okafke.clientcommons.util.unmute

object VolumeUpAction : AbstractAction(
    NodeInfo(
        "VolUp", R.drawable.ic_baseline_volume_up_24, null, "Increases audio volume."
    )
) {
    override fun onSelected(ctx: Context) {
        val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        for (streamType in getStreamTypes()) {
            audioManager.unmute(streamType)
            audioManager.adjustStreamVolume(streamType, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
        }
    }
}
