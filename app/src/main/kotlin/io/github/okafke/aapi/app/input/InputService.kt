package io.github.okafke.aapi.app.input

import io.github.okafke.aapi.aidl.Input

interface InputService {
    val inputs: MutableList<Input>

    fun onInput(input: Input?)

}
