package io.github.okafke.aapi.app.tree

import io.github.okafke.aapi.aidl.Node
import io.github.okafke.aapi.api.NodeAdapter
import io.github.okafke.aapi.app.Constants
import io.github.okafke.aapi.app.R

class AApiNodeAdapter: NodeAdapter<Node> {
    override fun getType(): Class<Node> {
        return Node::class.java
    }

    override fun getBackAction(): Node {
        val backNode = Node("Back",
            arrayOf(R.drawable.baseline_undo_24),
            arrayOf(Constants.PACKAGE),
            "Go back",
            Node.INVALID_ID,
            emptyArray())

        // we make use of the fact that the tree will just go back when we click any action
        backNode.callbackInApi = Runnable {  }

        return backNode
    }

    override fun mergeWithBackNode(node: Node): Node {
        return Node.merge(arrayOf(node, getBackAction()))
    }

    override fun aggregate(nodes: Array<Node>, degree: Int): Array<Node> {
        TODO("Not yet implemented")
    }

    override fun setChildren(node: Node, children: Array<Node>) {
        TODO("Not yet implemented")
    }

    override fun addChild(node: Node, child: Node) {
        TODO("Not yet implemented")
    }

    override fun removeChild(node: Node, child: Node) {
        TODO("Not yet implemented")
    }

    override fun getChildren(node: Node): Array<Node> {
        TODO("Not yet implemented")
    }

}