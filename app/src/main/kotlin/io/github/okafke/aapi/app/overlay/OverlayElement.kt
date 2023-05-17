package io.github.okafke.aapi.app.overlay

import android.widget.Button
import io.github.okafke.aapi.aidl.Input
import io.github.okafke.aapi.aidl.Node

data class OverlayElement(val button: Button, val input: Input, var keyCode: Int = 0, var node: Node? = null)
