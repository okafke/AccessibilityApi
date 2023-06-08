package io.github.okafke.keyboard

import android.accessibilityservice.AccessibilityService
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import io.github.okafke.aapi.aidl.INavigationTreeService
import io.github.okafke.aapi.aidl.Node
import io.github.okafke.aapi.client.ClientService
import java.util.concurrent.ConcurrentHashMap

class KeyboardService: AccessibilityService() {
    private val keyCodes = ConcurrentHashMap<Int, Int>()
    private lateinit var service: ClientService

    override fun onServiceConnected() {
        service = ClientService(applicationContext)
        service.whenAvailable { api ->
            run {
                register(api, 0, getNode("w"), KeyEvent.KEYCODE_W)
                register(api, 1, getNode("a"), KeyEvent.KEYCODE_A)
                register(api, 2, getNode("s"), KeyEvent.KEYCODE_S)
                register(api, 3, getNode("d"), KeyEvent.KEYCODE_D)
                register(api, 4, getNode("q"), KeyEvent.KEYCODE_Q)
                register(api, 5, getNode("y"), KeyEvent.KEYCODE_Y)
                register(api, 6, getNode("c"), KeyEvent.KEYCODE_C)
                register(api, 7, getNode("e"), KeyEvent.KEYCODE_E)
            }
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        println("KeyEvent $event")
        if (keyCodes.contains(event.keyCode)) {
            if (service.isConnected()) {
                service.whenAvailable { api ->
                    run {
                        api.onInput(keyCodes[event.keyCode]!!)
                    }
                }
            }

            return true
        }

        return super.onKeyEvent(event)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // NOP
    }

    override fun onInterrupt() {
        // NOP
    }

    private fun register(api: INavigationTreeService, id: Int, node: Node, keyCode: Int) {
        api.registerInput(id, node)
        keyCodes[keyCode] = id
    }

    private fun getNode(name: String): Node {
        return Node(
            name,
            arrayOf(io.github.okafke.aapi.client.R.drawable.keyboard),
            arrayOf(applicationContext.packageName),
            "",
            "keyboard".hashCode().toLong(),
            emptyArray()
        )
    }

}