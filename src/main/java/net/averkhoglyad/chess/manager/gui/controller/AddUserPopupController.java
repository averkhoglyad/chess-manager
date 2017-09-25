package net.averkhoglyad.chess.manager.gui.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import net.averkhoglyad.chess.manager.Main;
import net.averkhoglyad.chess.manager.core.sdk.data.User;
import net.averkhoglyad.chess.manager.gui.data.ApplicationModel;
import net.averkhoglyad.chess.manager.gui.event.ApplicationEventDispatcher;
import net.averkhoglyad.chess.manager.gui.event.ViewEvent;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doStrict;

public class AddUserPopupController {

    @FXML
    private BorderPane pane;
    @FXML
    private TextField profileTextField;
    @FXML
    private ListView<User> usersListView;

    public void initialize() {
        BorderPane.setMargin(pane.getTop(), new Insets(5));
        ApplicationEventDispatcher.getInstance()
            .on(ViewEvent.SHOW_MANAGE_USERS_POPUP, () -> profileTextField.setText(""));
        usersListView.setItems(ApplicationModel.getInstance().getUsers());
        usersListView.setCellFactory((ListView<User> view) -> {
            return new ListCell<User>() {

                final Label label = new Label();
                final Button button = createButton();

                Button createButton() {
                    Button button = new Button(null, new Glyph("FontAwesome", FontAwesome.Glyph.MINUS));
                    button.setOnAction(event -> {
                        if (button.getUserData() != null) {
                            ApplicationModel.getInstance().getUsers().remove(button.getUserData());
                            writeUsers();
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
            };
        });
    }

    public void addLichessUser(ActionEvent event) {
        String username = profileTextField.getText();

        if( username == null || username.isEmpty() )
        {
            return;
        }

        ObservableList<User> users = ApplicationModel.getInstance().getUsers();

        if (!users.stream().anyMatch(it -> it.getUsername().equals(username)))
        {
            users.add(new User(username));
        }

        writeUsers();

        ApplicationModel.getInstance().setCurrentPage(0);
        ApplicationEventDispatcher.getInstance().trigger(ViewEvent.SELECT_USER, username);
        ApplicationModel.getInstance().getSelectedGames().clear();
        ApplicationEventDispatcher.getInstance().trigger(ViewEvent.CLOSE_ADD_USER_POPUP);
    }

    private void writeUsers() {
        List<String> usernames = ApplicationModel.getInstance().getUsers().stream()
            .map(User::getUsername)
            .collect(Collectors.toList());
        doStrict(() -> Files.write(Main.profilesFile, usernames));
    }

}
