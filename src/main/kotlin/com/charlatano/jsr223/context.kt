package com.charlatano.jsr223

import java.io.File
import java.net.URI
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.Manifest

private fun URL.toFile() =
	try {
		File(toURI().schemeSpecificPart)
	}
	catch (e: java.net.URISyntaxException) {
		if (protocol != "file") null
		else File(file)
	}

fun classpathFromClassloader(classLoader: ClassLoader): List<File>? =
	generateSequence(classLoader) { it.parent }.toList().flatMap { (it as? URLClassLoader)?.urLs?.mapNotNull(URL::toFile) ?: emptyList() }

// Maven runners sometimes place classpath into the manifest, so we can use it for a fallback search
fun manifestClassPath(classLoader: ClassLoader): List<File>? =
	classLoader.getResources("META-INF/MANIFEST.MF")
		.asSequence()
		.mapNotNull { ifFailed(null) { it.openStream().use { Manifest().apply { read(it) } } } }
		.flatMap { it.mainAttributes?.getValue("Class-Path")?.splitToSequence(" ") ?: emptySequence() }
		.mapNotNull { ifFailed(null) { File(URI.create(it)) } }
		.toList()
		.let { if (it.isNotEmpty()) it else null }

private inline fun <R> ifFailed(default: R, block: () -> R) = try {
	block()
} catch (t: Throwable) {
	default
}
