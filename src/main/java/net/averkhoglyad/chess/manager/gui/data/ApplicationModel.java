package net.averkhoglyad.chess.manager.gui.data;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;
import net.averkhoglyad.chess.manager.core.sdk.data.User;

public class ApplicationModel {

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableSet<Game> selectedGames = FXCollections.observableSet();
    private IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private IntegerProperty totalPages = new SimpleIntegerProperty(0);

    public ObservableList<User> getUsers() {
        return users;
    }
    public void setUsers(ObservableList<User> users) {
        this.users = users;
    }

    public ObservableSet<Game> getSelectedGames() {
        return selectedGames;
    }
    public void setSelectedGames(ObservableSet<Game> selectedGames) {
        this.selectedGames = selectedGames;
    }

    public int getCurrentPage() {
        return currentPage.get();
    }
    public IntegerProperty currentPageProperty() {
        return currentPage;
    }
    public void setCurrentPage(int currentPage) {
        this.currentPage.set(currentPage);
    }

    public int getTotalPages() {
        return totalPages.get();
    }
    public IntegerProperty totalPagesProperty() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages.set(totalPages);
    }

    // Singleton implementation
    public static final ApplicationModel getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        static ApplicationModel instance = new ApplicationModel();
    }

}
