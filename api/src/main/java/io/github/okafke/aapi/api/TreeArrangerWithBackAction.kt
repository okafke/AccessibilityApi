package io.github.okafke.aapi.api

class TreeArrangerWithBackAction(val alwaysAddAtEnd: Boolean = false): DefaultTreeRearranger() {
    override fun <T> rearrangeInternal(
        parents: ArrayDeque<T?>,
        treeIn: Array<T>,
        degree: Int,
        actualDegree: Int,
        adapter: NodeAdapter<T>
    ): Array<T> {
        if (parents.size > 1) { // TODO: degree 2
            if (degree == 2 || alwaysAddAtEnd) {
                val rearranged = super.rearrangeInternal(parents, treeIn, degree, actualDegree, adapter)
                var allActions = true
                for (action in rearranged) {
                    if (adapter.getChildren(action).isNotEmpty()) {
                        allActions = false
                        break
                    }
                }

                if (allActions) {
                    return if (rearranged.size < degree) {
                        val tree = ArrayList(rearranged.asList())
                        tree.add(adapter.getBackAction())
                        tree.toArray(adapter.getEmptyArray())
                    } else {
                        val nodeToMerge = adapter.getNodeToMergeWithBackNode(rearranged)
                        val tree = ArrayList(rearranged.asList())
                        tree.remove(nodeToMerge)
                        tree.add(adapter.mergeWithBackNode(nodeToMerge))
                        tree.toArray(adapter.getEmptyArray())
                    }
                }

                return rearranged
            } else {
                val tree = ArrayList(super.rearrangeInternal(parents, treeIn, actualDegree - 1, actualDegree, adapter).asList())
                tree.add(adapter.getBackAction())
                return tree.toArray(adapter.getEmptyArray())
            }
        }

        return super.rearrangeInternal(parents, treeIn, degree, actualDegree, adapter)
    }

}