package io.github.okafke.aapi.annotations

@Repeatable
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Tree(val name: String, val children: Array<String>)
