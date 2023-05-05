package io.github.okafke.aapi.aidl

import java.util.*

/**
 * This part of a [Node] will not be serialized and is only for the AccessibilityApi.
 */
open class INode(val input2Child: MutableMap<Input, Node> = LinkedHashMap(),
                 val mChildren : MutableList<Node> = LinkedList(),
                 var parent: Node? = null,
                 var callbackInApi: Runnable? = null)
