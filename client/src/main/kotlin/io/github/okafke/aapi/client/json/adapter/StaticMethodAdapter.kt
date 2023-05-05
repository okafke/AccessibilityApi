package io.github.okafke.aapi.client.json.adapter

import java.lang.reflect.Method
import java.util.function.Supplier

class StaticMethodAdapter(private val method: Method): Supplier<Any?> {
    override fun get(): Any? {
        println("Calling Static Method ${method.name}")
        return method.invoke(null)
    }
}