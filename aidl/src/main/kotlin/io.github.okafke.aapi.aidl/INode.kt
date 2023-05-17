package io.github.okafke.aapi.aidl

import io.github.okafke.json.Exclude
import java.util.*

/**
 * This part of a [Node] will not be serialized and is only for the AccessibilityApi.
 */
open class INode(@field:Exclude var parent: Node? = null,
                 @field:Exclude var callbackInApi: Runnable? = null) {
    @Suppress("SENSELESS_COMPARISON")
    @field:Exclude var input2Child: MutableMap<Input, Node> = LinkedHashMap()
        // Gson seems to instantiate this in a way that not even by lazy works and this is null
        get() {
            if (field == null) {
                field = LinkedHashMap()
                input2Child = field
                return field
            }

            return field
        }

    @Suppress("SENSELESS_COMPARISON")
    @field:Exclude var mChildren : MutableList<Node> = LinkedList()
        // Gson seems to instantiate this in a way that not even by lazy works and this is null
        get() {
            if (field == null) {
                field = LinkedList()
                mChildren = field
                return field
            }

            return field
        }

}
