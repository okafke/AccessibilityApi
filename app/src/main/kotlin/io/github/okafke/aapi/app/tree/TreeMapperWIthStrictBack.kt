package io.github.okafke.aapi.app.tree

import io.github.okafke.aapi.app.Constants
import io.github.okafke.aapi.app.R
import io.github.okafke.aapi.aidl.Input
import io.github.okafke.aapi.aidl.Node
import io.github.okafke.aapi.app.util.last

class TreeMapperWIthStrictBack: TreeMapperStrict() {
    override fun onProcess(tree: Node, inputs: List<Input>, isTopLevel: Boolean) {
        if (!isTopLevel) {
            val backNode = Node("Back",
                arrayOf(R.drawable.baseline_undo_24),
                arrayOf(Constants.PACKAGE),
                "Go back",
                Node.INVALID_ID,
                emptyArray())

            tree.mChildren.add(backNode)
            // we make use of the fact that the tree will just go back when we click any action
            backNode.callbackInApi = Runnable {  }
        }
    }

}