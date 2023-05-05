package io.github.okafke.aapi.gradle

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.annotations.SerializedName
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashMap
import kotlin.math.min
import kotlin.streams.toList

fun Category(name: String, drawableId: String, children: MutableSet<String>): Category {
    return Category(name, arrayOf(drawableId), children)
}

data class Category(
    @SerializedName("name") override var name: String,
    @SerializedName("drawableId") override var drawableId: Array<String>,
    @SerializedName("children") override val children: MutableSet<String>
): java.io.Serializable, Node, SortsFound {
    @Suppress("SENSELESS_COMPARISON")
    @Exclude override var found: MutableMap<String, Node> = LinkedHashMap()
        // Gson seems to instantiate this in a way that not even by lazy works and this is null
        get() {
            if (field == null) {
                field = LinkedHashMap()
                found = field
                return field
            }

            return field
        }

    /**
     * toJson() for the actual tree. For the gradle cache we just use GSON since the only the names
     * of the children are needed.
     */
    override fun toJson(): JsonElement {
        sortFound()
        val children = foundToJsonArray()
        return getObj(children)
    }

    override fun toJson(degree: Int): JsonElement {
        sortFound()
        val children = JsonArray(found.values.size.coerceAtMost(degree))
        val childSet = ArrayList(found.values)
        // TODO: This is kinda the DefaultTreeMapper, If this was not BuildSrc we could depend on it
        while (childSet.size > degree) {
            var groupSize = min(childSet.size - degree + 1, degree)
            val subFound = HashMap<String, Node>()
            while (groupSize-- > 0) {
                val first = childSet.removeFirst()
                subFound[first.name] = first
            }

            val newParent = merge(subFound)
            childSet.add(newParent)
        }

        for (child in childSet) {
            children.add(child.toJson(degree))
        }

        return getObj(children)
    }

    private fun getObj(children: JsonArray): JsonObject {
        val obj = JsonObject()
        obj.add("name", JsonPrimitive(name))
        val drawableIds = JsonArray(drawableId.size)
        drawableId.forEach { drawableIds.add(it) }
        obj.add("drawableId", drawableIds)
        obj.add("children", children)
        return obj
    }

    private fun merge(children: MutableMap<String, Node>): Category {
        val name: String = children.keys.stream().collect(Collectors.joining(","))
        val drawableIds: Array<String> = children.values.stream().map { c -> c.drawableId }.flatMap { Arrays.stream(it) }.limit(4).toList().toTypedArray()
        val childSet = HashSet(children.keys)
        val result = Category(name, drawableIds, childSet)
        result.found = children
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Category

        if (name != other.name) return false
        if (!drawableId.contentEquals(other.drawableId)) return false
        if (children != other.children) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + drawableId.contentHashCode()
        result = 31 * result + children.hashCode()
        return result
    }

}
