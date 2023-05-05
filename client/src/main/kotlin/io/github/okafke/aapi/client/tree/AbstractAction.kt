package io.github.okafke.aapi.client.tree

import android.content.Context

abstract class AbstractAction(info: NodeInfo) : AbstractNode(info) {
    abstract override fun onSelected(ctx: Context)

}
