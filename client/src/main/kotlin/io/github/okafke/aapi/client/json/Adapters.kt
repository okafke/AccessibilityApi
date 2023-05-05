package io.github.okafke.aapi.client.json

object Adapters {
    private val lookup = HashMap<String, Runnable>()

    fun register(name: String, adapter: Runnable) {
        io.github.okafke.aapi.client.json.Adapters.lookup[name] = adapter
    }

    fun runAction(name: String) {
        io.github.okafke.aapi.client.json.Adapters.lookup[name]?.run()
    }

}