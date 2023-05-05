package io.github.okafke.aapi.plugin

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.objectweb.asm.Type

object Constants {
    val actionDesc = Type.getDescriptor(io.github.okafke.aapi.annotations.Action::class.java)!!
    val categoryDesc = Type.getDescriptor(io.github.okafke.aapi.annotations.Category::class.java)!!
    val treeDesc = Type.getDescriptor(io.github.okafke.aapi.annotations.Tree::class.java)!!
    val contextDesc = Type.getDescriptor(io.github.okafke.aapi.annotations.ContextProvider::class.java)!!

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