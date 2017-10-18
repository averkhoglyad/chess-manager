package net.averkhoglyad.chess.manager.gui.view;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileChooserDialog {

    private final Stage stage;
    private final FileChooser fileChooser;

    public FileChooserDialog(Stage stage, FileChooser fileChooser) {
        this.stage = stage;
        this.fileChooser = fileChooser;
    }

    public Optional<Path> showSaveDialog() {
        File file = fileChooser.showSaveDialog(stage);
        return Optional.ofNullable(file)
            .map(File::toPath);
    }

    public Optional<Path> showOpenDialog() {
        File file = fileChooser.showOpenDialog(stage);
        return Optional.ofNullable(file)
            .map(File::toPath);
    }

    public List<Path> showOpenMultipleDialog() {
        List<File> file = fileChooser.showOpenMultipleDialog(stage);
        if(file == null) return Collections.emptyList();
        return file.stream()
            .map(File::toPath)
            .collect(Collectors.toList());
    }

}
