package net.averkhoglyad.chess.manager.gui.component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import net.averkhoglyad.chess.manager.core.sdk.data.User;
import net.averkhoglyad.chess.manager.gui.event.DataEvent;
import net.averkhoglyad.chess.manager.gui.view.AlertHelper;

import java.util.Comparator;
import java.util.List;

public class TopMenu extends BaseComponent {

    private static final EventType<DataEvent<Integer>> CHANGE_PAGE = new EventType<>("changePage");
    private static final EventType<DataEvent<User>> SELECT_USER = new EventType<>("selectUser");
    private static final EventType<Event> IMPORT_PGN = new EventType<>("importPgn");
    private static final EventType<Event> CLEAR_SELECTED_GAMES = new EventType<>("clearSelectedGames");
    private static final EventType<Event> MANAGE_USERS_CLICK = new EventType<>("manageUsersClick");

    // Properties
    private SimpleIntegerProperty currentPage = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty totalPages = new SimpleIntegerProperty(0);
    private SimpleIntegerProperty selectedGamesCount = new SimpleIntegerProperty(0);
    private ListProperty<User> users = new SimpleListProperty<>(FXCollections.observableArrayList());

    // Event Handlers
    private ObjectProperty<EventHandler<DataEvent<Integer>>> onChangePage = createHandler(CHANGE_PAGE);
    private ObjectProperty<EventHandler<DataEvent<User>>> onSelectUser = createHandler(SELECT_USER);
    private ObjectProperty<EventHandler<Event>> onImportPgn = createHandler(IMPORT_PGN);
    private ObjectProperty<EventHandler<Event>> onClearSelectedGames = createHandler(CLEAR_SELECTED_GAMES);
    private ObjectProperty<EventHandler<Event>> onManageUsersClick = createHandler(MANAGE_USERS_CLICK);

    // Nodes
    @FXML
    public MenuItem noUsersMenuItem;
    @FXML
    private MenuButton usersMenuButton;

    public TopMenu() {
        super("net/averkhoglyad/chess/manager/gui/component/TopMenu.fxml");
    }

    public void initialize() {
        fitContentToComponentSize();
        noUsersMenuItem.visibleProperty().bind(Bindings.isEmpty(users));
        renderUserItems(users);
        users.addListener((ListChangeListener<? super User>) c -> renderUserItems(c.getList()));
    }

    private void renderUserItems(List<? extends User> users) {
        usersMenuButton.getItems().setAll(noUsersMenuItem);
        users.stream()
            .sorted(Comparator.comparing(User::getUsername))
            .map(this::createUserMenuItem)
            .forEach(usersMenuButton.getItems()::add);
    }

    private MenuItem createUserMenuItem(User user) {
        MenuItem item = new MenuItem();
        item.setText(user.getUsername());
        item.setUserData(user);
        item.setOnAction(this::selectUser);
        return item;
    }

    // Event Handlers
    public void selectUser(ActionEvent event) {
        MenuItem menuItem = MenuItem.class.cast(event.getSource());
        User selectedUser = User.class.cast(menuItem.getUserData());
        fireEvent(new DataEvent(SELECT_USER, selectedUser));
    }

    public void showAboutPopup(ActionEvent event) {
        AlertHelper.info("About", "Free tool to download games from lichess.com as single PGN file.\n\nCurrent version: 0.1-ALFA\nFeedback email: awer.doc@gmail.com");
    }

    public void showResourcesPopup(ActionEvent event) {
        AlertHelper.info("Used resources and technologies", "Platform: JavaFX 8.0\nCompiler: ExcelsiorJet 12.0\nComponents: ControlsFX\nIcons and Images: FontAwesome, Veryicon");
    }

    public void toPrevPage(ActionEvent event) {
        fireEvent(new DataEvent(CHANGE_PAGE, currentPage.get() - 1));
    }

    public void toNextPage(ActionEvent event) {
        fireEvent(new DataEvent(CHANGE_PAGE, currentPage.get() + 1));
    }

    public void importPgn(ActionEvent event) {
        fireEvent(new Event(IMPORT_PGN));
    }

    public void clearSelectedGames(ActionEvent event) {
        fireEvent(new Event(CLEAR_SELECTED_GAMES));
    }

    public void manageUsers(ActionEvent event) {
        fireEvent(new Event(MANAGE_USERS_CLICK));
    }

    // Properties
    public int getCurrentPage() {
        return currentPage.get();
    }
    public SimpleIntegerProperty currentPageProperty() {
        return currentPage;
    }
    public void setCurrentPage(int currentPage) {
        this.currentPage.set(currentPage);
    }

    public int getTotalPages() {
        return totalPages.get();
    }
    public SimpleIntegerProperty totalPagesProperty() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages.set(totalPages);
    }

    public int getSelectedGamesCount() {
        return selectedGamesCount.get();
    }
    public SimpleIntegerProperty selectedGamesCountProperty() {
        return selectedGamesCount;
    }
    public void setSelectedGamesCount(int selectedGamesCount) {
        this.selectedGamesCount.set(selectedGamesCount);
    }

    public ObservableList<User> getUsers() {
        return users.get();
    }
    public ListProperty<User> usersProperty() {
        return users;
    }
    public void setUsers(ObservableList<User> users) {
        this.users.set(users);
    }

    // Events
    public EventHandler<DataEvent<Integer>> getOnChangePage() {
        return onChangePage.get();
    }
    public ObjectProperty<EventHandler<DataEvent<Integer>>> onChangePageProperty() {
        return onChangePage;
    }
    public void setOnChangePage(EventHandler<DataEvent<Integer>> onChangePage) {
        this.onChangePage.set(onChangePage);
    }

    public EventHandler<DataEvent<User>> getOnSelectUser() {
        return onSelectUser.get();
    }
    public ObjectProperty<EventHandler<DataEvent<User>>> onSelectUserProperty() {
        return onSelectUser;
    }
    public void setOnSelectUser(EventHandler<DataEvent<User>> onSelectUser) {
        this.onSelectUser.set(onSelectUser);
    }

    public EventHandler<Event> getOnImportPgn() {
        return onImportPgn.get();
    }
    public ObjectProperty<EventHandler<Event>> onImportPgnProperty() {
        return onImportPgn;
    }
    public void setOnImportPgn(EventHandler<Event> onImportPgn) {
        this.onImportPgn.set(onImportPgn);
    }

    public EventHandler<Event> getOnClearSelectedGames() {
        return onClearSelectedGames.get();
    }
    public ObjectProperty<EventHandler<Event>> onClearSelectedGamesProperty() {
        return onClearSelectedGames;
    }
    public void setOnClearSelectedGames(EventHandler<Event> onClearSelectedGames) {
        this.onClearSelectedGames.set(onClearSelectedGames);
    }

    public EventHandler<Event> getOnManageUsersClick() {
        return onManageUsersClick.get();
    }
    public ObjectProperty<EventHandler<Event>> onManageUsersClickProperty() {
        return onManageUsersClick;
    }
    public void setOnManageUsersClick(EventHandler<Event> onManageUsersClick) {
        this.onManageUsersClick.set(onManageUsersClick);
    }

}
