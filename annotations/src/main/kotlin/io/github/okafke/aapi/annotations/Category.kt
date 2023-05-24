package io.github.okafke.aapi.annotations

@Repeatable
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Category(val name: String, val drawableId: String, val children: Array<String>, val priority: Int = 0)
