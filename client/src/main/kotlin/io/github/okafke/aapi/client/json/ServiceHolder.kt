package io.github.okafke.aapi.client.json

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.okafke.aapi.client.ClientService
import java.io.InputStreamReader

object ServiceHolder {
    // TODO: Solve this with WeakReferences?
    //  Also this should actually be fine since it will hold the application context most likely
    lateinit var clientService: ClientService
    lateinit var jsonService: JsonTreeService

    @JvmStatic
    val GSON: Gson = GsonBuilder().setPrettyPrinting().create()

    fun init(ctx: Context) {
        clientService = ClientService(ctx)
        jsonService = JsonTreeService(clientService)
    }

    fun refreshConnection(ctx: Context) {

    }

    fun isInitialized(): Boolean {
        return this::clientService.isInitialized
    }

    @JvmStatic
    @Suppress("unused")
    fun setTree(name: String) {
        if (!this::clientService.isInitialized || !this.clientService.isConnected() && this.clientService.wasConnected) {
            val adapter = InputStreamReader(ServiceHolder.javaClass.classLoader?.getResourceAsStream("contextprovider.json")).use {
                GSON.fromJson(it, CallbackAdapter::class.java)
            }

            val contextProvider = CallbackFactory().create(adapter)
            init(contextProvider.get() as Context)
        }

        jsonService.load(name)
    }

}