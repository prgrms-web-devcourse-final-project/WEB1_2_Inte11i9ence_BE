# TABLE LISTS

USE skypedia;

CREATE TABLE IF NOT EXISTS member
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    oauth_id      VARCHAR(255)       NOT NULL,
    username      VARCHAR(20) UNIQUE NOT NULL,
    email         VARCHAR(50)        NOT NULL,
    role          VARCHAR(20)        NOT NULL DEFAULT 'ROLE_USER',
    profile_image VARCHAR(255)       NULL,
    withdrawn     TINYINT(1)         NOT NULL DEFAULT '0',
    created_at    TIMESTAMP                   DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP                   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    withdrawn_at  TIMESTAMP          NULL,
    CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'))
);

CREATE TABLE IF NOT EXISTS notify
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT       NULL,
    content     VARCHAR(255) NOT NULL,
    notify_type VARCHAR(20)  NOT NULL,
    uri         VARCHAR(255) NOT NULL,
    sent_at     TIMESTAMP    NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (notify_type IN ('REPLY', 'CHAT', 'NOTICE')),
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE IF NOT EXISTS photo
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid              VARCHAR(255)                        NOT NULL UNIQUE,
    original_filename VARCHAR(255)                        NOT NULL,
    content_type      VARCHAR(255)                        NULL,
    s3_filekey        VARCHAR(255)                        NOT NULL UNIQUE,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS post_category
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) UNIQUE                 NOT NULL,
    description VARCHAR(1000)                       NOT NULL DEFAULT '',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS post
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT        NOT NULL,
    category_id BIGINT        NOT NULL,
    title       VARCHAR(255)  NOT NULL,
    content     VARCHAR(2000) NOT NULL DEFAULT '',
    hashtags    VARCHAR(255)  NULL,
    views       BIGINT        NOT NULL DEFAULT '0',
    likes       BIGINT        NOT NULL DEFAULT '0',
    rating      DECIMAL(3, 2) NULL CHECK (rating BETWEEN 0.00 AND 5.00),
    deleted     TINYINT(1)    NOT NULL DEFAULT '0',
    deleted_at  TIMESTAMP     NULL,
    created_at  TIMESTAMP              DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (category_id) REFERENCES post_category (id),
    FULLTEXT INDEX f_idx_post_title (title),
    FULLTEXT INDEX f_idx_post_hashtags (hashtags)
);

CREATE TABLE IF NOT EXISTS region
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS plan_group
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT       NOT NULL,
    region_id   BIGINT       NOT NULL,
    title       VARCHAR(20)  NOT NULL,
    group_image VARCHAR(255) NOT NULL,
    views       BIGINT       NOT NULL DEFAULT '0',
    likes       BIGINT       NOT NULL DEFAULT '0',
    deleted     TINYINT(1)   NOT NULL DEFAULT '0',
    created_at  TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP    NULL,
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (region_id) REFERENCES region (id)
);

CREATE TABLE IF NOT EXISTS plan_detail
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_group_id BIGINT       NOT NULL,
    location      VARCHAR(255) NOT NULL,
    content       VARCHAR(255) NULL,
    latitude      DOUBLE       NOT NULL,
    longitude     DOUBLE       NOT NULL,
    views         BIGINT       NOT NULL DEFAULT '0',
    likes         BIGINT       NOT NULL DEFAULT '0',
    deleted       TINYINT(1)   NOT NULL DEFAULT '0',
    created_at    TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP    NULL,
    FOREIGN KEY (plan_group_id) REFERENCES plan_group (id),
    INDEX idx_plan_detail_location (latitude, longitude)
);

CREATE TABLE IF NOT EXISTS reply
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id       BIGINT        NOT NULL,
    parent_reply_id BIGINT        NULL,
    content         VARCHAR(1000) NOT NULL DEFAULT '',
    likes           BIGINT        NOT NULL DEFAULT '0',
    deleted         TINYINT(1)    NOT NULL DEFAULT '0',
    deleted_at      TIMESTAMP     NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (parent_reply_id) REFERENCES reply (id) ON DELETE SET NULL
);

# JUNCTION TABLE LISTS

CREATE TABLE IF NOT EXISTS post_reply
(
    post_id    BIGINT NOT NULL,
    reply_id   BIGINT NOT NULL,
    replied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, reply_id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (reply_id) REFERENCES reply (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_scrap
(
    post_id    BIGINT NOT NULL,
    member_id  BIGINT NOT NULL,
    scraped_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, member_id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_likes
(
    post_id   BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    liked_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, member_id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reply_likes
(
    reply_id  BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    liked_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (reply_id, member_id),
    FOREIGN KEY (reply_id) REFERENCES reply (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS plan_group_reply
(
    plan_group_id BIGINT NOT NULL,
    reply_id      BIGINT NOT NULL,
    replied_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (plan_group_id, reply_id),
    FOREIGN KEY (plan_group_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (reply_id) REFERENCES reply (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS plan_group_scrap
(
    plan_group_id BIGINT NOT NULL,
    member_id     BIGINT NOT NULL,
    scraped_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (plan_group_id, member_id),
    FOREIGN KEY (plan_group_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS plan_group_likes
(
    plan_group_id BIGINT NOT NULL,
    member_id     BIGINT NOT NULL,
    liked_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (plan_group_id, member_id),
    FOREIGN KEY (plan_group_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);
