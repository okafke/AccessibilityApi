package io.github.okafke.aapi.client.json.adapter

import java.lang.reflect.Method
import java.util.function.Supplier

class StaticStringMethodAdapter(
    private val method: Method,
    private val value: String
) : Supplier<Any?> {
    override fun get(): Any? {
        println("Calling String Method ${method.name}")
        return method.invoke(null, value)
    }

}
