package io.github.okafke.aapi.api

interface NodeAdapter<T> {
    fun getType(): Class<T>

    fun getBackAction(): T

    fun getChildren(node: T): Array<T>

    fun removeChild(node: T, child: T)

    fun addChild(node: T, child: T)

    fun setChildren(node: T, children: Array<T>)

    fun aggregate(nodes: Array<T>, degree: Int): Array<T>

    // TODO: implement taking priority into account
    fun getNodesToMoveUpwards(nodes: Array<T>, amount: Int): Array<T> {
        val result = ArrayList<T>(amount)
        for (i in nodes.size - 1 downTo 0) {
            result.add(nodes[i])
            if (result.size == amount) {
                break
            }
        }

        return result.toArray(getEmptyArray())
    }

    @Suppress("UNCHECKED_CAST")
    fun getEmptyArray(): Array<T> {
        return java.lang.reflect.Array.newInstance(getType(), 0) as Array<T>
    }

}