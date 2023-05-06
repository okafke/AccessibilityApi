package io.github.okafke.aapi.client.json

object Adapters {
    private val lookup = HashMap<String, Runnable>()

    fun register(name: String, adapter: Runnable) {
        lookup[name] = adapter
    }

    fun runAction(name: String) {
        Adapters.lookup[name]?.run()
    }

}