package net.averkhoglyad.chess.manager.core.sdk.lichess.data

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class Game @JvmOverloads constructor(var id: String = "",
                                          var variant: String? = null,
                                          var rated: Boolean = false,
                                          var status: Status? = null,
                                          var speed: String? = null,
                                          @field:JsonProperty("perf")
                                          var performance: String? = null,
                                          var createdAt: Instant? = null,
                                          var lastMoveAt: Instant? = null,
                                          var turns: Int = 0,
                                          @field:JsonProperty("color")
                                          var currentTurn: Color? = null,
                                          var winner: Color? = null,
                                          var url: String? = null,
                                          var moves: String? = null,
                                          var opening: Opening? = null,
                                          var players: Map<Color, Player> = mapOf(),
                                          @field:JsonProperty("fens")
                                          var fenDiagrams: List<String> = listOf()) {

    override fun equals(other: Any?): Boolean {
        return (other as? Game)?.id == this.id
    }
}

enum class Status {
    created,
    started,
    aborted,
    mate,
    resign,
    stalemate,
    timeout,
    draw,
    outoftime,
    cheat,
    nostart,
    unknownfinish,
    variantend;

    val isDrawn
        get() = this == stalemate || this == draw
}

enum class Color {
    white, black
}

data class Opening(var code: String? = null,
                   var name: String? = null)

data class Player(var userId: String? = "n/a",
                  var name: String? = "n/a",
                  var rating: Int? = 0)
