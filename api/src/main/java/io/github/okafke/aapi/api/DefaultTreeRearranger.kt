package io.github.okafke.aapi.api

class DefaultTreeRearranger: TreeRearranger {
    override fun <T> rearrange(tree: Array<T>, degree: Int, adapter: NodeAdapter<T>): Array<T> {
        val parents = ArrayDeque<T?>()
        parents.add(null)
        return rearrangeInternal(parents, tree, degree, adapter)
    }

    private fun <T> rearrangeInternal(parents: ArrayDeque<T?>, treeIn: Array<T>, degree: Int, adapter: NodeAdapter<T>): Array<T> {
        var tree = treeIn
        val superParent = if (parents.size > 1) parents[1] else null
        if (superParent != null) {
            val parentChildren = adapter.getChildren(superParent)
            if (parentChildren.size < degree) {
                val nodesToMoveUp = adapter.getNodesToMoveUpwards(tree, degree - parentChildren.size)
                val list = ArrayList(tree.asList())
                nodesToMoveUp.forEach {
                    adapter.addChild(superParent, it)
                    list.remove(it)
                }

                @Suppress("UNCHECKED_CAST")
                tree = list.toArray(java.lang.reflect.Array.newInstance(adapter.getType(), 0) as Array<T>)
            }
        }

        if (tree.size > degree) {
            tree = adapter.aggregate(tree, degree)
        }

        for (node in tree) {
            parents.addFirst(node)
            adapter.setChildren(node, rearrangeInternal(parents, adapter.getChildren(node), degree, adapter))
            parents.removeFirst()
        }

        return tree
    }

}