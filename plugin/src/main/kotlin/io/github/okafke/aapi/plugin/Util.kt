package io.github.okafke.aapi.plugin

import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode

fun Any.getAllAnnotations(): List<AnnotationNode> {
    return when (this) {
        is ClassNode -> {
            concat(visibleAnnotations, invisibleAnnotations, visibleTypeAnnotations, invisibleTypeAnnotations)
        }
        is MethodNode -> {
            concat(visibleAnnotations, invisibleAnnotations, visibleTypeAnnotations, invisibleTypeAnnotations)
        }
        is FieldNode -> {
            concat(visibleAnnotations, invisibleAnnotations, visibleTypeAnnotations, invisibleTypeAnnotations)
        }
        else -> emptyList()
    }
}

fun <T> MutableSet<T>.removeFirst(): T {
    val itr = this.iterator()
    val result = itr.next()
    this.remove(result)
    return result
}

private fun concat(vararg lists: List<AnnotationNode>?): List<AnnotationNode> {
    val result = ArrayList<AnnotationNode>()
    for (list in lists) {
        if (list != null) {
            result.addAll(list)
        }
    }

    return result
}
