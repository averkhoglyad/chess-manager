package net.averkhoglyad.chess.manager.gui.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class DataEvent<E> extends Event {

    private final E value;

    public DataEvent(EventType<? extends Event> eventType, E value) {
        super(eventType);
        this.value = value;
    }

    public DataEvent(Object source, EventTarget target, EventType<? extends Event> eventType, E value) {
        super(source, target, eventType);
        this.value = value;
    }

    public E getValue() {
        return value;
    }

}
