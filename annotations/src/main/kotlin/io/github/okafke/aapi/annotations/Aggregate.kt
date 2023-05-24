package io.github.okafke.aapi.annotations

// TODO: priority for aggregates
@Repeatable
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Aggregate(val name: String, val drawableId: String, val children: Array<String>)
