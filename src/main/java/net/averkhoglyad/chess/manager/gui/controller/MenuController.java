package net.averkhoglyad.chess.manager.gui.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;
import net.averkhoglyad.chess.manager.core.sdk.data.User;
import net.averkhoglyad.chess.manager.gui.data.ApplicationModel;
import net.averkhoglyad.chess.manager.gui.data.StatusBarModel;
import net.averkhoglyad.chess.manager.gui.event.ApplicationEventDispatcher;
import net.averkhoglyad.chess.manager.gui.event.FileEvent;
import net.averkhoglyad.chess.manager.gui.event.ViewEvent;
import net.averkhoglyad.chess.manager.gui.view.AlertHelper;

public class MenuController {

    @FXML
    public MenuItem noUsersMenuItem;
    @FXML
    private MenuButton usersMenuButton;
    @FXML
    private Label pagingLabel;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private SplitMenuButton pgnDownloadButton;

    private ApplicationModel applicationModel = ApplicationModel.getInstance();
    private StatusBarModel statusBarModel = StatusBarModel.getInstance();

    private ApplicationEventDispatcher eventDispatcher = ApplicationEventDispatcher.getInstance();

    public void initialize() {
        noUsersMenuItem.visibleProperty().bind(Bindings.isEmpty(applicationModel.getUsers()));

        pagingLabel.textProperty().bind(
            Bindings.concat(applicationModel.currentPageProperty(), " / ", applicationModel.totalPagesProperty())
        );

        pagingLabel.visibleProperty().bind(applicationModel.totalPagesProperty().greaterThan(1));
        prevButton.visibleProperty().bind(applicationModel.totalPagesProperty().greaterThan(1));
        nextButton.visibleProperty().bind(applicationModel.totalPagesProperty().greaterThan(1));

        prevButton.disableProperty().bind(applicationModel.currentPageProperty().greaterThan(1).not());
        nextButton.disableProperty().bind(applicationModel.currentPageProperty().lessThan(applicationModel.totalPagesProperty()).not());

        applicationModel.getUsers()
            .addListener((ListChangeListener<? super User>) c -> {
                usersMenuButton.getItems().setAll(noUsersMenuItem);
                c.getList()
                    .stream()
                    .map(User::getUsername)
                    .map(username -> {
                        MenuItem item = new MenuItem();
                        item.setText(username);
                        item.setUserData(username);
                        item.setOnAction(this::selectUser);
                        return item;
                    })
                    .forEach(usersMenuButton.getItems()::add);
            });

        pgnDownloadButton.setText(Integer.toString(applicationModel.getSelectedGames().size()) + " games");
        applicationModel.getSelectedGames()
            .addListener((SetChangeListener<? super Game>) c -> {
                pgnDownloadButton.setDisable(c.getSet().isEmpty());
                pgnDownloadButton.setText(Integer.toString(c.getSet().size()) + " games");
            });
    }

    public void showAddUserPopup(ActionEvent event) {
        eventDispatcher.trigger(ViewEvent.SHOW_ADD_USER_POPUP);
    }

    public void selectUser(ActionEvent event) {
        MenuItem menuItem = MenuItem.class.cast(event.getSource());
        applicationModel.setCurrentPage(0);
        eventDispatcher.trigger(ViewEvent.SELECT_USER, menuItem.getUserData());
        applicationModel.getSelectedGames().clear();
    }

    public void prevPage(ActionEvent event) {
        if (applicationModel.getCurrentPage() > 1) {
            applicationModel.setCurrentPage(applicationModel.getCurrentPage() - 1);
        }
    }

    public void nextPage(ActionEvent event) {
        if (applicationModel.getCurrentPage() < applicationModel.getTotalPages()) {
            applicationModel.setCurrentPage(applicationModel.getCurrentPage() + 1);
        }
    }

    public void importPgn(ActionEvent event) {
        if (applicationModel.getSelectedGames().isEmpty()) {
            return;
        }
        eventDispatcher.trigger(ViewEvent.SHOW_IMPORT_FILE_POPUP, FileEvent.IMPORT_PGN);
    }

    public void clearSelectedGames(ActionEvent event) {
        applicationModel.getSelectedGames().clear();
    }
                                
    public void showAboutPopup(ActionEvent event) {
        AlertHelper.info("About", "Free tool to download games from lichess.com as single PGN file.\n\nCurrent version: 0.1-ALFA\nFeedback email: awer.doc@gmail.com");
    }

    public void showResourcesPopup(ActionEvent event) {
        AlertHelper.info("Used resources and technologies", "Platform: JavaFX 8.0\nCompiler: ExcelsiorJet 12.0\nComponents: ControlsFX\nIcons and Images: FontAwesome, Veryicon");
    }

}
