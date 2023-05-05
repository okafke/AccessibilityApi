package io.github.okafke.aapi.app.tree

import io.github.okafke.aapi.app.R
import io.github.okafke.aapi.app.Constants
import io.github.okafke.aapi.aidl.Input
import io.github.okafke.aapi.aidl.Node
import io.github.okafke.aapi.app.util.last

class TreeMapperWithBackAction: DefaultTreeMapper() {
    override fun processGroup(tree: Node, children: Array<Node>,
                              mappedChildren: LinkedHashMap<Input, Node>) {
        if (children.all {it.isAction()}) {
            val input = mappedChildren.keys.iterator().last()!!
            val node = mappedChildren.remove(input)!!

            tree.mChildren.add(0, node)
            val backNode = Node("Back",
                arrayOf(R.drawable.baseline_undo_24),
                arrayOf(Constants.PACKAGE),
                "Go back",
                Node.INVALID_ID,
                emptyArray())

            // we make use of the fact that the tree will just go back when we click any action
            backNode.callbackInApi = Runnable {  }
            children[0] = backNode
            mappedChildren[input] = backNode
        }
    }

}