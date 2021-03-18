DROP DATABASE IF EXISTS soundcloud;
CREATE DATABASE soundcloud;
USE soundcloud;

CREATE TABLE users(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    age INT NOT NULL,
    city VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    pass VARCHAR(100) NOT NULL,
    register_date DATETIME NOT NULL DEFAULT now()
);

CREATE TABLE songs(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    song_name VARCHAR(100) NOT NULL,
    views INT NOT NULL DEFAULT 0,
    added_at DATETIME NOT NULL DEFAULT now(),
    user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE playlists(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	playlist_name VARCHAR(50) NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    created_at DATETIME NOT NULL DEFAULT now()
);

CREATE TABLE comments(
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    comment_text TEXT NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    song_id INT NOT NULL,
    FOREIGN KEY(song_id) REFERENCES songs(id),
    created_at DATETIME NOT NULL DEFAULT now()
);

CREATE TABLE songs_have_likes(
	song_id INT NOT NULL,
    FOREIGN KEY(song_id) REFERENCES songs(id),
    user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    PRIMARY KEY(song_id, user_id)
);

CREATE TABLE songs_have_dislikes(
	song_id INT NOT NULL,
    FOREIGN KEY(song_id) REFERENCES songs(id),
    user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    PRIMARY KEY(song_id, user_id)
);

CREATE TABLE comments_have_likes(
	comment_id INT NOT NULL,
    FOREIGN KEY(comment_id) REFERENCES comments(id),
    user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    PRIMARY KEY(comment_id, user_id)
);

CREATE TABLE comments_have_dislikes(
	comment_id INT NOT NULL,
    FOREIGN KEY(comment_id) REFERENCES comments(id),
    user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    PRIMARY KEY(comment_id, user_id)
);

CREATE TABLE playlists_have_songs(
	playlist_id INT NOT NULL,
    FOREIGN KEY(playlist_id) REFERENCES playlists(id),
    song_id INT NOT NULL,
    FOREIGN KEY(song_id) REFERENCES songs(id),
    PRIMARY KEY(playlist_id, song_id)
);

CREATE TABLE users_have_followers(
	user_id INT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    following_user_id INT NOT NULL,
    FOREIGN KEY(following_user_id) REFERENCES users(id),
    PRIMARY KEY(user_id, following_user_id)
);

CREATE TABLE songs_have_values(
	song_id INT NOT NULL,
    FOREIGN KEY(song_id) REFERENCES songs(id),
    song_value BLOB NOT NULL
);