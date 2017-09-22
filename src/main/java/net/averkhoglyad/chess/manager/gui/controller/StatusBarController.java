package net.averkhoglyad.chess.manager.gui.controller;

import javafx.fxml.FXML;
import net.averkhoglyad.chess.manager.gui.data.StatusBarModel;
import org.controlsfx.control.StatusBar;

public class StatusBarController {

    @FXML
    private StatusBar statusBar;

    private StatusBarModel statusBarModel = StatusBarModel.getInstance();

    public void initialize() {
        statusBar.textProperty().bind(statusBarModel.statusTextProperty());
        statusBar.progressProperty().bind(statusBarModel.statusProgressProperty());
    }

}
