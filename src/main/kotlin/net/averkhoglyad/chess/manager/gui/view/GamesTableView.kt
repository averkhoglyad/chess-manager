package net.averkhoglyad.chess.manager.gui.view

import javafx.beans.binding.Bindings.*
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleSetProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.TableCell
import javafx.scene.control.TableView
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color.BLACK
import javafx.scene.paint.Color.ORANGE
import javafx.util.Callback
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.Color
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.Game
import net.averkhoglyad.chess.manager.core.sdk.lichess.data.Player
import net.averkhoglyad.chess.manager.core.util.noop1
import net.averkhoglyad.chess.manager.gui.fragment.SelectColumn
import net.averkhoglyad.chess.manager.gui.fragment.SelectColumnScope
import net.averkhoglyad.chess.manager.gui.util.*
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.FontAwesome.Glyph.EYE
import tornadofx.*
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt
import kotlin.reflect.KClass

private val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")

class GamesTableView : View() {

    val loadingProperty = SimpleBooleanProperty(false)
    var loading by loadingProperty

    val gamesProperty = SimpleListProperty<Game>(FXCollections.observableArrayList())
    var games by gamesProperty

    val selectedGames = SimpleSetProperty<Game>(FXCollections.observableSet(TreeSet()))

    private val displayedGameProperty = SimpleObjectProperty<Game>()
    private var displayedGame by displayedGameProperty

    private var onSelect: (Collection<Game>) -> Unit = noop1
    private var onDeselect: (Collection<Game>) -> Unit = noop1
    private var onPreview: (Game) -> Unit = noop1

    override val root = tableview(gamesProperty) {
        placeholder = this@GamesTableView.placeholder()
        columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY // TODO: Try tornadofx policies
        sortPolicy = Callback<TableView<Game>, Boolean> { false }

        val selectColScope = SelectColumnScope(this@tableview, onSelect = { onSelect(it) }, onDeselect = { onDeselect(it) })

        fun toggleGameSelection(index: Int, isRange: Boolean) {
            val isSelection = !selectedGames.contains(this@tableview.items[index])
            if (isSelection) {
                if (isRange) {
                    selectColScope.selectRangeTo(index)
                } else {
                    selectColScope.selectItemOn(index)
                }
            } else {
                if (isRange) {
                    selectColScope.deselectRangeTo(index)
                } else {
                    selectColScope.deselectItemOn(index)
                }
            }
        }

        setOnKeyPressed { evt ->
            when (evt.code) {
                KeyCode.ENTER -> displayGame(this@tableview.selectionModel.selectedItem)
                KeyCode.SPACE -> toggleGameSelection(this@tableview.selectionModel.selectedIndex, evt.isShiftDown)
            }
        }

        column("", Boolean::class) {
            minWidth = 25.0
            maxWidth = 25.0
            prefWidth = 25.0
            isResizable = false
            graphic = CheckBox().apply {
                selectedProperty()
                        .forceWith(isNotEmpty(gamesProperty).and(selectedGames.containsAllProperty(gamesProperty)))
                disableWhen(isEmpty(gamesProperty))
                action {
                    if (isSelected) {
                        onDeselect(games)
                    } else {
                        onSelect(games)
                    }
                }
                indeterminateProperty()
                        .forceWith(not(selectedProperty()).and(selectedGames.containsAnyProperty(gamesProperty)))
            }

            setCellValueFactory {
                selectedGames.containsProperty(it.value)
            }

            cellFragment(selectColScope, SelectColumn::class as KClass<SelectColumn<Game>>)
        }

        column("", Game::class) {
            minWidth = 25.0
            maxWidth = 25.0
            prefWidth = 25.0
            isResizable = false

            setCellValueFactory {
                it.value.toProperty()
            }
            cellFormat { game ->
                alignment = Pos.CENTER
                graphic = fontawesome(EYE) {
                    cursor = Cursor.HAND
                    setOnMouseClicked {
                        displayGame(game)
                    }
                    textFillProperty().bind(`when`(displayedGameProperty.isEqualTo(game))
                            .then(ORANGE).otherwise(BLACK))
                }
            }
        }
        column("White", Game::class) {
            setCellValueFactory {
                it.value.toProperty()
            }
            cellFormat(playerCellFormatFn(Color.white))
        }
        column("Black", Game::class) {
            setCellValueFactory {
                it.value.toProperty()
            }
            cellFormat(playerCellFormatFn(Color.black))
        }
        column("Turns", Number::class) {
            setCellValueFactory {
                (it.value.turns.toFloat() / 2).roundToInt().toProperty()
            }
            minWidth = 50.0
            maxWidth = 50.0
            prefWidth = 50.0
            isResizable = false
        }
        column("Status", Game::status) {
            minWidth = 50.0
            maxWidth = 1000.0
            prefWidth = 50.0
        }
        column("Finished", ZonedDateTime::class) {
            setCellValueFactory {
                it.value.lastMoveAt?.atZone(ZoneId.systemDefault()).toProperty()
            }
            cellFormat {
                text = it?.format(dateTimeFormatter) ?: ""
            }
            minWidth = 75.0
            maxWidth = 1000.0
            prefWidth = 75.0
        }
    }

    private fun placeholder(): Node = group {
        label("No games") {
            hiddenWhen(loadingProperty)
        }
        progressbar {
            visibleWhen(loadingProperty)
            progress = -1.0
            maxWidth = 125.0
        }
    }

    private fun displayGame(game: Game?) {
        if (game == null) return
        displayedGame = game
        onPreview(game)
    }

    fun onSelect(op: (Collection<Game>) -> Unit) {
        onSelect = op
    }

    fun onDeselect(op: (Collection<Game>) -> Unit) {
        onDeselect = op
    }

    fun onPreview(op: (game: Game) -> Unit) {
        onPreview = op
    }

}

private fun playerCellFormatFn(targetColor: Color): TableCell<Game, Game>.(Game?) -> Unit = { game ->
    graphic = if(game?.winner == targetColor) fontawesome(FontAwesome.Glyph.TROPHY) else null
    text = game?.players?.get(targetColor).toLabel()
}

private fun Player?.toLabel() = if (this?.userId == null) "n/a" else "${this.userId} (${this.rating})"
