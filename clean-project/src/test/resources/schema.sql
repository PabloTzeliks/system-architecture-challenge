CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    sender_account_id UUID NOT NULL,
    receiver_account_id UUID NOT NULL,
    raw_amount DECIMAL(19,4) NOT NULL,
    fee_amount DECIMAL(19,4) NOT NULL,
    total_amount DECIMAL(19,4) NOT NULL,
    type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    confirmation_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS entries (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    transaction_id UUID NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    created_at TIMESTAMP NOT NULL,

    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);