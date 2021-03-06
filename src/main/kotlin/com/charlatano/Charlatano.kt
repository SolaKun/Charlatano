/*
 * Charlatano: Free and open-source (FOSS) cheat for CS:GO/CS:CO
 * Copyright (C) 2017 - Thomas G. P. Nappo, Jonathan Beaudoin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

@file:JvmName("Charlatano")

package com.charlatano

import com.charlatano.game.CSGO
import com.charlatano.overlay.Overlay
import com.charlatano.scripts.*
import com.charlatano.scripts.aim.flatAim
import com.charlatano.scripts.aim.pathAim
import com.charlatano.scripts.esp.esp
import com.charlatano.settings.*
import com.charlatano.utils.Dojo
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

const val SETTINGS_DIRECTORY = "settings"

fun main(args: Array<String>) {
    setKotlinCompilerPath()
    setIdeaIoUseFallback()

    loadSettings()

    CSGO.initialize()

    bunnyHop()
    rcs()
    esp()
    flatAim()
    pathAim()
    boneTrigger()
    reducedFlash()
    bombTimer()

    Toggles_AIM()
    Toggles_BUNNYHOP()
    Toggles_ESP()
    Toggles_RAGE()
    Toggles_RCS()
    Toggles_BONETRIGGER()

    clearScreen()

    val scanner = Scanner(System.`in`)
    while (!Thread.interrupted()) {
        System.out.println()
        System.out.print("> ")
        when (scanner.nextLine()) {
            "exit", "quit", "e", "q" -> System.exit(0)
            "reload", "r" -> loadSettings()
            "reset" -> resetToggles()
            "toggles", "t" -> printToggles()
            "cls", "clear", "c" -> clearScreen()
            "ranks" -> getRanks()
        }
    }
}

private fun loadSettings() {
    Files.walk(Paths.get(SETTINGS_DIRECTORY)).forEach { scriptPath ->
        if (Files.isDirectory(scriptPath)) {
            return@forEach
        }
        Dojo.script(
            Files.readAllBytes(scriptPath).toString(Charsets.UTF_8)
        )
    }

    System.out.println("Loaded settings.")

    val needsOverlay = ENABLE_BOMB_TIMER or (ENABLE_ESP and (SKELETON_ESP or BOX_ESP))
    if (!Overlay.opened && needsOverlay) Overlay.open()
}

private fun resetToggles() {
    ENABLE_AIM = false
    ENABLE_BUNNY_HOP = false
    ENABLE_ESP = false
    ENABLE_RCS = false
    ENABLE_BONE_TRIGGER = false

    ENABLE_RAGE = false
    System.out.println("All togglables disabled.")
}

private fun printToggles() {
    System.out.println("AIM      = $ENABLE_AIM")
    System.out.println("BunnyHop = $ENABLE_BUNNY_HOP")
    System.out.println("ESP      = $ENABLE_ESP")
    System.out.println("Rage     = $ENABLE_RAGE")
    System.out.println("RCS      = $ENABLE_RCS")
    System.out.println("Trigger  = $ENABLE_BONE_TRIGGER")
}

private fun clearScreen() {
    System.out.println("  =============+========+========================= ")
    System.out.println(" | Command     | Alias  | Function                |")
    System.out.println("  =============+========+========================= ")
    System.out.println(" | clear       | cls, c | Clears console screen   |")
    System.out.println(" | exit / quit | e, q   | Stops Charlatano        |")
    System.out.println(" | reload      | r      | Reloads /settings       |")
    System.out.println(" | reset       |        | Disables all toggles    |")
    System.out.println(" | toggles     | t      | Show what is toggled    |")
    System.out.println(" | ranks       |        | Show ranks              |")
    System.out.println("  =============+========+========================= ")
    System.out.println()
}

private fun setKotlinCompilerPath() {
    System.setProperty(
        "kotlin.compiler.jar",
        K2JVMCompiler::class.java.protectionDomain.codeSource.location.toURI().path
    )
}
