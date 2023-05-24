package io.github.okafke.aapi.plugin

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

fun Action(name: String, drawableId: String, adapter: Adapter): Action {
    return Action(name, arrayOf(drawableId), adapter)
}

data class Action(
    @SerializedName("name") override var name: String,
    @SerializedName("drawableId") override var drawableId: Array<String>,
    @SerializedName("adapter") val adapter: Adapter,
    //@SerializedName("priority") override var priority: Int
): java.io.Serializable, Node {
    override fun toJson(): JsonElement {
        return Constants.GSON.toJsonTree(this)
    }

}
