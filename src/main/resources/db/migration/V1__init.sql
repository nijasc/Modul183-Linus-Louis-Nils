CREATE TABLE comment_entity
(
    id         UUID                        NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    content    TEXT                        NOT NULL,
    page_id    UUID,
    author_id  UUID,
    CONSTRAINT pk_commententity PRIMARY KEY (id)
);

CREATE TABLE like_entity
(
    id         UUID                        NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    comment_id UUID,
    user_id    UUID,
    CONSTRAINT pk_likeentity PRIMARY KEY (id)
);

CREATE TABLE link_entity
(
    id         UUID                        NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name       VARCHAR(255)                NOT NULL,
    href       VARCHAR(255)                NOT NULL,
    icon       VARCHAR(255),
    icon_color VARCHAR(255)                NOT NULL,
    page_id    UUID,
    CONSTRAINT pk_linkentity PRIMARY KEY (id)
);

CREATE TABLE page_entity
(
    id               UUID                        NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    views            INTEGER                     NOT NULL,
    user_id          UUID,
    background_color VARCHAR(255)                NOT NULL,
    text_color       VARCHAR(255)                NOT NULL,
    card_color       VARCHAR(255)                NOT NULL,
    icon_color       VARCHAR(255)                NOT NULL,
    show_comments    BOOLEAN                     NOT NULL,
    CONSTRAINT pk_pageentity PRIMARY KEY (id)
);

CREATE TABLE page_view_entity
(
    id         UUID                        NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    ip_address VARCHAR(255)                NOT NULL,
    page_id    UUID,
    CONSTRAINT pk_pageviewentity PRIMARY KEY (id)
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

ALTER TABLE page_view_entity
    ADD CONSTRAINT uc_0728857739c158d9e3f27a92b UNIQUE (ip_address, page_id);

ALTER TABLE page_entity
    ADD CONSTRAINT uc_pageentity_user UNIQUE (user_id);

ALTER TABLE user_entity
    ADD CONSTRAINT uc_userentity_page UNIQUE (page_id);

ALTER TABLE comment_entity
    ADD CONSTRAINT FK_COMMENTENTITY_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES user_entity (id);

ALTER TABLE comment_entity
    ADD CONSTRAINT FK_COMMENTENTITY_ON_PAGE FOREIGN KEY (page_id) REFERENCES page_entity (id);

ALTER TABLE like_entity
    ADD CONSTRAINT FK_LIKEENTITY_ON_COMMENT FOREIGN KEY (comment_id) REFERENCES comment_entity (id);

ALTER TABLE like_entity
    ADD CONSTRAINT FK_LIKEENTITY_ON_USER FOREIGN KEY (user_id) REFERENCES user_entity (id);

ALTER TABLE link_entity
    ADD CONSTRAINT FK_LINKENTITY_ON_PAGE FOREIGN KEY (page_id) REFERENCES page_entity (id);

ALTER TABLE page_entity
    ADD CONSTRAINT FK_PAGEENTITY_ON_USER FOREIGN KEY (user_id) REFERENCES user_entity (id);

ALTER TABLE page_view_entity
    ADD CONSTRAINT FK_PAGEVIEWENTITY_ON_PAGE FOREIGN KEY (page_id) REFERENCES page_entity (id);

ALTER TABLE user_entity
    ADD CONSTRAINT FK_USERENTITY_ON_PAGE FOREIGN KEY (page_id) REFERENCES page_entity (id);