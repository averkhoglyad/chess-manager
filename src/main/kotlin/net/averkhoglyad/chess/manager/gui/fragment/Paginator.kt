package net.averkhoglyad.chess.manager.gui.fragment

import javafx.beans.binding.Bindings.concat
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Pos
import net.averkhoglyad.chess.manager.core.util.noop1
import net.averkhoglyad.chess.manager.gui.util.fontawesome
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.FontAwesome.Glyph.LONG_ARROW_LEFT
import tornadofx.*

class Paginator : Fragment() {

    val totalProperty = SimpleIntegerProperty()
    private var total by totalProperty
    var pageProperty = SimpleIntegerProperty()
    private var page by pageProperty
    private var onChange: (Int) -> Unit = noop1

    override val root = hbox {
        alignment = Pos.BASELINE_CENTER
        button {
            prefWidth = 25.0
            alignment = Pos.CENTER
            graphic = fontawesome(LONG_ARROW_LEFT) {
                alignment = Pos.TOP_CENTER
                fontSize = 10.0
            }
            visibleWhen(totalProperty.greaterThan(1))
            disableWhen(pageProperty.eq(1))
            action {
                onChange(page - 1)
            }
        }
        label {
            // TODO: Add control to navigate to any page
            textProperty().bind(concat(pageProperty, " / ", totalProperty))
            visibleWhen(totalProperty.greaterThan(0))
            padding = insets(horizontal = 7.0)
        }
        button {
            graphic = fontawesome(FontAwesome.Glyph.LONG_ARROW_RIGHT) {
                alignment = Pos.TOP_CENTER
                fontSize = 10.0
            }
            prefWidth = 25.0
            alignment = Pos.CENTER
            visibleWhen { totalProperty.greaterThan(1) }
            disableWhen { pageProperty.eq(totalProperty) }
            action {
                onChange(page + 1)
            }
        }
    }

    fun onChange(fn: (Int) -> Unit) {
        onChange = fn
    }

}
