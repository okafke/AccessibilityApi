package io.github.okafke.aapi.api

class TreeArrangerWithBackAction(val alwaysAddAtEnd: Boolean = false): DefaultTreeRearranger() {
    override fun <T> rearrangeInternal(
        parents: ArrayDeque<T?>,
        treeIn: Array<T>,
        degree: Int,
        actualDegree: Int,
        adapter: NodeAdapter<T>
    ): Array<T> {
        if (parents.size > 1 && degree != 2) { // TODO: degree 2
            val tree = ArrayList(super.rearrangeInternal(parents, treeIn, actualDegree - 1, actualDegree, adapter).asList())
            tree.add(adapter.getBackAction())
            return tree.toArray(adapter.getEmptyArray())
        }

        return super.rearrangeInternal(parents, treeIn, degree, actualDegree, adapter)
    }

}