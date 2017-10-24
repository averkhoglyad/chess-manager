package net.averkhoglyad.chess.manager.gui.component;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import net.averkhoglyad.chess.manager.core.helper.StringHelper;
import net.averkhoglyad.chess.manager.core.sdk.data.User;
import net.averkhoglyad.chess.manager.gui.event.DataEvent;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

public class ProfilesManager extends BaseComponent {

    // Events
    private static final EventType<DataEvent<String>> ADD_USER = new EventType<>("addUser");
    private static final EventType<DataEvent<User>> DROP_USER = new EventType<>("dropUser");

    // Properties
    private ListProperty<User> profiles = new SimpleListProperty<>(this, "lichessUsers", FXCollections.observableArrayList());

    // Event Handlers
    private ObjectProperty<EventHandler<DataEvent<String>>> onAddUser = new EventHandlerProperty<>(ADD_USER);
    private ObjectProperty<EventHandler<DataEvent<User>>> onDropUser = new EventHandlerProperty<>(DROP_USER);

    // Nodes
    @FXML
    private TextField profileTextField;
    @FXML
    private ListView<User> usersListView;

    public ProfilesManager() {
        super("net/averkhoglyad/chess/manager/gui/component/ProfilesManager.fxml");
    }

    public void initialize() {
        fitContentToComponentSize();
        usersListView.itemsProperty().bind(profiles);
        usersListView.setCellFactory((ListView<User> view) -> new ListCell<User>() {

            final Label label = new Label();
            final Button button = createButton();

            Button createButton() {
                Button button = new Button(null, new Glyph("FontAwesome", FontAwesome.Glyph.MINUS));
                button.setOnAction(event -> {
                    if (button.getUserData() != null) {
                        fireEvent(new DataEvent<>(DROP_USER, (User) button.getUserData()));
                    }
                });
                return button;
            }

            @Override
            public void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(null);
                if (empty) initEmptyCell();
                else initProfileCell(user);
            }

            private void initEmptyCell() {
                setGraphic(null);
            }

            private void initProfileCell(User user) {
                button.setUserData(user);
                setGraphic(createPane(user));
            }

            private Pane createPane(User user) {
                HBox pane = new HBox();
                label.setText(user.getUsername());
                Pane spacer = new Pane();
                spacer.setPrefWidth(20);
                pane.getChildren().setAll(button, spacer, label);
                return pane;
            }
        });
    }

    public void reset() {
        profileTextField.setText("");
    }

    public void addLichessUser(ActionEvent event) {
        String username = profileTextField.getText();
        if (StringHelper.isEmpty(username)) return;
        fireEvent(new DataEvent<>(ADD_USER, username));
        profileTextField.setText("");
    }

    // Properties
    public ObservableList<User> getProfiles() {
        return profiles.get();
    }
    public ListProperty<User> profilesProperty() {
        return profiles;
    }
    public void setProfiles(ObservableList<User> profiles) {
        this.profiles.set(profiles);
    }

    // Event Handlers
    public EventHandler<DataEvent<String>> getOnAddUser() {
        return onAddUser.get();
    }
    public ObjectProperty<EventHandler<DataEvent<String>>> onAddUserProperty() {
        return onAddUser;
    }
    public void setOnAddUser(EventHandler<DataEvent<String>> onAddUser) {
        this.onAddUser.set(onAddUser);
    }

    public EventHandler<DataEvent<User>> getOnDropUser() {
        return onDropUser.get();
    }
    public ObjectProperty<EventHandler<DataEvent<User>>> onDropUserProperty() {
        return onDropUser;
    }
    public void setOnDropUser(EventHandler<DataEvent<User>> onDropUser) {
        this.onDropUser.set(onDropUser);
    }

}
