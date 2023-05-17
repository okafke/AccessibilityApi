package io.github.okafke.aapi

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.okafke.aapi.aidl.Node
import io.github.okafke.json.Exclude
import org.junit.Test
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

class JsonTest {
    @Test
    fun testJson() {
        val type: Type = object : TypeToken<Map<Int?, Node>>() {}.type
        var map: MutableMap<Int, Node> = ConcurrentHashMap<Int, Node>()
        map[1] = Node("test", arrayOf(1), arrayOf("testpackage"), "descriptiontest", 1, emptyArray())
        val json = Gson().toJson(map, type)
        println(json)

        map = Gson().fromJson(json, type)
        println(map)
        println(map[1])
    }

    @Test
    fun testExclude() {
        var node = Node("test", arrayOf(1), arrayOf("testpackage"), "descriptiontest", 1, emptyArray())
        val json = Exclude.GSON.toJson(node)
        KotlinTest("test").toString()
        println(json)

        node = Exclude.GSON.fromJson(json, Node::class.java)
    }



}