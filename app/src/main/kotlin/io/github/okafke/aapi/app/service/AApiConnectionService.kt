package io.github.okafke.aapi.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import io.github.okafke.aapi.app.aidl.DelegateNavigationTreeService
import io.github.okafke.aapi.app.aidl.NavigationTreeService
import io.github.okafke.aapi.app.util.AApiUtil

class AApiConnectionService : Service() {
    init {
        println("AApiConnectionService created")
    }

    override fun onBind(intent: Intent): IBinder {
        println("AApiConnectionService.onBind called!")
        NavigationTreeService.inputAmount = AApiUtil.getInputs(applicationContext)
        return DelegateNavigationTreeService()
    }

}