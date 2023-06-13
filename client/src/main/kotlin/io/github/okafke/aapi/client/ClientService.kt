package io.github.okafke.aapi.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import io.github.okafke.aapi.aidl.BuildConfig
import io.github.okafke.aapi.aidl.INavigationTreeService
import io.github.okafke.aapi.aidl.Node
import io.github.okafke.aapi.client.tree.AbstractNode
import java.util.function.Consumer


class ClientService(val ctx: Context, val addApp: Boolean = true) {
    companion object {
        const val API_PACKAGE = "io.github.okafke.aapi.app"
        var PACKAGE_NAME = BuildConfig.LIBRARY_PACKAGE_NAME
    }

    private val lock: Any = Object()
    private val callbackHandler = CallbackHandler(ctx)
    var wasConnected = false
    // TODO: something seems to be wrong with this connection object?
    //  during typing sometimes letters were typed twice?
    //  could it be that this object is not disposed properly?
    //  and if a resumed Fragment/Activity calls the ServiceHolder it reconnects?
    //  with the unbind() method this should be fixed, TODO: regress?
    private val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            wasConnected = true
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
                unbind()
            }
        }
    }

    fun unbind() {
        ctx.unbindService(connection)
        callbackHandler.clearCallbacks()
    }

    @Volatile
    var navigationService: INavigationTreeService? = null
    @Volatile
    private var pendingAction: Runnable? = Runnable {
        whenAvailable { api ->
            run {
                println("Add App: $addApp")
                if (addApp) {
                    api.registerApp(ctx.packageName, Node(
                        getAppLabel(ctx),
                        if (ctx.applicationInfo.icon == 0) emptyArray() else arrayOf(ctx.applicationInfo.icon),
                        if (ctx.applicationInfo.icon == 0) emptyArray() else arrayOf(ctx.packageName),
                        "Starts the ${ctx.packageName} app",
                        0, emptyArray()
                    ))
                }
            }
        }
    }

    fun getAppLabel(context: Context): String {
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo = context.packageManager.getApplicationInfo(context.applicationInfo.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("TAG", "The package with the given name cannot be found on the system.")
        }
        return (if (applicationInfo != null) {
            context.packageManager.getApplicationLabel(applicationInfo)
        } else context.packageName).toString()
    }

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
                println("Navigation Tree: $array")
                navigationService.setNavigationTree(array)
            } else {
                println("Not connected but setNavigationTree was called, setting currentTree...")
                val existingAction = pendingAction
                pendingAction = Runnable {
                    existingAction?.run()
                    setNavigationTree(vertices)
                }
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
                val existingAction = pendingAction
                pendingAction = Runnable {
                    existingAction?.run()
                    setNavigationTree(action)
                }
            }
        }
    }

    fun whenAvailable(action: Consumer<INavigationTreeService>) {
        synchronized(lock) {
            val navigationService = this.navigationService
            if (navigationService != null) {
                action.accept(navigationService)
            } else {
                val existingAction = pendingAction
                pendingAction = Runnable {
                    existingAction?.run()
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
            intent.setPackage("io.github.okafke.aapi.app")
            ctx.bindService(intent, connection, 0) // Service.BIND_ABOVE_CLIENT?
        }
    }

}