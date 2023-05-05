package io.github.okafke.aapi.plugin

import com.google.gson.JsonArray
import com.google.gson.JsonElement

interface JsonableWithFound: Jsonable {
    var found: MutableMap<String, Node>

    override fun toJson(): JsonElement {
        return foundToJsonArray()
    }

    fun foundToJsonArray(): JsonArray {
        val array = JsonArray(found.size)
        for (child in found.values) {
            array.add(child.toJson())
        }

        return array
    }

}