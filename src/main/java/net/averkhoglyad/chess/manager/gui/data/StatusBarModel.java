package net.averkhoglyad.chess.manager.gui.data;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StatusBarModel {

    private final StringProperty statusText = new SimpleStringProperty();
    private final DoubleProperty statusProgress = new SimpleDoubleProperty();

    private StatusBarModel() {
    }

    public String getStatusText() {
        return statusText.get();
    }
    public StringProperty statusTextProperty() {
        return statusText;
    }
    public void setStatusText(String statusText) {
        this.statusText.set(statusText);
    }

    public double getStatusProgress() {
        return statusProgress.get();
    }
    public DoubleProperty statusProgressProperty() {
        return statusProgress;
    }
    public void setStatusProgress(double statusProgress) {
        this.statusProgress.set(statusProgress);
    }

    // Singleton implementation
    public static final StatusBarModel getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        static StatusBarModel instance = new StatusBarModel();
    }

}
