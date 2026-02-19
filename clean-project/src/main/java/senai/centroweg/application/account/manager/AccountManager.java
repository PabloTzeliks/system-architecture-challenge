package senai.centroweg.application.account.manager;

@FunctionalInterface
public interface AccountManager {
    void execute(Runnable action);
}
