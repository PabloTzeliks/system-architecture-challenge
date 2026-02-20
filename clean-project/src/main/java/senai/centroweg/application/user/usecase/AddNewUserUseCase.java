package senai.centroweg.application.user.usecase;

import senai.centroweg.application.user.command.NewUserCommand;
import senai.centroweg.domain.user.model.User;
import senai.centroweg.domain.user.ports.UserRepositoryPort;

public class AddNewUserUseCase {

    private final UserRepositoryPort userRepository;

    public AddNewUserUseCase(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(NewUserCommand userCommand) {

        User user = new User(userCommand.username());

        return userRepository.save(user);
    }
}
