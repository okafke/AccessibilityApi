package io.github.okafke.aapi.plugin

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import java.io.File

interface AApiExtension {
    val dir: DirectoryProperty

    val cacheDir: DirectoryProperty

    fun getDir(project: Project): File {
        return dir.map { d -> d.asFile }.orElse(File(project.rootProject.rootDir, "build/aapi-tree")).get()
    }

    fun getCacheDir(project: Project): File {
        return cacheDir.map { d -> d.asFile }.orElse(File(project.rootProject.rootDir, "build/aapi-tree")).get()
    }

}