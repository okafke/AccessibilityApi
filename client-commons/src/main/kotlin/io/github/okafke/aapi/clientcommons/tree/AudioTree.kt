package io.github.okafke.clientcommons.tree

import io.github.okafke.aapi.annotations.Action
import io.github.okafke.aapi.annotations.Category
import io.github.okafke.aapi.client.json.ServiceHolder
import io.github.okafke.clientcommons.tree.audio.MuteAction
import io.github.okafke.clientcommons.tree.audio.VolumeDownAction
import io.github.okafke.clientcommons.tree.audio.VolumeUpAction


@Category("Audio", "ic_baseline_music_note_24", ["Mute", "VolDown", "VolUp"])
object AudioTree {
    @Action("Mute", "ic_baseline_volume_off_24")
    @JvmStatic
    fun mute() {
        MuteAction.onSelected(ServiceHolder.clientService.ctx)
    }

    @JvmStatic
    @Action("VolDown", "ic_baseline_volume_down_24")
    fun volDown() {
        VolumeDownAction.onSelected(ServiceHolder.clientService.ctx)
    }

    @Action("VolUp", "ic_baseline_volume_up_24")
    @JvmStatic
    fun volUp() {
        VolumeUpAction.onSelected(ServiceHolder.clientService.ctx)
    }

}
