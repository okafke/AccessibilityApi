package io.github.okafke.aapi.app.aidl

import android.annotation.SuppressLint
import android.content.Context
import android.os.RemoteException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.okafke.aapi.aidl.*
import io.github.okafke.aapi.app.overlay.TreeListener
import io.github.okafke.aapi.app.service.AApiOverlayService
import io.github.okafke.aapi.app.util.FileHelper
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

@SuppressLint("StaticFieldLeak")
object NavigationTreeService: INavigationTreeService.Stub() {
    private val remoteListeners = ConcurrentHashMap<Long, INavigationTreeListener>()
    private val listeners = CopyOnWriteArrayList<TreeListener>()
    private val id = AtomicLong()
    var inputAmount: Int = 2

    private const val FILE_NAME = "inputmap.json"
    private val mapType: Type = object: TypeToken<Map<Int?, Input>>() {}.type
    private val inputs = ConcurrentHashMap<Int, Input>()
    private val initialized = AtomicBoolean()

    private lateinit var context: Context

    fun init(context: Context) {
        synchronized(initialized) {
            if (!initialized.getAndSet(true)) {
                this.context = context
                try {
                    if (FileHelper.isFilePresent(context, FILE_NAME)) {
                        val json = FileHelper.read(context, FILE_NAME)
                        val map: Map<Int, Input> = Gson().fromJson(json, mapType)
                        inputs.putAll(map)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun registerInput(id: Int, node: Node) {
        assert(initialized.get())
        val input = inputs.computeIfAbsent(id) { Input(id) }
        if (!input.nodes.contains(node)) {
            input.nodes.add(node)
        }

        saveInputs()
    }

    override fun unregisterInput(id: Int, nodeId: Long) {
        val input = inputs.computeIfAbsent(id) { Input(id) }
        input.nodes.removeIf { node -> node.id == nodeId }
        saveInputs()
    }

    private fun saveInputs() {
        synchronized(initialized) {
            val json = Gson().toJson(inputs, mapType)
            FileHelper.create(context, FILE_NAME, json)
        }
    }

    override fun onInput(id: Int) {
        AApiOverlayService.instance?.inputService?.onInput(inputs[id])
    }

    fun getInput(context: Context, id: Int): Input {
        init(context)
        return inputs.computeIfAbsent(id) { Input(id) }
    }

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
