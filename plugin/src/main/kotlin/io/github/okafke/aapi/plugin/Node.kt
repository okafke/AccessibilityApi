package io.github.okafke.aapi.plugin

interface Node: Jsonable {
    var name: String
    var drawableId: Array<String>
    //var priority: Int

}