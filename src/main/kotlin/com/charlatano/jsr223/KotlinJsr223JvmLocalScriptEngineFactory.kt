/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.charlatano.jsr223

import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineFactoryBase
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.utils.PathUtil
import java.io.File
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine
import kotlin.script.templates.standard.ScriptTemplateWithArgs

class KotlinJsr223JvmLocalScriptEngineFactory : KotlinJsr223JvmScriptEngineFactoryBase() {

	override fun getScriptEngine(): ScriptEngine =
		KotlinJsr223JvmLocalScriptEngine(
			Disposer.newDisposable(),
			this,
			scriptCompilationClasspathFromContext("kotlin-compiler-embeddable.jar"),
			KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
			{ ctx, types -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), types ?: emptyArray()) },
			arrayOf(Bindings::class)
		)
}

private fun File.existsOrNull(): File? = existsAndCheckOrNull { true }
private inline fun File.existsAndCheckOrNull(check: (File.() -> Boolean)): File? = if (exists() && check()) this else null

private fun <T> Iterable<T>.anyOrNull(predicate: (T) -> Boolean) = if (any(predicate)) this else null

private fun File.matchMaybeVersionedFile(baseName: String) =
	name == baseName ||
		name == baseName.removeSuffix(".jar") || // for classes dirs
		name.startsWith(baseName.removeSuffix(".jar") + "-")

private fun contextClasspath(keyName: String, classLoader: ClassLoader): List<File>? =
	(classpathFromClassloader(classLoader)?.anyOrNull { it.matchMaybeVersionedFile(keyName) }
		?: manifestClassPath(classLoader)?.anyOrNull { it.matchMaybeVersionedFile(keyName) }
		)?.toList()

private val validJarExtensions = setOf("jar", "zip")

private fun scriptCompilationClasspathFromContext(keyName: String, classLoader: ClassLoader = Thread.currentThread().contextClassLoader): List<File> =
	(System.getProperty("kotlin.script.classpath")?.split(File.pathSeparator)?.map(::File)
		?: contextClasspath(keyName, classLoader)
		).let {
		it?.plus(kotlinScriptStandardJars) ?: kotlinScriptStandardJars
	}
		.mapNotNull { it?.canonicalFile }
		.distinct()
		.filter { (it.isDirectory || (it.isFile && it.extension.toLowerCase() in validJarExtensions)) && it.exists() }

private val kotlinStdlibJar: File? by lazy {
	PathUtil.getResourcePathForClass(JvmStatic::class.java).existsOrNull()
}

private val kotlinScriptRuntimeJar: File? by lazy {
	PathUtil.getResourcePathForClass(ScriptTemplateWithArgs::class.java).existsOrNull()
}

private val kotlinScriptStandardJars by lazy { listOf(kotlinStdlibJar, kotlinScriptRuntimeJar) }
