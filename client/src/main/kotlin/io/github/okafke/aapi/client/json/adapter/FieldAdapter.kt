package io.github.okafke.aapi.client.json.adapter

import io.github.okafke.aapi.client.json.instances.Instances
import java.lang.reflect.Field
import java.util.function.Supplier

class FieldAdapter(private val instanceId: String, private val field: Field): Supplier<Any?> {
    override fun get(): Any? {
        val instance = io.github.okafke.aapi.client.json.instances.Instances.getInstance(instanceId)
        return field.get(instance)
    }
}