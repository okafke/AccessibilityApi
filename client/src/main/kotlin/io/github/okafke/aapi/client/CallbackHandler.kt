package io.github.okafke.aapi.client

import android.content.Context
import io.github.okafke.aapi.aidl.INavigationTreeListener
import io.github.okafke.aapi.client.tree.NodeCallback
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class CallbackHandler(private val ctx: Context) : INavigationTreeListener.Stub() {
    private val callbacks = ConcurrentHashMap<Long, NodeCallback>()
    private val id = AtomicLong()

    override fun onVerticeSelected(id: Long) {
        val node = callbacks[id]
        if (node != null) {
            println("$node with id $id selected")
            node.onSelected(ctx)
        } else {
            println("No node for id $id found!")
        }
    }

    fun addCallback(callback: NodeCallback): Long {
        val id = id.incrementAndGet()
        callbacks[id] = callback
        return id
    }

    fun delCallback(id: Long) {
        callbacks.remove(id)
    }

    fun clearCallbacks() {
        callbacks.clear()
    }

}