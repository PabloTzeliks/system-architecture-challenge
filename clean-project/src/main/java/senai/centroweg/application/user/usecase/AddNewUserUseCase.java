package senai.centroweg.application.user.usecase;

import senai.centroweg.application.user.command.NewUserCommand;
import senai.centroweg.application.user.manager.UserManager;
import senai.centroweg.domain.user.model.User;
import senai.centroweg.domain.user.ports.UserRepositoryPort;

public class AddNewUserUseCase {

    UserRepositoryPort userRepository;
    UserManager userManager;

    public User execute(NewUserCommand userCommand) {

        User user = new User(userCommand.username());

        userManager.execute(() -> {
            userRepository.save(user);
        });

        return user;
    }
}
