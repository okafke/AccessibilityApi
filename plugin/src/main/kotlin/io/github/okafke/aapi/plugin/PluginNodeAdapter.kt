package io.github.okafke.aapi.plugin

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.github.okafke.aapi.api.NodeAdapter
import io.github.okafke.aapi.client.json.instances.BackAction
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class PluginNodeAdapter(val context: InstrumentationContext): NodeAdapter<Node> {
    private val childMap = HashMap<String, MutableList<Node>>()

    fun clear() {
        childMap.clear()
    }

    fun toJson(nodes: Array<Node>, result: JsonArray): JsonArray {
        for (node in nodes) {
            val json = JsonObject()
            json.add("name", JsonPrimitive(node.name))
            val drawableIds = JsonArray()
            node.drawableId.forEach { drawableIds.add(JsonPrimitive(it)) }
            json.add("drawableId", drawableIds)
            if (node is Category) {
                json.add("children", toJson(getChildren(node), JsonArray()))
            } else if (node is Action) {
                json.add("adapter", Constants.GSON.toJsonTree(node.adapter))
            }

            result.add(json)
        }

        return result
    }

    override fun getType(): Class<Node> {
        return Node::class.java
    }

    override fun getBackAction(): Node {
        return Action("Back", "aapi_undo", Adapter("backAction", BackAction::class.java.name, "back"))
    }

    override fun mergeWithBackNode(node: Node): Node {
        val map = LinkedHashMap<String, Node>()
        map[node.name] = node
        val back = getBackAction()
        map[back.name] = back
        return Category.merge(map)
    }

    override fun aggregate(nodes: Array<Node>, degree: Int): Array<Node> {
        val result = ArrayList(nodes.asList())
        while (result.size > degree) {
            val aggregated = HashMap<String, Node>()

            var currentAggregate: Aggregate? = null
            val itr = result.iterator()
            while (itr.hasNext() && aggregated.size < degree) {
                val node = itr.next()
                for (aggregate in context.aggregates) {
                    if ((currentAggregate == null || currentAggregate == aggregate) && aggregate.children.contains(node.name)) {
                        currentAggregate = aggregate
                        itr.remove()
                        aggregated[node.name] = node
                        break
                    }
                }
            }

            if (currentAggregate == null) {
                for (i in 0 until degree) {
                    val removed = result.removeLast()
                    aggregated[removed.name] = removed
                }

                val aggregateNode = Category.merge(aggregated)
                result.add(0, aggregateNode)
            } else {
                val aggregateNode = currentAggregate.toCategory(aggregated)
                result.add(0, aggregateNode)
            }
        }

        @Suppress("UNCHECKED_CAST")
        return result.toArray(java.lang.reflect.Array.newInstance(getType(), 0) as Array<Node>)
    }

    override fun setChildren(node: Node, children: Array<Node>) {
        if (node is Category) {
            childMap[node.name] = ArrayList(children.asList())
        }
    }

    override fun addChild(node: Node, child: Node) {
        if (node is Category) {
            initializeChildMap(node)
            childMap[node.name]?.add(child)
        }
    }

    override fun removeChild(node: Node, child: Node) {
        if (node is Category) {
            initializeChildMap(node)
            childMap[node.name]?.remove(node)
        }
    }

    override fun getChildren(node: Node): Array<Node> {
        if (node.name == "yz") {
            return emptyArray()
        }

        if (node is Category) {
            initializeChildMap(node)
            return childMap[node.name]?.toTypedArray() ?: emptyArray()
        }

        return emptyArray()
    }

    private fun initializeChildMap(node: Node) {
        if (childMap[node.name] == null && node is Category) {
            childMap[node.name] = ArrayList(node.found.values.toTypedArray().toList())
        }
    }

}