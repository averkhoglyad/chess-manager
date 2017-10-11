package net.averkhoglyad.chess.manager.gui.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import net.averkhoglyad.chess.manager.core.sdk.data.Game;
import net.averkhoglyad.chess.manager.core.sdk.data.User;

public class ApplicationModel {

    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableSet<Game> selectedGames = FXCollections.observableSet();

    public ObservableList<User> getUsers() {
        return users;
    }
    public void setUsers(ObservableList<User> users) {
        this.users = users;
    }

//    public ObservableSet<Game> getSelectedGames() {
//        return selectedGames;
//    }
//    public void setSelectedGames(ObservableSet<Game> selectedGames) {
//        this.selectedGames = selectedGames;
//    }

    // Singleton implementation
    public static final ApplicationModel getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        static ApplicationModel instance = new ApplicationModel();
    }

}
