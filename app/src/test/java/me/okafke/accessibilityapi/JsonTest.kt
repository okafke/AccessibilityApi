package me.okafke.accessibilityapi

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.okafke.aapi.aidl.Node
import org.junit.Test
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

class JsonTest {
    @Test
    fun testJson() {
        val type: Type = object : TypeToken<Map<Int?, Node>>() {}.type
        var map: MutableMap<Int, Node> = ConcurrentHashMap<Int, Node>()
        map.put(1, Node("test", arrayOf(1), arrayOf("testpackage"), "descriptiontest", 1, emptyArray()))
        val json = Gson().toJson(map, type)
        println(json)

        map = Gson().fromJson(json, type)
        println(map)
        println(map[1])
    }

}