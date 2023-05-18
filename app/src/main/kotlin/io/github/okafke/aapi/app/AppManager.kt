package io.github.okafke.aapi.app

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.reflect.TypeToken
import io.github.okafke.aapi.aidl.Node
import io.github.okafke.aapi.app.input.InputService
import io.github.okafke.aapi.app.service.AApiOverlayService
import io.github.okafke.aapi.app.tree.DefaultTreeMapper
import io.github.okafke.aapi.app.tree.TreeMapper
import io.github.okafke.aapi.app.util.FileHelper
import io.github.okafke.json.Exclude
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("StaticFieldLeak")
object AppManager {
    const val FILE_NAME = "appmap.json"
    private val mapType: Type = object: TypeToken<Map<String, Node>>() {}.type
    private lateinit var context: Context
    private val initialized = AtomicBoolean()
    private val apps = ConcurrentHashMap<String, Node>()

    fun getApps(): Map<String, Node> {
        return apps
    }

    fun getTree(inputs: InputService, treeMapper: TreeMapper = DefaultTreeMapper()): Array<Node> {
        return treeMapper.map(apps.values.toTypedArray(), inputs.inputs).children
    }

    fun addApp(packageName: String, node: Node, save: Boolean = true) {
        println("Adding App $packageName")
        apps[packageName] = node
        node.callbackInApi = Runnable {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent == null) {
                println("Launch Intent for package $packageName was null!")
            } else {
                println("Launching package $packageName")
                AApiOverlayService.instance?.startActivity(intent)
            }
        }

        if (save) {
            saveApps()
        }
    }

    fun init(context: Context) {
        synchronized(initialized) {
            if (!initialized.getAndSet(true)) {
                this.context = context
                try {
                    if (FileHelper.isFilePresent(context, FILE_NAME)) {
                        val json = FileHelper.read(context, FILE_NAME)
                        println("Reading $FILE_NAME: $json")
                        val map: Map<String, Node> = Exclude.GSON.fromJson(json, mapType)

                        map.entries.forEach { entry -> addApp(entry.key, entry.value, save = false) }
                    } else {
                        println("File $FILE_NAME does not exist")
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                // remove old apps
                apps.entries.removeIf { entry -> context.packageManager.getLaunchIntentForPackage(entry.key) == null }
                saveApps()
            }
        }
    }

    private fun saveApps() {
        synchronized(initialized) {
            val json = Exclude.GSON.toJson(apps, mapType)
            println("Saving $json")
            FileHelper.create(context, FILE_NAME, json)
        }
    }

}