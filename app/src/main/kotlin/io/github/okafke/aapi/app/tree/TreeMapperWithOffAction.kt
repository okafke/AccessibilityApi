package io.github.okafke.aapi.app.tree

import android.accessibilityservice.AccessibilityService
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeInfo.FOCUS_INPUT
import io.github.okafke.aapi.app.Constants
import io.github.okafke.aapi.app.R
import io.github.okafke.aapi.aidl.Input
import io.github.okafke.aapi.aidl.Node


class TreeMapperWithOffAction(private val service: AccessibilityService,
                              private val treeHolder: TreeHolder): TreeMapper {
    private val wrapped = treeHolder.mapper
    override fun map(tree: Array<Node>, inputs: List<Input>): Node {
        if (tree.find { node -> node is OffAction } != null) {
            return wrapped.map(tree, inputs)
        }

        println("Adding off action!!")
        val treeList = ArrayList(tree.asList())
        treeList.add(OffAction(service))
        val array = treeList.toTypedArray()
        treeHolder.currentTree = array
        return wrapped.map(array, inputs)
    }

    private class OffAction(service: AccessibilityService) : Node(
        "Off", arrayOf(R.drawable.power),
        arrayOf(Constants.PACKAGE), "Turn AccessibilityApi Off",
        INVALID_ID, emptyArray()) {
        init {
            callbackInApi = Runnable {
                service.disableSelf()
            }
        }
    }
}