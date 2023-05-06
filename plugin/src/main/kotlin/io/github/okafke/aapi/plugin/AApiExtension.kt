package io.github.okafke.aapi.plugin

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import java.io.File
import javax.inject.Inject

open class AApiExtension @Inject constructor(
    objectFactory: ObjectFactory
) {
    private val dir: DirectoryProperty = objectFactory.directoryProperty()
    private val cacheDir: DirectoryProperty = objectFactory.directoryProperty()

    fun getDir(project: Project): File {
        return dir.map { d -> d.asFile }.orElse(File(project.rootProject.rootDir, "build/aapi-tree")).get()
    }

    fun getCacheDir(project: Project): File {
        return cacheDir.map { d -> d.asFile }.orElse(File(project.rootProject.rootDir, "build/aapi-tree")).get()
    }

}