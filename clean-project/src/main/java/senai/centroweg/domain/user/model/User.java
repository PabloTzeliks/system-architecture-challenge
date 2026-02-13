package senai.centroweg.domain.user.model;

import java.util.UUID;

public class User {

    private final UUID id;
    private String username;

    public User(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    public User(String username) {
        this.id = UUID.randomUUID();
        this.username = username;
    }
}
