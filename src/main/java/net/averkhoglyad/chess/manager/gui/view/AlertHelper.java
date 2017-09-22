package net.averkhoglyad.chess.manager.gui.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.*;

public abstract class AlertHelper {

    private AlertHelper() {
    }

    public static Alert error(Throwable e) {
        return error(e.getMessage(), null, e);
    }

    public static Alert error(String title, Throwable e) {
        return error(title, e.getMessage(), e);
    }

    public static Alert error(String title, String message, Throwable ex) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Unexpected exception");
        alert.setHeaderText(message);
        alert.setContentText(Optional.ofNullable(title).filter(it -> it.length() > 0).orElse(""));

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.setHeight(400);
        alert.setWidth(600);
        alert.showAndWait();
        return alert;
    }

    public static Alert error(String title) {
        return alert(ERROR, title, null, null);
    }

    public static Alert error(String title, String message) {
        return alert(ERROR, title, null, message);
    }

    public static Alert error(String title, String header, String message) {
        return alert(ERROR, title, header, message);
    }

    public static Alert warning(String title) {
        return alert(WARNING, title, null, title);
    }

    public static Alert warning(String title, String message) {
        return alert(WARNING, title, null, message);
    }

    public static Alert warning(String title, String header, String message) {
        return alert(WARNING, title, header, message);
    }

    public static Alert info(String title) {
        return alert(INFORMATION, title, null, null);
    }

    public static Alert info(String title, String message) {
        return alert(INFORMATION, title, null, message);
    }

    public static Alert info(String title, String header, String message) {
        return alert(INFORMATION, title, header, message);
    }

    private static Alert alert(AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
        return alert;
    }

}
