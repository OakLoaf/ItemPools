CREATE TABLE IF NOT EXISTS pool_data
(
    id VARCHAR(50) NOT NUlL PRIMARY KEY,
    goals JSON NOT NULL,
    completed BOOL
);