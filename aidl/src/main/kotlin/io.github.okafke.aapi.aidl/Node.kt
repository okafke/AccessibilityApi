package io.github.okafke.aapi.aidl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*
import java.util.stream.Collectors

@Parcelize
open class Node(
    val name: String,
    val drawableIds: Array<Int>,
    val drawablePackageNames: Array<String?>,
    val description: String,
    val id: Long,
    val children: Array<Node>
) : INode(), Parcelable {
    init {
        resetMChildren()
    }

    companion object {
        const val INVALID_ID = (-1).toLong()
        val DUMMY = root()

        @JvmStatic
        fun root(children: Array<Node> = emptyArray()): Node {
            val result = Node("root", emptyArray(), emptyArray(), "root node", INVALID_ID, children)
            result.resetMChildren()
            return result
        }

        // TODO: discuss: move into API, override getName
        //  child could get moved down further?
        fun merge(children: Array<Node>): Node {
            val map = HashMap<Int, String?>()
            children.forEach { child ->
                child.drawableIds.forEachIndexed { index, element ->
                    if (child.drawablePackageNames.size > index) {
                        map[element] = child.drawablePackageNames[index]
                    }
                }
            }

            map.remove(-1)
            val drawableIds = ArrayList<Int>()
            val drawablePackageNames = ArrayList<String?>()
            map.entries.forEach {
                drawableIds.add(it.key)
                drawablePackageNames.add(it.value)
            }

            return Node(Arrays.stream(children).map { it.name }.collect(Collectors.joining(",\n")),
                drawableIds.toTypedArray(), drawablePackageNames.toTypedArray(),
                Arrays.stream(children).map { it.name }.collect(Collectors.joining(",")),
                INVALID_ID, children)
        }
    }

    fun resetMChildren() {
        mChildren.clear()
        children.forEach { mChildren.add(it) }
        setAsParent()
    }

    fun setAsParent() {
        children.forEach { it.parent = this }
    }

    fun isCategory(): Boolean {
        return children.isNotEmpty()
    }

    fun isAction(): Boolean {
        return children.isEmpty()
    }

    fun hasDrawables(): Boolean {
        return drawableIds.isNotEmpty() && drawableIds[0] != -1
                && drawablePackageNames.isNotEmpty() && drawablePackageNames[0] != null
    }

    override fun toString(): String {
        return buildString {
            append("IParcelableNode(name='")
            append(name)
            append("', drawableId=")
            append(drawableIds.contentToString())
            append(", drawablePackageName=")
            append(drawablePackageNames.toString())
            append(", description='")
            append(description)
            append("', id=")
            append(id)
            append(", children=")
            append(children.contentToString())
            append(")")
        }
    }

}