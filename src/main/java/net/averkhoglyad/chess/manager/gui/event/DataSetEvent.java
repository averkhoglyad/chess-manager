package net.averkhoglyad.chess.manager.gui.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

import java.util.Set;

public class DataSetEvent<E> extends DataCollectionEvent<E, Set<? extends E>> {

    public DataSetEvent(EventType<? extends Event> eventType, Set<? extends E> value) {
        super(eventType, value);
    }

    public DataSetEvent(Object source, EventTarget target, EventType<? extends Event> eventType, Set<? extends E> value) {
        super(source, target, eventType, value);
    }

}
