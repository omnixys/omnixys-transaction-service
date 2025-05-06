
CREATE TABLE transaction
(
    id BINARY (16) NOT NULL PRIMARY KEY,
    sender BINARY (16),
    receiver BINARY (16),
    amount  DECIMAL(10, 2) NOT NULL,
    created TIMESTAMP
);
