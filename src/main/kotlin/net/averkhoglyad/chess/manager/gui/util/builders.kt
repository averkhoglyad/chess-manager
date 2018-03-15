package net.averkhoglyad.chess.manager.gui.util

import javafx.event.EventTarget
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.SplitMenuButton
import org.controlsfx.control.MasterDetailPane
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.Glyph
import tornadofx.opcr

fun EventTarget.splitmenubutton(text: String = "", graphic: Node? = null, op: SplitMenuButton.() -> Unit = {}): SplitMenuButton {
    val button = SplitMenuButton()
    button.text = text
    if (graphic != null) button.graphic = graphic
    return opcr(this, button, op)
}

fun fontawesome(icon: FontAwesome.Glyph, op: Glyph.() -> Unit = {}) = Glyph("FontAwesome", icon).also(op)

fun EventTarget.masterdetailpane(side: Side = Side.RIGHT, op: MasterDetailPane.() -> Unit = {}): MasterDetailPane {
    val pane = MasterDetailPane()
    pane.detailSide = side
    return opcr(this, pane, op)
}

val MasterDetailPane.animatedProperty
    get() = this.animatedProperty()!!

var MasterDetailPane.animated
    get() = animatedProperty.get()
    set( value ) = animatedProperty.set(value)
