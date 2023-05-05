package io.github.okafke.aapi.client.tree

import android.content.Context
import io.github.okafke.aapi.aidl.Node
import io.github.okafke.aapi.client.CallbackHandler
import io.github.okafke.aapi.client.ClientService

abstract class AbstractNode(info: NodeInfo) : NodeInfo(
    info.name,
    info.drawableId,
    info.drawablePackageName,
    info.description,
    info.children
), NodeCallback {
    init {
        if (this.drawablePackageName == null) {
            this.drawablePackageName = ClientService.PACKAGE_NAME
        }
    }

    override fun onSelected(ctx: Context) {
        // NOP
    }

    open fun serialize(callbackHandler: CallbackHandler): Node {
        val id = callbackHandler.addCallback(this)
        val packageNames = ArrayList<String>(drawableId.size)
        drawableId.forEach { _ -> drawablePackageName?.let { pn -> packageNames.add(pn) } }

        return Node(name, drawableId, packageNames.toTypedArray(), description, id,
            children.map { ch -> ch.serialize(callbackHandler) }.toTypedArray())
    }

}