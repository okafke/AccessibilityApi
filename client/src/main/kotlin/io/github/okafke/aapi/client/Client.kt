package io.github.okafke.aapi.client

import android.content.Context
import io.github.okafke.aapi.client.json.ServiceHolder

object Client {
    fun isConnected(context: Context): Boolean {
        if (!ServiceHolder.isInitialized()) {
            ServiceHolder.init(context)
        }

        return ServiceHolder.clientService.isConnected()
    }

}