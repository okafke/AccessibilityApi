package io.github.okafke.aapi.app.tree.permissions

import android.view.accessibility.AccessibilityNodeInfo
import io.github.okafke.aapi.app.R
import io.github.okafke.aapi.app.Constants
import io.github.okafke.aapi.aidl.Node
import io.github.okafke.aapi.app.overlay.TreeListener

class DenyNode(
    private val prevTree: Array<Node>,
    private val denyElements: List<AccessibilityNodeInfo>,
    private val treeListener: TreeListener
): Node("Deny", arrayOf(R.drawable.close), Constants.PACKAGES, "Deny", INVALID_ID, emptyArray()) {
    init {
        callbackInApi = Runnable {
            for (allowElement in denyElements) {
                allowElement.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }

            treeListener.onNewTree(prevTree)
        }
    }
}
