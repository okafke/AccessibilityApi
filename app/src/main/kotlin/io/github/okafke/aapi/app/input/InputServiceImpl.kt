package io.github.okafke.aapi.app.input

import android.content.Context
import io.github.okafke.aapi.aidl.Input
import io.github.okafke.aapi.app.aidl.NavigationTreeService
import io.github.okafke.aapi.app.overlay.OverlayUpdateService
import io.github.okafke.aapi.app.tree.TreeHolder
import io.github.okafke.aapi.app.util.AApiUtil
import java.util.concurrent.CopyOnWriteArrayList

class InputServiceImpl(ctx: Context,
                       private val tree: TreeHolder,
                       private val overlayRenderService: OverlayUpdateService) : InputService {
    override val inputs: MutableList<Input> = CopyOnWriteArrayList()

    init {
        val numOfInputs = AApiUtil.getInputs(ctx, false)
        for (i in 0 until numOfInputs) {
            inputs.add(NavigationTreeService.getInput(ctx, i))
        }
    }

    override fun onInput(input: Input?) {
        if (input == null) {
            println("Input was null!!!")
            return
        }

        val newNode = tree.currentNode.input2Child[input]
        if (newNode != null) {
            if (newNode.isAction()) {
                tree.currentNode = tree.currentRootNode
                overlayRenderService.update(tree.currentNode)
                val callbackInApi = newNode.callbackInApi
                if (callbackInApi != null) {
                    callbackInApi.run()
                } else {
                    NavigationTreeService.notifyListeners(newNode.id)
                }
            } else {
                tree.currentNode = newNode
                overlayRenderService.update(tree.currentNode)
            }
        } else {
            println("No child node for $input found!")
        }
    }

}
