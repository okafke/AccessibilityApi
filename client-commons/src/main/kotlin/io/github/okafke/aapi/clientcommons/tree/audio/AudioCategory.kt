package io.github.okafke.clientcommons.tree.audio

import io.github.okafke.aapi.client.tree.CategoryNode
import io.github.okafke.aapi.client.tree.NodeInfo
import io.github.okafke.aapi.clientcommons.R

object AudioCategory: CategoryNode(
    NodeInfo(
        "Audio", R.drawable.ic_baseline_music_note_24, null,"Audio settings",
        children = arrayOf(MuteAction, VolumeDownAction, VolumeUpAction))
) {
}
