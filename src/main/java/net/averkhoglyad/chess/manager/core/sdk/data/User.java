package net.averkhoglyad.chess.manager.core.sdk.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String username;
    private String title;
    private String url;
    private boolean online;
    private String playing;
    private boolean engine;
    private String language;
    private Profile profile;
    private Map<String, Stats> performances;

    public User(String username) {
        this.username = username;
    }
}
