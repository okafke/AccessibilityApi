package io.github.okafke.aapi.app.tree.permissions

import android.view.accessibility.AccessibilityNodeInfo
import io.github.okafke.aapi.aidl.Node
import io.github.okafke.aapi.app.Constants.Companion.PACKAGES
import io.github.okafke.aapi.app.R
import io.github.okafke.aapi.app.overlay.TreeListener

class AllowNode(
    private val prevTree: Array<Node>,
    private val allowElements: List<AccessibilityNodeInfo>,
    private val treeListener: TreeListener
): Node("Allow", arrayOf(R.drawable.allow), PACKAGES, "Allow", INVALID_ID, emptyArray()) {
    init {
        callbackInApi = Runnable {
            for (allowElement in allowElements) {
                allowElement.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }

            treeListener.onNewTree(prevTree)
        }
    }
}
