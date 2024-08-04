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