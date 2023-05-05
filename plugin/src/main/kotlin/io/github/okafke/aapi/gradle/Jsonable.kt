package io.github.okafke.aapi.gradle

import com.google.gson.JsonElement

interface Jsonable {
    fun toJson(): JsonElement

    fun toJson(degree: Int): JsonElement = toJson()

}