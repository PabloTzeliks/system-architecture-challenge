package senai.centroweg.application.account.command;

import java.util.UUID;

public record NewAccountCommand(
        UUID userId
){ }
