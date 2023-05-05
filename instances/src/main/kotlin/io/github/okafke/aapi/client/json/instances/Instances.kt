package io.github.okafke.aapi.client.json.instances

object Instances {
    private val instances = HashMap<String, Any>()

    fun getInstance(uid: String): Any {
        return instances[uid]!!
    }

    @Suppress("unused")
    fun addInstance(uid: String, instance: Any) {
        println("Adding instance $uid : $instance")
        instances[uid] = instance
    }

}