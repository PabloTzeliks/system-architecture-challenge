package senai.centroweg.infrastructure.persistence.repository;

import senai.centroweg.domain.user.model.User;
import senai.centroweg.domain.user.ports.UserRepositoryPort;

import java.util.UUID;

public class UserRepositoryAdapter implements UserRepositoryPort {

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User findById(UUID id) {
        return null;
    }
}
