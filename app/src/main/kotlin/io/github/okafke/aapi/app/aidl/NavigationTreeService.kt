package io.github.okafke.aapi.app.aidl

import android.os.RemoteException
import io.github.okafke.aapi.aidl.IKeyboard
import io.github.okafke.aapi.aidl.INavigationTreeListener
import io.github.okafke.aapi.aidl.INavigationTreeService
import io.github.okafke.aapi.aidl.Node
import io.github.okafke.aapi.app.overlay.TreeListener
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong


object NavigationTreeService: INavigationTreeService.Stub() {
    private val remoteListeners = ConcurrentHashMap<Long, INavigationTreeListener>()
    private val listeners = CopyOnWriteArrayList<TreeListener>()
    private val id = AtomicLong()
    var inputAmount: Int = 2

    override fun getInputs(): Int {
        return inputAmount
    }

    override fun setNavigationTree(tree: Array<Node>?) {
        tree?.let { listeners.forEach { it.onNewTree(tree) } }
    }

    override fun registerListener(listener: INavigationTreeListener?): Long {
        if (listener != null) {
            val id = this.id.incrementAndGet()
            remoteListeners[id] = listener
            return id
        }

        return -1
    }

    override fun unregisterListener(id: Long) {
        remoteListeners.remove(id)
    }

    override fun getKeyboard(): IKeyboard {
        return Keyboard()
    }

    fun notifyListeners(id: Long) {
        remoteListeners.values.forEach {
            try {
                it.onVerticeSelected(id)
            } catch (e: RemoteException) {
                e.printStackTrace()
                remoteListeners.remove(id)
            }
        }
    }

    fun addLocalListener(listener: TreeListener) {
        listeners.add(listener)
    }

    fun hasListener(listener: TreeListener): Boolean {
        return listeners.contains(listener)
    }

}
