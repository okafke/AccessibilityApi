package io.github.okafke.aapi.app.aidl

import io.github.okafke.aapi.aidl.IKeyboard
import io.github.okafke.aapi.aidl.INavigationTreeListener
import io.github.okafke.aapi.aidl.INavigationTreeService
import io.github.okafke.aapi.aidl.Node

// TODO: not sure why we cant just return the NavigationTreeService object?
class DelegateNavigationTreeService: INavigationTreeService.Stub() {
    override fun getInputs(): Int {
        return NavigationTreeService.inputs
    }

    override fun setNavigationTree(tree: Array<Node>?) {
        NavigationTreeService.setNavigationTree(tree)
    }

    override fun registerListener(listener: INavigationTreeListener?): Long {
        return NavigationTreeService.registerListener(listener)
    }

    override fun unregisterListener(id: Long) {
        NavigationTreeService.unregisterListener(id)
    }

    override fun getKeyboard(): IKeyboard {
        return NavigationTreeService.keyboard
    }

}