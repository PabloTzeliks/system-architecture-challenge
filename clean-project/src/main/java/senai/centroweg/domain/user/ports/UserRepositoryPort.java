package senai.centroweg.domain.user.ports;

import senai.centroweg.domain.user.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UUID id);
}
