package net.averkhoglyad.chess.manager.gui.util

import javafx.beans.property.Property
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener
import tornadofx.onChange

fun <E> ObservableList<E>.containsProperty(obj: E): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.contains(obj))
    this.onChange { c: ListChangeListener.Change<out E> ->
        result.set(c.list.contains(obj))
    }
    return result
}

fun <E> ObservableList<E>.containsProperty(objProperty: ObservableValue<E>): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.contains(objProperty.value))
    objProperty.onChange {
        result.set(this.contains(it))
    }
    this.onChange { c: ListChangeListener.Change<out E> ->
        result.set(c.list.contains(objProperty.value))
    }
    return result
}

fun <E> ObservableList<E>.containsAllProperty(list: ObservableList<E>): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.containsAll(list))
    list.onChange { c: ListChangeListener.Change<out E> ->
        result.set(this.containsAll(c.list))
    }
    this.onChange { c: ListChangeListener.Change<out E> ->
        result.set(c.list.containsAll(list))
    }
    return result
}

fun <E> ObservableList<E>.containsAllProperty(set: ObservableSet<E>): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.containsAll(set))
    set.onChange { c: SetChangeListener.Change<out E> ->
        result.set(this.containsAll(c.set))
    }
    this.onChange { c: ListChangeListener.Change<out E> ->
        result.set(c.list.containsAll(set))
    }
    return result
}

fun <E> ObservableList<E>.containsAllProperty(collection: Collection<E>): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.containsAll(collection))
    this.onChange { c: ListChangeListener.Change<out E> ->
        result.set(c.list.containsAll(collection))
    }
    return result
}

fun <E> ObservableList<E>.containsAnyProperty(list: ObservableList<E>): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.any { list.contains(it) })
    list.onChange { c: ListChangeListener.Change<out E> ->
        result.set(this.any { c.list.contains(it) })
    }
    this.onChange { c: ListChangeListener.Change<out E> ->
        result.set(c.list.any { list.contains(it) })
    }
    return result
}

fun <E> ObservableList<E>.containsAnyProperty(set: ObservableSet<E>): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.any { set.contains(it) })
    set.onChange { c: SetChangeListener.Change<out E> ->
        result.set(this.any { c.set.contains(it) })
    }
    this.onChange { c: ListChangeListener.Change<out E> ->
        result.set(c.list.any { set.contains(it) })
    }
    return result
}

fun <E> ObservableList<E>.containsAnyProperty(collection: Collection<E>): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.any { collection.contains(it) })
    this.onChange { c: ListChangeListener.Change<out E> ->
        result.set(c.list.any { collection.contains(it) })
    }
    return result
}

fun <E> ObservableSet<E>.containsProperty(obj: E): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.contains(obj))
    this.onChange { c: SetChangeListener.Change<out E> ->
        result.set(c.set.contains(obj))
    }
    return result
}

fun <E> ObservableSet<E>.containsProperty(objProperty: ObservableValue<E>): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.contains(objProperty.value))
    objProperty.onChange {
        result.set(this.contains(it))
    }
    this.onChange { c: SetChangeListener.Change<out E> ->
        result.set(c.set.contains(objProperty.value))
    }
    return result
}

fun <E> ObservableSet<E>.containsAllProperty(set: ObservableSet<E>): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.containsAll(set))
    set.onChange { c: SetChangeListener.Change<out E> ->
        result.set(this.containsAll(c.set))
    }
    this.onChange { c: SetChangeListener.Change<out E> ->
        result.set(c.set.containsAll(set))
    }
    return result
}

fun <E> ObservableSet<E>.containsAllProperty(list: ObservableList<E>): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.containsAll(list))
    list.onChange { c: ListChangeListener.Change<out E> ->
        result.set(this.containsAll(c.list))
    }
    this.onChange { c: SetChangeListener.Change<out E> ->
        result.set(c.set.containsAll(list))
    }
    return result
}

fun <E> ObservableSet<E>.containsAllProperty(collection: Collection<E>): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.containsAll(collection))
    this.onChange { c: SetChangeListener.Change<out E> ->
        result.set(c.set.containsAll(collection))
    }
    return result
}

fun <E> ObservableSet<E>.containsAnyProperty(set: ObservableSet<E>): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.any { set.contains(it) })
    set.onChange { c: SetChangeListener.Change<out E> ->
        result.set(this.any { c.set.contains(it) })
    }
    this.onChange { c: SetChangeListener.Change<out E> ->
        result.set(c.set.any { set.contains(it) })
    }
    return result
}

fun <E> ObservableSet<E>.containsAnyProperty(list: ObservableList<E>): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.any { list.contains(it) })
    list.onChange { c: ListChangeListener.Change<out E> ->
        result.set(this.any { c.list.contains(it) })
    }
    this.onChange { c: SetChangeListener.Change<out E> ->
        result.set(c.set.any { list.contains(it) })
    }
    return result
}

fun <E> ObservableSet<E>.containsAnyProperty(collection: Collection<E>): ObservableBooleanValue {
    val result = ReadOnlyBooleanWrapper(this.any { collection.contains(it) })
    this.onChange { c: SetChangeListener.Change<out E> ->
        result.set(c.set.any { collection.contains(it) })
    }
    return result
}

fun <T> ObservableSet<T>.onChange(op: (SetChangeListener.Change<out T>) -> Unit) = apply {
    addListener(SetChangeListener { op(it) })
}

fun <T> Property<T>.forceWith(other: ObservableValue<T>) {
    this.value = other.value
    other.onChange {
        this.value = other.value
    }
    this.onChange {
        if (this.value != other.value) {
            this.value = other.value
        }
    }
}
