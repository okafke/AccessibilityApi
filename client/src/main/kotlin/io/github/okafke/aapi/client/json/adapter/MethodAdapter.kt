package io.github.okafke.aapi.client.json.adapter

import java.lang.reflect.Method
import java.util.function.Supplier

class MethodAdapter(private val instanceId: String, private val method: Method): Supplier<Any?> {
    override fun get(): Any? {
        println("Looking for instance $instanceId for method ${method.declaringClass.name}.${method.name}")
        val instance = io.github.okafke.aapi.client.json.instances.Instances.getInstance(instanceId)
        println("Calling instance method ${method.name} $instanceId on $instance")
        return method.invoke(instance)
    }
}