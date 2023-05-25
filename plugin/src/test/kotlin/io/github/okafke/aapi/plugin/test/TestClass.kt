package io.github.okafke.aapi.plugin.test

import io.github.okafke.aapi.annotations.Action
import io.github.okafke.aapi.annotations.Aggregate
import io.github.okafke.aapi.annotations.Category
import io.github.okafke.aapi.annotations.Tree

@Tree("Tree1", ["Category1"])
@Aggregate("Action23", "none23", ["Action2", "Action3"])
@Category("Category1", "noneC1", ["Action1", "Action2", "Action3", "Action4"])
class TestClass {
    @Action("Action1", "none1")
    @Action("Action2", "none2")
    fun action12() {

    }

    @Action("Action3", "none3")
    @Action("Action4", "none4")
    fun action34() {

    }

}