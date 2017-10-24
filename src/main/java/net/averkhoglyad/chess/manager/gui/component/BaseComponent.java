package net.averkhoglyad.chess.manager.gui.component;

import javafx.beans.DefaultProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberExpression;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import static javafx.application.Platform.runLater;
import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doStrict;

@DefaultProperty("children")
public abstract class BaseComponent extends Region {

    public BaseComponent(String fxmlPath) {
        getStyleClass().add("base-component");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        runLater(() -> doStrict(() -> fxmlLoader.load()));
    }

    protected final void fitContentToComponentSize() {
        getChildren().stream()
            .filter(it -> it instanceof Region)
            .map(Region.class::cast)
            .forEach(it -> {
                it.prefWidthProperty().bind(this.widthProperty());
                it.prefHeightProperty().bind(this.heightProperty());
            });
    }

    protected final void fitContentToComponentWidth() {
        getChildren().stream()
            .filter(it -> it instanceof Region)
            .map(Region.class::cast)
            .forEach(it -> {
                it.prefWidthProperty().bind(this.widthProperty());
            });
    }

    protected final void fitContentToComponentHeight() {
        getChildren().stream()
            .filter(it -> it instanceof Region)
            .map(Region.class::cast)
            .forEach(it -> {
                it.prefHeightProperty().bind(this.heightProperty());
            });
    }

    protected final void fitComponentToContentSize() {
        Size target = new Size();

        getChildren().stream()
            .filter(it -> it instanceof Region)
            .map(Region.class::cast)
            .map(it -> new Size(it.widthProperty(), it.heightProperty()))
            .forEach(size -> {
                target.width = target.width == null ? size.width : Bindings.max(target.width, size.width);
                target.height = target.height == null ? size.height : Bindings.max(target.height, size.height);
            });

        if( target.width != null ) this.prefWidthProperty().bind(target.width);
        if( target.height != null ) this.prefHeightProperty().bind(target.height);
    }

    @Override
    public final ObservableList<Node> getChildren() {
        return super.getChildren();
    }

    private static final class Size {

        NumberExpression width;
        NumberExpression height;

        public Size() {
        }

        public Size(NumberExpression width, NumberExpression height) {
            this.width = width;
            this.height = height;
        }

    }

    protected final class EventHandlerProperty<E extends Event> extends ObjectPropertyBase<EventHandler<E>> {

        private final EventType<E> eventType;
        private final String name;

        public EventHandlerProperty(EventType<E> eventType) {
            this(eventType.getName(), eventType);
        }

        private EventHandlerProperty(String name, EventType<E> eventType) {
            this.eventType = eventType;
            this.name = name;
        }

        @Override
        protected void invalidated() {
            BaseComponent.this.setEventHandler(eventType, get());
        }

        @Override
        public Object getBean() {
            return BaseComponent.this;
        }

        @Override
        public String getName() {
            return name;
        }

    }

}
