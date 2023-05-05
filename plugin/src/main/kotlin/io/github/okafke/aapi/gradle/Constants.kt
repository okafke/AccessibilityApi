package io.github.okafke.aapi.gradle

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object Constants {
    const val actionDesc = "Lme/okafke/annotations/Action;"
    const val categoryDesc = "Lme/okafke/annotations/Category;"
    const val treeDesc = "Lme/okafke/annotations/Tree;"
    const val contextDesc = "Lme/okafke/annotations/ContextProvider;"

    val GSON: Gson = GsonBuilder()
        .setPrettyPrinting()
        .setExclusionStrategies(object: ExclusionStrategy {
            override fun shouldSkipField(f: FieldAttributes?): Boolean {
                return f?.getAnnotation(Exclude::class.java) != null
            }

            override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                return false
            }
        })
        .create()

}