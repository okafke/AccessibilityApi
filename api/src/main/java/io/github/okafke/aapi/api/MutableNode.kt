package io.github.okafke.aapi.api

import java.util.concurrent.CopyOnWriteArrayList

class MutableNode(var names: MutableSet<String> ,
                  val drawableIds: MutableList<String> = CopyOnWriteArrayList(),
                  val drawablePackageNames: MutableList<String> = CopyOnWriteArrayList(),
                  val children: Array<MutableNode>) {

}