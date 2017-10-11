package net.averkhoglyad.chess.manager.gui.view;

import javafx.fxml.FXMLLoader;

import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doStrict;

public abstract class ViewHelper {

    private ViewHelper() {
    }

    public static <T> T loadFxmlView(String path) {
        return doStrict(() -> FXMLLoader.load(ViewHelper.class.getClassLoader().getResource(path)));
    }

}
