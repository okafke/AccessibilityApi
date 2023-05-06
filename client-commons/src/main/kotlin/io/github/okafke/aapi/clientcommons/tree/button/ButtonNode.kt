package io.github.okafke.clientcommons.tree.button

import android.app.Activity
import android.content.Context
import android.widget.TextView
import io.github.okafke.aapi.client.tree.AbstractAction
import io.github.okafke.aapi.client.tree.NodeInfo

class ButtonNode(val button: TextView,
                 val text: CharSequence = button.text,
                 val drawable: Int = -1,
                 val activity: Activity? = null): AbstractAction(
    NodeInfo(
        text.toString(),
        drawable
    )
) {
    override fun onSelected(ctx: Context) {
        if (activity != null) {
            activity.runOnUiThread {
                onSelected()
            }
        } else {
            onSelected()
        }
    }

    private fun onSelected() {
        if (button.isEnabled) {
            button.performClick()
        }
    }
}