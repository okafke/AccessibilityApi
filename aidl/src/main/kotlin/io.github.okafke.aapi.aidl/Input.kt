package io.github.okafke.aapi.aidl

import java.util.*
import java.util.stream.Collectors

data class Input(val index: Int,
                 val nodes: MutableSet<Node> = Collections.synchronizedSet(LinkedHashSet())) {
    fun toDisplayString(): String {
        if (nodes.isEmpty()) {
            return "$index"
        }

        return nodes.stream().map { node -> node.name }.distinct().collect(Collectors.joining(","))
    }

    fun getAsNode(): Node {
        return Node(toDisplayString(), getIds { node -> node.drawableIds },
            getIds { node -> node.drawablePackageNames }, "", 0, emptyArray())
    }

    private inline fun <reified T> getIds(function: java.util.function.Function<Node, Array<T>>): Array<T> {
        val result = ArrayList<T>(4)
        for (node in nodes) {
            for (t in function.apply(node)) {
                result.add(t)
                break
            }
        }

        return result.toTypedArray()
    }

}
