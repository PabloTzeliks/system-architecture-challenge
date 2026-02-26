package senai.centroweg.application.user.manager;

@FunctionalInterface
public interface UserManager {
    void execute(Runnable action);
}
