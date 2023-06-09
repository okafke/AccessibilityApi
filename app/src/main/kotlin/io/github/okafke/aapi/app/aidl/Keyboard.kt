package io.github.okafke.aapi.app.aidl

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import io.github.okafke.aapi.aidl.IKeyboard
import io.github.okafke.aapi.app.service.AApiOverlayService

class Keyboard: IKeyboard.Stub() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun type(key: String?) {
        val node = AApiOverlayService.instance?.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
        println("Focus node: $node")
        if (node != null) {
            val nodeText = if (node.isShowingHintText) "" else node.text ?: ""
            node.actionSetText("$nodeText$key")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun delete() {
        val node = AApiOverlayService.instance?.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
        println("Focus node: $node")
        if (node != null) {
            val nodeText = if (node.isShowingHintText) "" else node.text ?: ""
            node.actionSetText(nodeText.substring(0, (nodeText.length - 1).coerceAtLeast(0)))
        }
    }

    override fun enter() {
        // Android 11 offers performAction IME_ENTER or something?
        AApiOverlayService.instance?.softKeyboardController?.showMode = AccessibilityService.SHOW_MODE_HIDDEN
    }

    override fun open() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // This does not work! But it is also not needed I just realized, we type using the ui of aapi
            AApiOverlayService.instance?.softKeyboardController?.showMode = AccessibilityService.SHOW_MODE_IGNORE_HARD_KEYBOARD
        }
    }

    override fun hide() {
        AApiOverlayService.instance?.softKeyboardController?.showMode = AccessibilityService.SHOW_MODE_HIDDEN
    }

    private fun AccessibilityNodeInfo.actionSetText(txt: String) {
        val args = Bundle()
        args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, txt)
        this.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
    }

}
