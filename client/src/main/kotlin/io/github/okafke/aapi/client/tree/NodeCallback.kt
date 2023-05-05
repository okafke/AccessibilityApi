package io.github.okafke.aapi.client.tree

import android.content.Context

@FunctionalInterface
fun interface NodeCallback {
    fun onSelected(ctx: Context)

}