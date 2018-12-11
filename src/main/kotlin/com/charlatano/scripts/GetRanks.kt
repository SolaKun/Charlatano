package com.charlatano.scripts

import com.charlatano.game.CSGO.clientDLL
import com.charlatano.game.CSGO.csgoEXE
import com.charlatano.game.entity.EntityType.Companion.ccsPlayer
import com.charlatano.game.forEntities
import com.charlatano.game.netvars.NetVarOffsets.iCompetitiveRanking
import com.charlatano.game.offsets.ClientOffsets.dwPlayerResource
import com.charlatano.game.offsets.ClientOffsets.dwRadarBase
import com.charlatano.utils.extensions.uint

private val rankList = listOf(
    "Unranked",
    "Silver I",
    "Silver II",
    "Silver III",
    "Silver IV",
    "Silver Elite",
    "Silver Elite Master",
    "Nova I",
    "Nova II",
    "Nova III",
    "Nova Master",
    "Master Guardian I",
    "Master Guardian II",
    "Master Guardian Elite",
    "Distinguished Master Guardian",
    "Legendary Eagle",
    "Legendary Eagle Master",
    "Supreme Master First Class",
    "The Global Elite"
)
private val playerResource = clientDLL.uint(dwPlayerResource)
private val playerNameBase = clientDLL.uint(dwRadarBase)
private val nameBase = csgoEXE.uint(playerNameBase + 0x68)
private var rankIndex = 1

fun getRanks() {
    rankIndex = 1
    System.out.println("=============WARNING=============")
    System.out.println("It's only working on Matchmaking")
    System.out.println("If name's are glitchy turn back game and wait another round or spam few times XD")
    forEntities(ccsPlayer) {
        rankIndex++

        val playerName = (0..31).joinToString(separator = "") { i ->
            csgoEXE.read(nameBase + (0x168 * rankIndex) + 0x14 + i * 2, 2, false)!!.getString(0)
        }

        val rank =
            rankList[csgoEXE.int((playerResource + iCompetitiveRanking) + (rankIndex * 4))] // selecting rank with rank number
        System.out.println("$playerName rank is : $rank")
    }
}
