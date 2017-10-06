package net.averkhoglyad.chess.manager.gui.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

import java.util.List;

public class DataListEvent<E> extends DataCollectionEvent<E, List<? extends E>> {

    public DataListEvent(EventType<? extends Event> eventType, List<E> value) {
        super(eventType, value);
    }

    public DataListEvent(Object source, EventTarget target, EventType<? extends Event> eventType, List<E> value) {
        super(source, target, eventType, value);
    }

}
