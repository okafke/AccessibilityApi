package io.github.okafke.json

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class Exclude() {
    companion object {
        val GSON: Gson = GsonBuilder()
            .setPrettyPrinting()
            .setExclusionStrategies(object: ExclusionStrategy {
                override fun shouldSkipField(f: FieldAttributes?): Boolean {
                    // println("Considering field $f result: ${f?.getAnnotation(Exclude::class.java) != null}, annotations: ${f?.annotations}")
                    return f?.getAnnotation(Exclude::class.java) != null
                }

                override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                    return false
                }
            })
            .create()
    }
}