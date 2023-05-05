package io.github.okafke.aapi.app.overlay

import android.widget.Button
import io.github.okafke.aapi.aidl.Input

interface ButtonListener {
    fun onInput(button: Button, index: Int, input: Input)

}