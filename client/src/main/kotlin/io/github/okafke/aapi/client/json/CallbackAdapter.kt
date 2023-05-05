package io.github.okafke.aapi.client.json

import com.google.gson.annotations.SerializedName

data class CallbackAdapter(
    @SerializedName("uid") val uid: String,
    @SerializedName("class") val clazz: String,
    @SerializedName("method") val method: String? = null,
    @SerializedName("field") val field: String? = null
)
