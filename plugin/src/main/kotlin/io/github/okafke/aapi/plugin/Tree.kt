package io.github.okafke.aapi.plugin

import com.google.gson.JsonElement

data class Tree(
    val name: String,
    override val children: HashSet<String>,
    override var found: MutableMap<String, Node> = LinkedHashMap()
): java.io.Serializable, SortsFound {
    override fun toJson(): JsonElement {
        sortFound()
        return super.toJson()
    }

    override fun toJson(degree: Int): JsonElement {
        sortFound()
        val category = Category("dummy", "dummy", children)
        category.found = this.found
        return category.toJson(degree).asJsonObject.get("children")
    }

}
