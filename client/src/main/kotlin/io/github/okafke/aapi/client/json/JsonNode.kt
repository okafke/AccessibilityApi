package io.github.okafke.aapi.client.json

import com.google.gson.annotations.SerializedName
import io.github.okafke.aapi.client.ClientService

data class JsonNode(
    @SerializedName("name") val name: String,
    @SerializedName("drawableId") val drawableId: Array<String>? = null,
    @SerializedName("drawablePackageName") var drawablePackageName: String? = ClientService.PACKAGE_NAME,
    //@SerializedName("description") val description: String = "Your description here...",
    @SerializedName("adapter") val adapter: io.github.okafke.aapi.client.json.CallbackAdapter? = null,
    @SerializedName("children") val children: Array<JsonNode>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JsonNode

        if (name != other.name) return false
        if (!drawableId.contentEquals(other.drawableId)) return false
        if (drawablePackageName != other.drawablePackageName) return false
        //if (description != other.description) return false
        if (adapter != other.adapter) return false
        if (!children.contentEquals(other.children)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + drawableId.contentHashCode()
        result = 31 * result + (drawablePackageName?.hashCode() ?: 0)
        //result = 31 * result + description.hashCode()
        result = 31 * result + (adapter?.hashCode() ?: 0)
        result = 31 * result + children.contentHashCode()
        return result
    }
}
