package senai.centroweg.domain.user.ports;

import senai.centroweg.domain.user.model.User;

import java.util.UUID;

public interface UserRepositoryPort {

    User create(User user);

    User findById(UUID id);
}
