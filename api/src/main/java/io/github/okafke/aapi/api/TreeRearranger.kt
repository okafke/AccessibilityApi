package io.github.okafke.aapi.api

interface TreeRearranger {
    fun <T> rearrange(tree: Array<T>, degree: Int, adapter: NodeAdapter<T>): Array<T>

}