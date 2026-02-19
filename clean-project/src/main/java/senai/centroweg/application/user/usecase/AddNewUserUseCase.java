package senai.centroweg.application.user.usecase;

import senai.centroweg.application.user.command.NewUserCommand;
import senai.centroweg.application.user.manager.UserManager;
import senai.centroweg.domain.user.model.User;
import senai.centroweg.domain.user.ports.UserRepositoryPort;

public class AddNewUserUseCase {

    private final UserRepositoryPort userRepository;
    private final UserManager userManager;

    public AddNewUserUseCase(UserRepositoryPort userRepository,
                             UserManager userManager) {

        this.userRepository = userRepository;
        this.userManager = userManager;
    }

    public User execute(NewUserCommand userCommand) {

        User user = new User(userCommand.username());

        userManager.execute(() -> {
            userRepository.save(user);
        });

        return user;
    }
}
