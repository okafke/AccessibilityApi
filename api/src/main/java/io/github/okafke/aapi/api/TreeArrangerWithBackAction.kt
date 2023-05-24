package io.github.okafke.aapi.api

class TreeArrangerWithBackAction: DefaultTreeRearranger() {
    override fun <T> rearrangeInternal(
        parents: ArrayDeque<T?>,
        treeIn: Array<T>,
        degree: Int,
        adapter: NodeAdapter<T>
    ): Array<T> {
        if (parents.size > 1) {
            val tree = ArrayList(treeIn.asList())
            tree.add(adapter.getBackAction())
            return super.rearrangeInternal(parents, tree.toArray(adapter.getEmptyArray()), degree, adapter)
        }

        return super.rearrangeInternal(parents, treeIn, degree, adapter)
    }

}