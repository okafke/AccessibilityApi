package io.github.okafke.aapi.client.tree

import android.app.Activity
import android.content.Context

class UiAction(private val activity: Activity, info: NodeInfo, private val onSelected: Runnable)
    : AbstractNode(info) {
    override fun onSelected(ctx: Context) {
        activity.runOnUiThread {
            onSelected.run()
        }
    }
}
