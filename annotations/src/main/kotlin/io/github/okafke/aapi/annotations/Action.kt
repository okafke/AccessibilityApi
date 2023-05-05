package io.github.okafke.aapi.annotations

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD/*, AnnotationTarget.CONSTRUCTOR*/)
annotation class Action(val name: String, val drawableId: String)
