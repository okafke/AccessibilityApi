package io.github.okafke.aapi.app.tree

import io.github.okafke.aapi.aidl.Input
import io.github.okafke.aapi.aidl.Node

class TreeHolder(var mapper: TreeMapper) {
    var currentNode = Node.root()
    var currentRootNode = currentNode; private set
    var currentTree : Array<Node> = emptyArray()

    fun setTree(tree: Array<Node>, inputs: List<Input>) {
        currentTree = tree
        currentNode = mapper.map(tree, inputs)
        println("New Tree: $currentNode")
        currentRootNode = currentNode
    }

}