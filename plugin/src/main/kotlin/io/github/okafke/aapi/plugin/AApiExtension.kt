package io.github.okafke.aapi.plugin

import org.gradle.api.file.DirectoryProperty

interface AApiExtension {
    fun getDir(): DirectoryProperty

    fun getCacheDir(): DirectoryProperty

}