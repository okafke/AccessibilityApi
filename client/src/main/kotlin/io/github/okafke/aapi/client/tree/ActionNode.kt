package io.github.okafke.aapi.client.tree

import android.content.Context

open class ActionNode(info: NodeInfo, private val onSelected: Runnable) : AbstractNode(info) {
    override fun onSelected(ctx: Context) {
        println("Running $onSelected")
        try {
            onSelected.run()
        } catch (t: Throwable) {
            println("ERROR RUNNING $onSelected")
            t.printStackTrace()
            throw t;
        }
    }
}
