package net.averkhoglyad.chess.manager.gui.view;

import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doStrict;

public class ViewFactory {

    private final List<Object> controllers;

    public ViewFactory() {
        this(Collections.emptyList());
    }

    public ViewFactory(List<?> controllers) {
        this.controllers = Collections.unmodifiableList(new ArrayList<>(controllers));
    }

    public <T> T loadFxmlView(String path) {
        FXMLLoader loader = new FXMLLoader(getResource(path));
        loader.setControllerFactory((Class<?> type) ->
        {
            return controllers.stream()
                .filter(it -> it.getClass() == type)
                .findFirst()
                .orElseGet(() -> doStrict(() -> type.newInstance()));
        });
        return doStrict(() -> loader.load());
    }

    private URL getResource(String path) {
        return getClass().getClassLoader().getResource(path);
    }

}
