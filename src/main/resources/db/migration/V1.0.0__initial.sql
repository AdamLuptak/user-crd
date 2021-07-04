CREATE TABLE SUSERS
(
    id   INTEGER      NOT NULL,
    guid VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY name (name)
)


