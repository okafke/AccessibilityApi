package io.github.okafke.aapi.app.overlay

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import androidx.core.content.res.ResourcesCompat
import io.github.okafke.aapi.aidl.Node

class OverlayUpdateService(private val context: Context,
                           private val threadHandler: Handler,
                           private val overlayHolder: Overlay.Holder) {
    fun update(node: Node) {
        threadHandler.post {
            val overlay = overlayHolder.overlay ?: return@post
            overlay.overlayElements.forEach { element ->
                val button = element.button
                val child = node.input2Child[element.input]
                if (child != null) {
                    button.text = child.name
                    if (child.hasDrawables()) {
                        val d0: Drawable? = loadDrawable(child, 0)
                        val d1: Drawable? = loadDrawable(child, 1)
                        val d2: Drawable? = loadDrawable(child, 2)
                        val d3: Drawable? = loadDrawable(child, 3)

                        button.setCompoundDrawablesWithIntrinsicBounds(d2, d0, d3, d1)
                    } else {
                        button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    }
                } else {
                    button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    button.text = ""
                }
            }
        }
    }

    private fun loadDrawable(node: Node, index: Int): Drawable? {
        if (index >= node.drawableIds.size || index >= node.drawablePackageNames.size) {
            return null
        }

        return context.packageManager
            ?.getResourcesForApplication(node.drawablePackageNames[index]!!)
            ?.let {
                ResourcesCompat.getDrawable(it, node.drawableIds[index], null)
            }
    }

}