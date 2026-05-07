CREATE TABLE comments
(
    id         UUID                        NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    content    TEXT                        NOT NULL,
    page_id    UUID,
    author_id  UUID,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

CREATE TABLE likes
(
    id         UUID                        NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    comment_id UUID,
    user_id    UUID,
    CONSTRAINT pk_likes PRIMARY KEY (id)
);

CREATE TABLE links
(
    id             UUID                        NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name           VARCHAR(255)                NOT NULL,
    href           VARCHAR(255)                NOT NULL,
    icon           VARCHAR(255),
    icon_color_hex VARCHAR(255)                NOT NULL,
    page_id        UUID,
    CONSTRAINT pk_links PRIMARY KEY (id)
);

CREATE TABLE page_views
(
    id         UUID                        NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    ip_address VARCHAR(64)                 NOT NULL,
    user_agent VARCHAR(512),
    referer    VARCHAR(512),
    page_id    UUID,
    CONSTRAINT pk_page_views PRIMARY KEY (id)
);

CREATE TABLE pages
(
    id                   UUID                        NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id              UUID,
    background_color_hex VARCHAR(255)                NOT NULL,
    text_color_hex       VARCHAR(255)                NOT NULL,
    card_color_hex       VARCHAR(255)                NOT NULL,
    icon_color_hex       VARCHAR(255)                NOT NULL,
    show_comments        BOOLEAN                     NOT NULL,
    CONSTRAINT pk_pages PRIMARY KEY (id)
);

CREATE TABLE users
(
    id            UUID                        NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    username      VARCHAR(255)                NOT NULL,
    password_hash VARCHAR(255)                NOT NULL,
    page_id       UUID,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE page_views
    ADD CONSTRAINT uc_9492fe9c36821975d0f0745e4 UNIQUE (ip_address, page_id);

ALTER TABLE pages
    ADD CONSTRAINT uc_pages_user UNIQUE (user_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_page UNIQUE (page_id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_PAGE FOREIGN KEY (page_id) REFERENCES pages (id);

ALTER TABLE likes
    ADD CONSTRAINT FK_LIKES_ON_COMMENT FOREIGN KEY (comment_id) REFERENCES comments (id);

ALTER TABLE likes
    ADD CONSTRAINT FK_LIKES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE links
    ADD CONSTRAINT FK_LINKS_ON_PAGE FOREIGN KEY (page_id) REFERENCES pages (id);

ALTER TABLE pages
    ADD CONSTRAINT FK_PAGES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE page_views
    ADD CONSTRAINT FK_PAGE_VIEWS_ON_PAGE FOREIGN KEY (page_id) REFERENCES pages (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_PAGE FOREIGN KEY (page_id) REFERENCES pages (id);