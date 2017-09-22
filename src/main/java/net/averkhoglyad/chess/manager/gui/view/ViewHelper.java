package net.averkhoglyad.chess.manager.gui.view;

import javafx.fxml.FXMLLoader;
import net.averkhoglyad.chess.manager.core.helper.ExceptionHelper;

public abstract class ViewHelper {

    private ViewHelper() {
    }

    public static <T> T loadFxmlView(String path) {
        return ExceptionHelper.doStrict(() -> FXMLLoader.load(ViewHelper.class.getClassLoader().getResource(path)));
    }

}
