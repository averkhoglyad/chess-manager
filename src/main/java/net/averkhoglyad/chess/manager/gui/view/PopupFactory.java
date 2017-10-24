package net.averkhoglyad.chess.manager.gui.view;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import static javafx.stage.Modality.WINDOW_MODAL;

public class PopupFactory {

    private final Stage primaryStage;

    public PopupFactory(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage create(String title, Parent content) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setScene(new Scene(content));
        return stage;
    }

    public FileChooserDialog fileChooser(Pair<String, String>... extensionFilters) {
        return fileChooser(null, extensionFilters);
    }

    public FileChooserDialog fileChooser(String initialFileName, Pair<String, String>... extensionFilters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(initialFileName);
        applyExtensionFilters(fileChooser, extensionFilters);
        return new FileChooserDialog(primaryStage, fileChooser);
    }

    private void applyExtensionFilters(FileChooser fileChooser, Pair<String, String>[] extensionFilters) {
        for (Pair<String, String> pair : extensionFilters) {
            fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(pair.getKey(), pair.getValue()));
        }
    }

}
