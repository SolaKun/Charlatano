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

package com.charlatano.game.offsets

import com.charlatano.game.CSGO.clientDLL
import com.charlatano.game.CSGO.csgoEXE
import com.charlatano.game.offsets.ClientOffsets.decalname
import com.charlatano.utils.extensions.uint

fun findDecal(): Long {
    val mask = ByteArray(4)
    for (i in 0..3) mask[i] = (((decalname shr 8 * i)) and 0xFF).toByte()

    val memory = Offset.memoryByModule[clientDLL]!!

    var skipped = 0
    var currentAddress = 0L
    while (currentAddress < clientDLL.size - mask.size) {
        if (memory.mask(currentAddress, mask, false)) {
            if (skipped < 5) { // skips
                currentAddress += 0xA // skipSize
                skipped++
                continue
            }
            return currentAddress + clientDLL.address
        }
        currentAddress++
    }

    return -1L
}

fun findFirstClass() = csgoEXE.uint(findDecal() + 0x3B)
