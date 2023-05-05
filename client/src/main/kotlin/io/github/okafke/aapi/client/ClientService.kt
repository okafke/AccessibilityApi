package io.github.okafke.aapi.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import io.github.okafke.aapi.aidl.INavigationTreeService
import io.github.okafke.aapi.client.tree.AbstractNode
import java.util.function.Consumer

class ClientService(val ctx: Context) {
    companion object {
        const val API_PACKAGE = "io.github.okafke.aapi"
        var PACKAGE_NAME = BuildConfig.LIBRARY_PACKAGE_NAME
    }

    private val lock: Any = Object()
    private val callbackHandler = CallbackHandler(ctx)
    private val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            println("Service is connected!")
            val binding = INavigationTreeService.Stub.asInterface(service)
            binding.registerListener(callbackHandler)
            synchronized(lock) {
                navigationService = binding
                val runnable = pendingAction
                if (runnable != null) {
                    println("CurrentTree existed, sending...")
                    runnable.run()
                    pendingAction = null
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            println("Service disconnected!")
            synchronized(lock) {
                navigationService = null
            }
        }
    }

    @Volatile
    var navigationService: INavigationTreeService? = null
    @Volatile
    private var pendingAction: Runnable? = null

    init {
        println("New PackageName: ${ctx.packageName}")
        PACKAGE_NAME = ctx.packageName
        connect(ctx)
    }

    fun isConnected(): Boolean {
        return navigationService != null
    }

    fun setNavigationTree(vertices: List<AbstractNode>) {
        synchronized(lock) {
            val navigationService = this.navigationService
            if (navigationService != null) {
                callbackHandler.clearCallbacks()
                val array = vertices.map { it.serialize(callbackHandler) }.toTypedArray()
                println(array)
                navigationService.setNavigationTree(array)
            } else {
                println("Not connected but setNavigationTree was called, setting currentTree...")
                pendingAction = Runnable { setNavigationTree(vertices) }
            }
        }
    }

    fun setNavigationTree(action: Consumer<INavigationTreeService>) {
        synchronized(lock) {
            val navigationService = this.navigationService
            if (navigationService != null) {
                callbackHandler.clearCallbacks()
                action.accept(navigationService)
            } else {
                println("Not connected but setNavigationTree was called, setting currentTree...")
                pendingAction = Runnable { setNavigationTree(action) }
            }
        }
    }

    fun whenAvailable(action: Consumer<INavigationTreeService>) {
        synchronized(lock) {
            val navigationService = this.navigationService
            if (navigationService != null) {
                action.accept(navigationService)
            } else {
                val currentPendingAction = pendingAction
                pendingAction = Runnable {
                    currentPendingAction?.run()
                    setNavigationTree(action)
                }
            }
        }
    }

    private fun connect(ctx: Context) {
        if (navigationService == null) {
            val intent = Intent(INavigationTreeService::class.java.name)
            intent.setClassName(API_PACKAGE, "$API_PACKAGE.service.AApiConnectionService")
            intent.action = "accessibilityapi.tree"
            intent.setPackage("io.github.okafke.aapi")
            ctx.bindService(intent, connection, 0) // Service.BIND_ABOVE_CLIENT?
        }
    }

}