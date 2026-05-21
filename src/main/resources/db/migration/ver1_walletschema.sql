CREATE TABLE wallets (
                         id UUID PRIMARY KEY,
                         user_id VARCHAR(255) UNIQUE NOT NULL,
                         available_balance NUMERIC(19,2) NOT NULL DEFAULT 0,
                         held_balance NUMERIC(19,2) NOT NULL DEFAULT 0,
                         created_at TIMESTAMP,
                         updated_at TIMESTAMP
);

CREATE TABLE wallet_transactions (
                                     id UUID PRIMARY KEY,
                                     wallet_id UUID NOT NULL,
                                     type VARCHAR(50) NOT NULL,
                                     amount NUMERIC(19,2) NOT NULL,
                                     reference_id VARCHAR(255),
                                     created_at TIMESTAMP
);