package io.github.okafke.aapi.gradle

interface Node: Jsonable {
    var name: String
    var drawableId: Array<String>

}