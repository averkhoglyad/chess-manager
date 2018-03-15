package net.averkhoglyad.chess.manager.gui.fragment

import javafx.beans.binding.Bindings
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.TOP_CENTER
import javafx.scene.control.Label
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.text.Font
import net.averkhoglyad.chess.manager.core.util.SimplePoolImpl
import tornadofx.*
import java.util.*
import java.util.function.Supplier

const val EMPTY_BOARD = "8/8/8/8/8/8/8/8"
const val START_BOARD = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

private var PIECES_MAP: Map<String, String> = mapOf(
        ("B" to "\u2657"),
        ("K" to "\u2654"),
        ("N" to "\u2658"),
        ("P" to "\u2659"),
        ("Q" to "\u2655"),
        ("R" to "\u2656"),
        ("b" to "\u265D"),
        ("k" to "\u265A"),
        ("n" to "\u265E"),
        ("p" to "\u265F"),
        ("q" to "\u265B"),
        ("r" to "\u265C")
)

class ChessDiagram : Fragment() {

    val fen by param(EMPTY_BOARD)
    val flipped by param(false)

    private val cellSize = 25.0

    private val pieceFont = Font.loadFont(resources.stream("/fonts/arial-unicode-ms.ttf"), cellSize * 0.8)

    private val labelsPool = SimplePoolImpl(Supplier { Label().apply { this.font = pieceFont } }, 32)
    private val usedLabels = ArrayList<Label>(32)

    override val root = stackpane {
        alignment = TOP_CENTER
        val board = gridpane {
            alignment = CENTER
            border = Border(BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS))
            for (row in 0..7) {
                this.row {
                    for (col in 0..7) {
                        val color = if ((row + col) % 2 == 0) Color.WHITESMOKE else Color.BURLYWOOD
                        rectangle {
                            fill = color
                            width = cellSize
                            height = cellSize
                        }
                    }
                }
            }
        }

        parseFen(fen).forEach { (figure, index) ->
            val piece = labelsPool.provide()
            piece.text = PIECES_MAP[figure]

            val effectedIndex = if (flipped) 63 - index else index
            val cell = board.children[effectedIndex]
            piece.translateXProperty().bind(
                    cell.layoutXProperty()
                            .subtract(piece.layoutXProperty())
                            .add(Bindings.subtract(cellSize, piece.widthProperty()).divide(2))
            )
            piece.translateYProperty().bind(
                    cell.layoutYProperty()
                            .subtract(piece.layoutYProperty())
                            .add(Bindings.subtract(cellSize, piece.heightProperty()).divide(2))
            )
            usedLabels.add(piece)
            this += piece
        }
    }

    private fun parseFen(fen: String): List<Pair<String, Int>> {
        var fen = fen.trim { it <= ' ' }
        val i = fen.indexOf(' ')
        if (i > 0) {
            fen = fen.substring(0, i)
        }
        val result = mutableListOf<Pair<String, Int>>()
        val rows = fen.split('/').dropLastWhile { it.isEmpty() }.toTypedArray()
        var position = 0
        for (row in rows) {
            for (ch in row.toCharArray()) {
                val s = String(charArrayOf(ch))
                if (Character.isAlphabetic(ch.toInt())) {
                    result.add(Pair(s, position++))
                } else {
                    position += Integer.valueOf(s)
                }
            }
        }
        return result
    }

}