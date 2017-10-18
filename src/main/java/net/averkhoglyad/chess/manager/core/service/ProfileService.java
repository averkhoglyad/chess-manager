package net.averkhoglyad.chess.manager.core.service;

import net.averkhoglyad.chess.manager.core.sdk.data.User;

import java.util.List;

public interface ProfileService {

    List<User> load();
    void save(List<User> profiles);

}
