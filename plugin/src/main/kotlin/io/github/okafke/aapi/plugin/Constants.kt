package io.github.okafke.aapi.plugin

import com.google.gson.Gson
import io.github.okafke.json.Exclude
import org.objectweb.asm.Type

object Constants {
    val actionDesc = Type.getDescriptor(io.github.okafke.aapi.annotations.Action::class.java)!!
    val categoryDesc = Type.getDescriptor(io.github.okafke.aapi.annotations.Category::class.java)!!
    val treeDesc = Type.getDescriptor(io.github.okafke.aapi.annotations.Tree::class.java)!!
    val contextDesc = Type.getDescriptor(io.github.okafke.aapi.annotations.ContextProvider::class.java)!!

    val GSON: Gson = Exclude.GSON

}