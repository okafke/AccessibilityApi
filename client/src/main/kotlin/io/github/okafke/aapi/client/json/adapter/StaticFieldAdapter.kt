package io.github.okafke.aapi.client.json.adapter

import java.lang.reflect.Field
import java.util.function.Supplier

class StaticFieldAdapter(private val field: Field): Supplier<Any?> {
    override fun get(): Any? {
        return field.get(null)
    }
}