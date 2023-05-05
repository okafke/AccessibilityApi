package io.github.okafke.aapi.app.tree

import io.github.okafke.aapi.aidl.Input
import io.github.okafke.aapi.aidl.Node
import kotlin.math.min

open class DefaultTreeMapper: TreeMapper {
    override fun map(tree: Array<Node>, inputs: List<Input>): Node {
        val root = Node.root(tree)
        println(tree.contentToString())
        rearrangeAndMap(root, inputs)
        return root
    }

    protected open fun rearrangeAndMap(tree: Node, inputs: List<Input>) {
        tree.resetMChildren()
        tree.input2Child.clear()
        while (tree.mChildren.size > inputs.size) {
            var groupSize = min(tree.mChildren.size - inputs.size + 1, inputs.size)
            val children = Array(groupSize) { Node.DUMMY }
            val mappedChildren = LinkedHashMap<Input, Node>()
            while (groupSize-- > 0) {
                val first = tree.mChildren.removeFirst()
                mappedChildren[inputs[groupSize]] = first
                children[groupSize] = first
            }

            // TODO: WHY IS THIS ONLY CALLED HERE??????????????????????????????????????????????????
            processGroup(tree, children, mappedChildren)
            val newParent = Node.merge(children)
            newParent.parent = tree
            newParent.input2Child.putAll(mappedChildren)
            tree.mChildren.add(newParent)
        }

        inputs.forEachIndexed { index, input ->
            if (index < tree.mChildren.size) {
                val child = tree.mChildren[index]
                tree.input2Child[input] = child
            }
        }

        tree.children.forEach {
            rearrangeAndMap(it, inputs)
        }
    }

    protected open fun processGroup(tree: Node, children: Array<Node>,
                               mappedChildren: LinkedHashMap<Input, Node>) {
        // NOP
    }

}