CREATE TABLE link_entity
(
    id         UUID                        NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    href       VARCHAR(255)                NOT NULL,
    icon       VARCHAR(255),
    page_id    UUID,
    CONSTRAINT pk_linkentity PRIMARY KEY (id)
);

CREATE TABLE page_entity
(
    id         UUID                        NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    views      INTEGER                     NOT NULL,
    user_id    UUID,
    CONSTRAINT pk_pageentity PRIMARY KEY (id)
);

CREATE TABLE user_entity
(
    id            UUID                        NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name          VARCHAR(255)                NOT NULL,
    password_hash VARCHAR(255)                NOT NULL,
    page_id       UUID,
    CONSTRAINT pk_userentity PRIMARY KEY (id)
);

ALTER TABLE page_entity
    ADD CONSTRAINT uc_pageentity_user UNIQUE (user_id);

ALTER TABLE user_entity
    ADD CONSTRAINT uc_userentity_page UNIQUE (page_id);

ALTER TABLE link_entity
    ADD CONSTRAINT FK_LINKENTITY_ON_PAGE FOREIGN KEY (page_id) REFERENCES page_entity (id);

ALTER TABLE page_entity
    ADD CONSTRAINT FK_PAGEENTITY_ON_USER FOREIGN KEY (user_id) REFERENCES user_entity (id);

ALTER TABLE user_entity
    ADD CONSTRAINT FK_USERENTITY_ON_PAGE FOREIGN KEY (page_id) REFERENCES page_entity (id);