package io.github.okafke.aapi.app.tree

import io.github.okafke.aapi.aidl.Node
import io.github.okafke.aapi.aidl.Input

interface TreeMapper {
    fun map(tree: Array<Node>, inputs: List<Input>): Node

}