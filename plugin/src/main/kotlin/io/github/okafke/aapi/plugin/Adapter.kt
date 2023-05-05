package io.github.okafke.aapi.plugin

import com.google.gson.annotations.SerializedName

// TODO: rn this is buildSrc so we cannot include project(:annotations)
//  but this should all be one api and a seperate gradle plugin to include
data class Adapter(
    @SerializedName("uid") var uid: String,
    @SerializedName("class") val clazz: String,
    @SerializedName("method") val method: String? = null,
    @SerializedName("field") val field: String? = null
): java.io.Serializable
