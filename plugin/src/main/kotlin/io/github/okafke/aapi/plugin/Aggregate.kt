package io.github.okafke.aapi.plugin

data class Aggregate(val name: String, val drawableId: String, val children: Array<String>) {
    fun toCategory(found: MutableMap<String, Node>): Category {
        val result = Category(name, drawableId, LinkedHashSet(found.keys))
        result.found = found
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Aggregate) return false

        if (name != other.name) return false
        if (drawableId != other.drawableId) return false
        if (!children.contentEquals(other.children)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + drawableId.hashCode()
        result = 31 * result + children.contentHashCode()
        return result
    }
}
