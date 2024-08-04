drop table if exists role_permission cascade;
drop table if exists permission cascade;
drop table if exists user_role cascade;
drop table if exists role cascade;
drop table if exists user_account cascade;

-- Table to store roles with composite primary key
CREATE TABLE role
(
    name              VARCHAR(50)  NOT NULL,
    description       VARCHAR(256) NOT NULL,
    display_name      VARCHAR(50) DEFAULT NULL,
    CONSTRAINT PK_Role PRIMARY KEY (name)
);

-- Table to store permissions
CREATE TABLE permission
(
    name         VARCHAR(100) NOT NULL,
    description  VARCHAR(256) NOT NULL,
    display_name VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (name)
);

CREATE TABLE user_account
(
    username          VARCHAR(255) PRIMARY KEY,
    full_name         VARCHAR(255) NOT NULL,
    password          VARCHAR(255) NOT NULL,
    email_address     VARCHAR(255) NOT NULL UNIQUE
);

-- Linking table between roles and permissions
CREATE TABLE role_permission
(
    role_name        VARCHAR(50)  NOT NULL,
    permission_name  VARCHAR(100) NOT NULL,
    PRIMARY KEY (role_name, permission_name),
    FOREIGN KEY (role_name) REFERENCES role (name) ON DELETE CASCADE,
    FOREIGN KEY (permission_name) REFERENCES permission (name) ON DELETE CASCADE
);

-- Linking table between users and roles
CREATE TABLE user_role
(
    username  VARCHAR(255) NOT NULL,
    role_name VARCHAR(50)  NOT NULL,
    PRIMARY KEY (username, role_name),
    FOREIGN KEY (username) REFERENCES user_account (username) ON DELETE CASCADE,
    FOREIGN KEY (role_name) REFERENCES role (name) ON DELETE CASCADE
);

CREATE INDEX idx_permission_name ON permission(name);
CREATE INDEX idx_role_permission_role_name ON role_permission(role_name);
CREATE INDEX idx_role_permission_permission_name ON role_permission(permission_name);
CREATE INDEX idx_user_role_role_name ON user_role(role_name);
CREATE INDEX idx_user_role_username ON user_role(username);
CREATE INDEX idx_user_account_username ON user_account(username);

-- Populate permissions
INSERT INTO permission (name, description, display_name)
VALUES ('read', 'Permission to read', 'Read'),
       ('write', 'Permission to write', 'Write'),
       ('delete', 'Permission to delete', 'Delete');

-- Populate roles
INSERT INTO role (name, description, display_name)
VALUES ('administrator', 'Administrator Role', 'Administrator'),
       ('manager', 'Manager Role', 'Manager'),
       ('user', 'User Role', 'User');

INSERT INTO role_permission (role_name, permission_name)
VALUES ('manager', 'read'),
       ('manager', 'write'),
       ('user', 'read'),
       ('administrator', 'read'),
       ('administrator', 'write'),
       ('administrator', 'delete');

INSERT INTO user_account (username, full_name, password, email_address)
VALUES ('userone', 'User One', 'password1', 'userone@example.com'),
       ('usertwo', 'User Two', 'password2', 'usertwo@example.com');

INSERT INTO user_role (username, role_name)
VALUES ('userone', 'administrator'),
       ('usertwo', 'user');
