package net.averkhoglyad.chess.manager.gui.controller;

import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.fxml.FXML;
import net.averkhoglyad.chess.manager.core.sdk.data.User;
import net.averkhoglyad.chess.manager.gui.component.TopMenu;
import net.averkhoglyad.chess.manager.gui.data.ApplicationModel;
import net.averkhoglyad.chess.manager.gui.event.ApplicationEventDispatcher;
import net.averkhoglyad.chess.manager.gui.event.DataEvent;
import net.averkhoglyad.chess.manager.gui.event.FileEvent;
import net.averkhoglyad.chess.manager.gui.event.ViewEvent;

public class RootController {

    @FXML
    private TopMenu topMenu;

    // TODO: Remove deprecated singletons
    private ApplicationEventDispatcher eventDispatcher = ApplicationEventDispatcher.getInstance();
    private ApplicationModel applicationModel = ApplicationModel.getInstance();

    public void initialize() {
        Bindings.bindContent(topMenu.getUsers(), applicationModel.getUsers());
        topMenu.currentPageProperty().bind(applicationModel.currentPageProperty());
        topMenu.totalPagesProperty().bind(applicationModel.totalPagesProperty());
        topMenu.selectedGamesCountProperty().bind(Bindings.size(applicationModel.getSelectedGames()));
    }

    public void selectUser(DataEvent<User> event) {
        User selectedUser = event.getValue();
        applicationModel.setCurrentPage(0);
        eventDispatcher.trigger(ViewEvent.SELECT_USER, selectedUser.getUsername());
    }

    public void changePage(DataEvent<Integer> event) {
        int page = event.getValue();
        if (page >= 1 && page <= topMenu.getTotalPages()) {
            applicationModel.setCurrentPage(page);
        }
    }

    public void importPgn(Event event) {
        eventDispatcher.trigger(ViewEvent.SHOW_IMPORT_FILE_POPUP, FileEvent.IMPORT_PGN);
    }

    public void clearSelectedGames(Event event) {
        applicationModel.getSelectedGames().clear();
    }

    public void manageUsers(Event event) {
        eventDispatcher.trigger(ViewEvent.SHOW_MANAGE_USERS_POPUP);
    }

}
