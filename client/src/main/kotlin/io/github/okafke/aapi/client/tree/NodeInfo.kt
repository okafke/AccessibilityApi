package io.github.okafke.aapi.client.tree

fun NodeInfo(name: String,
             drawableId: Int,
             drawablePackageName: String? = null,
             description: String = "Your description here...",
             children: Array<AbstractNode> = emptyArray()): NodeInfo {
    return NodeInfo(name, arrayOf(drawableId), drawablePackageName, description, children)
}

open class NodeInfo(
    var name: String,
    var drawableId: Array<Int> = emptyArray(),
    var drawablePackageName: String? = null,
    var description: String = "Your description here...",
    var children: Array<AbstractNode> = emptyArray()
)
