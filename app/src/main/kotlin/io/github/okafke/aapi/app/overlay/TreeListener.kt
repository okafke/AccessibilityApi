package io.github.okafke.aapi.app.overlay

import io.github.okafke.aapi.aidl.Node

@FunctionalInterface
interface TreeListener {
    fun onNewTree(tree: Array<Node>)

}