package net.averkhoglyad.chess.manager.gui.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.TOP_CENTER
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.Color
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.Game
import net.averkhoglyad.chess.manager.gui.fragment.ChessDiagram
import tornadofx.*

class PreviewView : View() {

    val loadingProperty = SimpleBooleanProperty(false)
    var loading by loadingProperty

    val gameProperty = SimpleObjectProperty<Game>()
    var game by gameProperty

    var currentUserId: String? = null

    override val root = stackpane {
        alignment = CENTER

        progressindicator {
            progress = -1.0
            maxWidth = 50.0
            visibleWhen(loadingProperty)
        }

        scrollpane {
            hiddenWhen(gameProperty.isNull)
            isFitToWidth = true
            padding = Insets(10.0)
            vbox {
                alignment = TOP_CENTER
                flowpane {
                    alignment = CENTER
                    gameProperty.onChange {
                        this.children.clear()
                        val params = mapOf(
                                ChessDiagram::fen to game?.fenDiagrams?.last(),
                                ChessDiagram::flipped to (game?.players?.get(Color.black)?.userId == currentUserId)
                        )
                        this += find<ChessDiagram>(params)
                    }
                }
                label {
                    gameProperty.onChange {
                        if (it != null) {
                            text = "${it.playerId(Color.white)} - ${it.playerId(Color.black)}"
                        } else {
                            text = null
                        }
                    }
                }
                label {
                    isWrapText = true
                    gameProperty.onChange {
                        text = it?.movesNotation()
                    }
                }
            }
        }
    }

}

private fun Game.playerId(color: Color) = players[color]?.userId ?: "n/a"

private fun Game.movesNotation(): String {
    val halfMoves = this.moves?.split("\\s".toRegex()) ?: emptyList()
    val builder = StringBuilder()
    for (i in 0 until halfMoves.size step 2) {
        builder.append(i / 2 + 1)
        builder.append('.')
        builder.append(' ')
        builder.append(halfMoves[i])
        builder.append(' ')
        if (i + 1 < halfMoves.size) {
            builder.append(halfMoves[i + 1])
        }
        builder.append(' ')
    }
    builder.append(' ')
    builder.append(this.result)
    return builder.toString()
}

private val Game.result
    get() = when {
        status?.isDrawn == true -> "½-½"
        winner == Color.white -> "1-0"
        winner == Color.black -> "0-1"
        else -> "*"
    }


