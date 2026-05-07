ALTER TABLE page_entity
    DROP COLUMN views;

ALTER TABLE page_view_entity
    ADD COLUMN user_agent VARCHAR(512);

ALTER TABLE page_view_entity
    ADD COLUMN referer VARCHAR(512);

CREATE INDEX idx_page_view_page_id ON page_view_entity (page_id);
CREATE INDEX idx_comment_page_id_created_at ON comment_entity (page_id, created_at DESC);
CREATE INDEX idx_link_page_id ON link_entity (page_id);
