package io.github.okafke.aapi.client.json

import io.github.okafke.aapi.client.json.adapter.*
import java.lang.reflect.Modifier
import java.util.function.Supplier

class CallbackFactory {
    fun asRunnable(data: io.github.okafke.aapi.client.json.CallbackAdapter): Runnable {
        val adapter = create(data)
        println("Runnable Adapter $adapter")
        return Runnable { adapter.get() }
    }

    fun create(data: io.github.okafke.aapi.client.json.CallbackAdapter): Supplier<Any?> {
        println("Creating CallbackAdapter $data")
        val clazz = Class.forName(data.clazz)
        if (data.method != null) {
            val method = try {
                clazz.getDeclaredMethod(data.method)
            } catch (e: NoSuchMethodException) {
                clazz.getDeclaredMethod(data.method, String::class.java)
            }

            method.isAccessible = true
            val argTypes = method.parameterTypes
            if (Modifier.isStatic(method.modifiers)) {
                if (argTypes.size == 1 && argTypes[0] == String::class.java) {
                    return StaticStringMethodAdapter(method, data.uid)
                }

                return StaticMethodAdapter(method)
            }

            return MethodAdapter(data.uid, method)
        }

        if (data.field != null) {
            println("Warning: Fields are only supported for ContextProviders yet")
            val field = clazz.getDeclaredField(data.field)
            field.isAccessible = true
            if (Modifier.isStatic(field.modifiers)) {
                return StaticFieldAdapter(field)
            }

            return FieldAdapter(data.uid, field)
        }

        throw IllegalArgumentException("Both Field and Method in Adapter $data where null!")
    }

}