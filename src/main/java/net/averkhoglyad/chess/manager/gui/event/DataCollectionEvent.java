package net.averkhoglyad.chess.manager.gui.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

import java.util.Collection;

public class DataCollectionEvent<E, C extends Collection<? extends E>> extends DataEvent<C> {

    public DataCollectionEvent(EventType<? extends Event> eventType, C value) {
        super(eventType, value);
    }

    public DataCollectionEvent(Object source, EventTarget target, EventType<? extends Event> eventType, C value) {
        super(source, target, eventType, value);
    }

}
