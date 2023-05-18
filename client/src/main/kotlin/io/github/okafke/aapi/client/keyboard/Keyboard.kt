@file:Suppress("unused")

package io.github.okafke.aapi.client.keyboard

import io.github.okafke.aapi.annotations.Action
import io.github.okafke.aapi.client.json.ServiceHolder

object Keyboard {
    private var current = "none"

    @JvmStatic
    fun typeKey(key: String) {
        ServiceHolder.clientService.whenAvailable { service ->
            service.keyboard.type(key)
        }
    }

    @JvmStatic
    fun typeSpace() {
        typeKey(" ")
    }

    @JvmStatic
    fun delete() {
        ServiceHolder.clientService.whenAvailable { service ->
            service.keyboard.delete()
        }
    }

    @JvmStatic
    fun enter() {
        ServiceHolder.clientService.whenAvailable { service ->
            service.keyboard.enter()
        }
    }

    @JvmStatic
    @Action("Type", "keyboard")
    fun loadLowerCaseTree() {
        saveCurrentTree()
        ServiceHolder.setTree("lowercase")

        ServiceHolder.clientService.whenAvailable { service ->
            service.keyboard.hide()
        }
    }

    @JvmStatic
    fun loadUpperCaseTree() {
        saveCurrentTree()
        ServiceHolder.setTree("uppercase")

        ServiceHolder.clientService.whenAvailable { service ->
            service.keyboard.hide()
        }
    }

    @JvmStatic
    fun returnToPreviousTree() {
        val current = this.current
        if (current != "none" && current != "lowercase" && current != "uppercase") {
            ServiceHolder.setTree(current)
        }

        ServiceHolder.clientService.whenAvailable { service ->
            service.keyboard.hide()
        }
    }

    @JvmStatic
    private fun saveCurrentTree() {
        val current = ServiceHolder.jsonService.currentTree
        if (current != "none" && current != "lowercase" && current != "uppercase") {
            this.current = current
        }
    }

}