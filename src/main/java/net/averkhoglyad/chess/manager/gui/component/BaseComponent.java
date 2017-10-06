package net.averkhoglyad.chess.manager.gui.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import static javafx.application.Platform.runLater;
import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doStrict;

public abstract class BaseComponent extends Pane {

    public BaseComponent(String fxmlPath) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        runLater(() -> doStrict(() -> fxmlLoader.load()));
    }

    protected final void fitContentToComponentSize() {
        getChildren().forEach(it -> {
            if (it instanceof Region) {
                Region.class.cast(it).prefWidthProperty().bind(this.widthProperty());
                Region.class.cast(it).prefHeightProperty().bind(this.heightProperty());
            }
        });
    }

    protected final <E extends Event> ObjectProperty<EventHandler<E>> createHandler(EventType<E> eventType) {
        return createHandler(eventType, eventType.getName());
    }

    protected final <E extends Event> ObjectProperty<EventHandler<E>> createHandler(EventType<E> eventType, String name) {
        final BaseComponent bean = this;
        return new ObjectPropertyBase<EventHandler<E>>() {
            @Override protected void invalidated() {
                setEventHandler(eventType, get());
            }

            @Override
            public Object getBean() {
                return bean;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

}
