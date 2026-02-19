package senai.centroweg.application.transaction.manager;

@FunctionalInterface
public interface TransactionManager {
    void execute(Runnable action);
}
