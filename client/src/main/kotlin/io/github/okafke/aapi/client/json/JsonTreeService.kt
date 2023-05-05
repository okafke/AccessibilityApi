package io.github.okafke.aapi.client.json

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.github.okafke.aapi.client.ClientService
import io.github.okafke.aapi.client.tree.AbstractNode
import io.github.okafke.aapi.client.tree.ActionNode
import io.github.okafke.aapi.client.tree.CategoryNode
import io.github.okafke.aapi.client.tree.NodeInfo
import java.io.InputStreamReader

class JsonTreeService(private val clientService: ClientService) {
    private val callbackFactory = io.github.okafke.aapi.client.json.CallbackFactory()
    var currentTree = "none"

    companion object {
        val GSON = Gson()
    }

    fun load(resource: String) {
        clientService.setNavigationTree { service ->
            val treeName = "${resource}_${service.inputs}.json"
            println("Loading Tree $treeName")
            val isr = InputStreamReader(
                JsonTreeService::class.java.classLoader?.getResourceAsStream(treeName)
            )
            isr.use {
                val array = JsonParser.parseReader(isr).asJsonArray
                val tree = ArrayList<AbstractNode>(array.size())
                array.forEach { tree.add(toNode(GSON.fromJson(it, JsonNode::class.java))) }
                clientService.setNavigationTree(tree)
            }

            currentTree = resource
        }
    }

    private fun toNode(node: JsonNode): AbstractNode {
        println("Checking $node")
        val children = ArrayList<AbstractNode>(node.children?.size?:0)
        println("NodeDrawablePackageName ${node.drawablePackageName}")
        if (node.drawablePackageName == null) {
            println("DrawablePackage name is null, using ${clientService.ctx.packageName}")
            node.drawablePackageName = clientService.ctx.packageName
        }

        return if (node.adapter == null || node.children?.isNotEmpty() == true) {
            node.children?.forEach { children.add(toNode(it)) }
            CategoryNode(NodeInfo(node.name, getIds(node.drawableId), node.drawablePackageName, "", children.toTypedArray()))
        } else {
            val adapter = callbackFactory.asRunnable(node.adapter)
            ActionNode(NodeInfo(node.name, getIds(node.drawableId), node.drawablePackageName, "", emptyArray()), adapter)
        }
    }

    @SuppressLint("DiscouragedApi")
    // well I would like to use ResourceIds directly but with the new AGP they become non-final,
    // so I cannot use them in annotations anymore
    private fun getIds(drawableIds: Array<String>?): Array<Int> {
        if (drawableIds == null) {
            return arrayOf(-1)
        }

        val list = ArrayList<Int>(drawableIds.size)
        for (drawableId in drawableIds) {
            list.add(clientService.ctx.resources.getIdentifier(
                drawableId, "drawable", clientService.ctx.packageName))
        }

        return list.toTypedArray()
    }

}