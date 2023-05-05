package io.github.okafke.aapi.aidl

open class Input(val index: Int, val name: String) {
    override fun toString(): String {
        return "Input($index, $name)"
    }
}
