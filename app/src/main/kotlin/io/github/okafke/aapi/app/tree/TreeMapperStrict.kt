package io.github.okafke.aapi.app.tree

import io.github.okafke.aapi.aidl.Input
import io.github.okafke.aapi.aidl.Node

open class TreeMapperStrict: TreeMapper {
    override fun map(tree: Array<Node>, inputs: List<Input>): Node {
        val root = Node.root(tree)
        println(tree.contentToString())
        rearrangeAndMap(root, inputs, true)
        return root
    }

    protected open fun rearrangeAndMap(tree: Node, inputs: List<Input>, isTopLevel: Boolean) {
        tree.resetMChildren()
        tree.input2Child.clear()
        onProcess(tree, inputs, isTopLevel)
        if (tree.mChildren.size > inputs.size) {
            tree.mChildren.forEach{ println("Tree has too many: ${it.name}")}
            throw IllegalStateException("Node $tree has ${tree.mChildren.size} children, but is only allowed ${inputs.size}")
        }

        inputs.forEachIndexed { index, input ->
            if (index < tree.mChildren.size) {
                val child = tree.mChildren[index]
                tree.input2Child[input] = child
            }
        }

        tree.children.forEach {
            rearrangeAndMap(it, inputs, false)
        }
    }

    protected open fun onProcess(tree: Node, inputs: List<Input>, isTopLevel: Boolean) {
        // to be implemented by sub-classes
    }

}