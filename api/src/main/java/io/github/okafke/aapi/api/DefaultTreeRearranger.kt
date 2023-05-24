package io.github.okafke.aapi.api

open class DefaultTreeRearranger: TreeRearranger {
    override fun <T> rearrange(tree: Array<T>, degree: Int, adapter: NodeAdapter<T>): Array<T> {
        val parents = ArrayDeque<T?>()
        parents.add(null)
        return rearrangeInternal(parents, tree, degree, degree, adapter)
    }

    protected open fun <T> rearrangeInternal(parents: ArrayDeque<T?>, treeIn: Array<T>, degree: Int, actualDegree: Int, adapter: NodeAdapter<T>): Array<T> {
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

                tree = list.toArray(adapter.getEmptyArray())
            }
        }

        if (tree.size > degree) {
            tree = adapter.aggregate(tree, degree)
        }

        for (node in tree) {
            parents.addFirst(node)
            adapter.setChildren(node, rearrangeInternal(parents, adapter.getChildren(node), degree, actualDegree, adapter))
            parents.removeFirst()
        }

        return tree
    }

}