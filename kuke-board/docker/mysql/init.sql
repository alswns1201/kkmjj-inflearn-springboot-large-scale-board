-- =============================================
-- article service
-- =============================================
CREATE DATABASE IF NOT EXISTS article CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE article;

CREATE TABLE IF NOT EXISTS article (
    article_id   BIGINT       NOT NULL,
    title        VARCHAR(300) NOT NULL,
    content      TEXT         NOT NULL,
    board_id     BIGINT       NOT NULL,
    writer_id    BIGINT       NOT NULL,
    created_at   DATETIME     NOT NULL,
    modified_at  DATETIME     NOT NULL,
    PRIMARY KEY (article_id)
);

-- =============================================
-- comment service
-- =============================================
CREATE DATABASE IF NOT EXISTS comment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE comment;

CREATE TABLE IF NOT EXISTS comment (
    comment_id        BIGINT        NOT NULL,
    content           VARCHAR(3000) NOT NULL,
    parent_comment_id BIGINT        NOT NULL,
    article_id        BIGINT        NOT NULL,
    writer_id         BIGINT        NOT NULL,
    deleted           TINYINT(1)    NOT NULL DEFAULT 0,
    created_at        DATETIME      NOT NULL,
    PRIMARY KEY (comment_id)
);

-- path: 5 chars per depth × max 5 depth = VARCHAR(25)
CREATE TABLE IF NOT EXISTS comment_v2 (
    comment_id  BIGINT        NOT NULL,
    content     VARCHAR(3000) NOT NULL,
    article_id  BIGINT        NOT NULL,
    writer_id   BIGINT        NOT NULL,
    path        VARCHAR(25)   NOT NULL,
    deleted     TINYINT(1)    NOT NULL DEFAULT 0,
    created_at  DATETIME      NOT NULL,
    PRIMARY KEY (comment_id)
);

-- =============================================
-- like service
-- =============================================
CREATE DATABASE IF NOT EXISTS article_like CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE article_like;

CREATE TABLE IF NOT EXISTS article_like (
    article_liked_id  BIGINT   NOT NULL,
    article_id        BIGINT   NOT NULL,
    user_id           BIGINT   NOT NULL,
    local_date_time   DATETIME NOT NULL,
    PRIMARY KEY (article_liked_id),
    UNIQUE KEY uq_article_user (article_id, user_id)
);


CREATE TABLE IF NOT EXISTS article_like_count (
    article_id  BIGINT   NOT NULL,
    like_count        BIGINT   NOT NULL,
    version BIGINT NOT NULL,
    PRIMARY KEY (article_id)
);

USE article;

CREATE TABLE IF NOT EXISTS board_article_count (
    board_id  BIGINT   NOT NULL,
    article_count  BIGINT   NOT NULL,
    version BIGINT NOT NULL,
    PRIMARY KEY (board_id)
);

USE comment;
CREATE TABLE IF NOT EXISTS article_comment_count (
    article_id  BIGINT   NOT NULL,
    comment_count  BIGINT   NOT NULL,
    version BIGINT NOT NULL,
    PRIMARY KEY (article_id)
);
