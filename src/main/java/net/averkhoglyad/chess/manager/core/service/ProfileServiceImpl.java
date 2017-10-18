package net.averkhoglyad.chess.manager.core.service;

import net.averkhoglyad.chess.manager.core.sdk.data.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.averkhoglyad.chess.manager.core.helper.ExceptionHelper.doStrict;

public class ProfileServiceImpl implements ProfileService {

    private final Path profilesFile;

    public ProfileServiceImpl(Path profilesFile) {
        this.profilesFile = profilesFile;
    }

    @Override
    public List<User> load() {
        List<User> users = new ArrayList<>();
        if (Files.exists(profilesFile)) {
            try (Stream<String> lines = doStrict(() -> Files.lines(profilesFile))) {
                lines.map(User::new).forEachOrdered(users::add);
            }
        }
        return users;
    }

    @Override
    public void save(List<User> profiles) {
        List<String> usernames = profiles.stream()
            .map(User::getUsername)
            .collect(Collectors.toList());
        doStrict(() -> Files.write(profilesFile, usernames));
    }

}
