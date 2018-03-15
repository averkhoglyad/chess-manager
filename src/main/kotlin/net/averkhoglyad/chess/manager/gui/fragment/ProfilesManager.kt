package net.averkhoglyad.chess.manager.gui.fragment

import javafx.beans.binding.Bindings.isEmpty
import javafx.scene.Cursor
import javafx.scene.layout.BorderPane
import net.averkhoglyad.chess.manager.core.data.Profile
import net.averkhoglyad.chess.manager.core.util.noop1
import net.averkhoglyad.chess.manager.gui.util.fontawesome
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class ProfilesManager : Fragment() {

    val profiles by param<List<Profile>>()
    private val _profiles = profiles.toMutableList().observable()

    private var onAdd: (Profile) -> Unit = noop1
    private var onDrop: (Profile) -> Unit = noop1

    override val root: BorderPane = borderpane {
        top = label("Lichess profile") {
            padding = insets(10, 5)
        }
        center = hbox {
            padding = insets(5)

            val textField = textfield {
                prefWidth = 190.0
                action {
                    doAdd(text)
                    text = ""
                }
            }

            spacer {
                minWidth = 10.0
            }

            button("Add") {
                disableWhen(isEmpty(textField.textProperty()))
                action {
                    doAdd(textField.text)
                    textField.text = ""
                }
            }
        }
        bottom = pane {
            listview(_profiles) {
                prefWidth = 250.0
                prefHeight = 300.0
                minHeight = 300.0
                maxHeight = 300.0

                placeholder = label("No profiles")
                cellFormat { profile ->
                    val cell = this
                    text = profile.lichessId
                    graphic = fontawesome(FontAwesome.Glyph.USER_TIMES) {
                        cursor = Cursor.HAND
                        visibleWhen { cell.hoverProperty() }
                        setOnMouseClicked {
                            onDrop(profile)
                            _profiles.remove(profile)
                        }
                    }
                }
            }
        }
    }

    private fun doAdd(text: String) {
        if (text.isEmpty()) return
        val profile = Profile(text)
        onAdd(profile)
        _profiles.add(profile)
    }

    fun onAdd(fn: (Profile) -> Unit) {
        onAdd = fn
    }

    fun onDrop(fn: (Profile) -> Unit) {
        onDrop = fn
    }

}