DROP DATABASE IF EXISTS soundcloud;
CREATE DATABASE soundcloud;
USE soundcloud;

CREATE TABLE users(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    age INT NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT now()
);

CREATE TABLE songs(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL UNIQUE,
    views INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT now(),
    url VARCHAR(100) NOT NULL,
    owner_id INT NOT NULL,
    FOREIGN KEY(owner_id) REFERENCES users(id)
    ON DELETE CASCADE
);

CREATE TABLE playlists(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(50) NOT NULL UNIQUE,
    owner_id INT NOT NULL,
    FOREIGN KEY(owner_id) REFERENCES users(id)
    ON DELETE CASCADE,
    created_at DATETIME NOT NULL DEFAULT now()
    
);

CREATE TABLE comments(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    text TEXT NOT NULL,
    owner_id INT NOT NULL,
    FOREIGN KEY(owner_id) REFERENCES users(id)
    ON DELETE CASCADE,
    song_id INT NOT NULL,
    FOREIGN KEY(song_id) REFERENCES songs(id)
    ON DELETE CASCADE,
    created_at DATETIME NOT NULL DEFAULT now()
);

CREATE TABLE token_verification(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
    ON DELETE CASCADE,
    created_at DATETIME NOT NULL DEFAULT now(),
    expires_at DATETIME NOT NULL,
    confirmed_at DATETIME
);

CREATE TABLE users_like_songs(
	song_id INT NOT NULL,
    FOREIGN KEY(song_id) REFERENCES songs(id)
    ON DELETE CASCADE,
    user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
    ON DELETE CASCADE,
    PRIMARY KEY(song_id, user_id)
);

CREATE TABLE users_dislike_songs(
	song_id INT NOT NULL,
    FOREIGN KEY(song_id) REFERENCES songs(id)
    ON DELETE CASCADE,
    user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
    ON DELETE CASCADE,
    PRIMARY KEY(song_id, user_id)
);

CREATE TABLE users_like_comments(
	comment_id INT NOT NULL,
    FOREIGN KEY(comment_id) REFERENCES comments(id)
    ON DELETE CASCADE,
    user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
    ON DELETE CASCADE,
    PRIMARY KEY(comment_id, user_id)
);

CREATE TABLE users_dislike_comments(
	comment_id INT NOT NULL,
    FOREIGN KEY(comment_id) REFERENCES comments(id)
    ON DELETE CASCADE,
    user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
    ON DELETE CASCADE,
    PRIMARY KEY(comment_id, user_id)
);

CREATE TABLE playlists_have_songs(
	playlist_id INT NOT NULL,
    FOREIGN KEY(playlist_id) REFERENCES playlists(id)
    ON DELETE CASCADE,
    song_id INT NOT NULL,
    FOREIGN KEY(song_id) REFERENCES songs(id)
    ON DELETE CASCADE,
    PRIMARY KEY(playlist_id, song_id)
);

CREATE TABLE users_follow_users(
	followed_id INT NOT NULL,
    FOREIGN KEY(followed_id) REFERENCES users(id)
    ON DELETE CASCADE,
    follower_id INT NOT NULL,
    FOREIGN KEY(follower_id) REFERENCES users(id)
    ON DELETE CASCADE,
    PRIMARY KEY(followed_id, follower_id)
);