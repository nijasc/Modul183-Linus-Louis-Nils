CREATE TABLE user_entity
(
    id            UUID                        NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name          VARCHAR(255)                NOT NULL,
    password_hash VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_userentity PRIMARY KEY (id)
);