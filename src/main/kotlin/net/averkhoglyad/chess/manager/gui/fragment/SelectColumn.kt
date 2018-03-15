package net.averkhoglyad.chess.manager.gui.fragment

import javafx.scene.control.CheckBox
import javafx.scene.control.TableView
import javafx.scene.input.MouseEvent
import net.averkhoglyad.chess.manager.core.util.noop1
import net.averkhoglyad.chess.manager.gui.util.forceWith
import tornadofx.Scope
import tornadofx.TableCellFragment
import kotlin.math.max
import kotlin.math.min

class SelectColumn<S> : TableCellFragment<S, Boolean>() {

    override val scope = (super.scope as SelectColumnScope<S>)

    override val root = CheckBox().apply {
        selectedProperty().forceWith(itemProperty)
        setOnMouseClicked { evt: MouseEvent ->
            cell!!.let { cell ->
                val isSelection = itemProperty.value.not()
                val isRange = evt.isShiftDown
                if (isSelection) {
                    if (isRange) {
                        scope.selectRangeTo(cell.index)
                    } else {
                        scope.selectItemOn(cell.index)
                    }
                } else {
                    if (isRange) {
                        scope.deselectRangeTo(cell.index)
                    } else {
                        scope.deselectItemOn(cell.index)
                    }
                }
            }
        }
    }

}

class SelectColumnScope<in S>(private val table: TableView<S>,
                              private val onSelect: ((Collection<S>) -> Unit) = noop1,
                              private val onDeselect: (Collection<S>) -> Unit = noop1) : Scope() {

    private var lastTargetIndex: Int = 0

    fun selectItemOn(index: Int) {
        if (index < 0) return
        onSelect(table.items.slice(index..index))
        lastTargetIndex = index
    }

    fun deselectItemOn(index: Int) {
        if (index < 0) return
        onDeselect(table.items.slice(index..index))
        lastTargetIndex = index
    }

    fun selectRangeTo(index: Int) {
        if (index < 0) return
        onSelect(collectItemsRangeTo(index))
        lastTargetIndex = index
    }

    fun deselectRangeTo(index: Int) {
        if (index < 0) return
        onDeselect(collectItemsRangeTo(index))
        lastTargetIndex = index
    }

    private fun collectItemsRangeTo(index: Int): List<S> {
        val from = min(lastTargetIndex, index)
        val to = max(lastTargetIndex, index)
        val range = from..to
        return table.items.slice(range)
    }

}
