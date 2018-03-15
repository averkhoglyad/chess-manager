package net.averkhoglyad.chess.manager.gui.data

import tornadofx.FXEvent

open class DataEvent<out E>(val value: E) : FXEvent()

open class DataCollectionEvent<out E, out C : Collection<E>>(value: C) : DataEvent<C>(value)

open class DataListEvent<out E>(value: List<E>) : DataCollectionEvent<E, List<E>>(value)

open class DataSetEvent<out E>(value: Set<E>) : DataCollectionEvent<E, Set<E>>(value)
