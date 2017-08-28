/* WORK IN PROGRESS */

CREATE TABLE IF NOT EXISTS libraries (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name VARCHAR(256) UNIQUE NOT NULL,
	librarytype SMALLINT NOT NULL,
	rootDir VARCHAR(2048) NOT NULL
);

CREATE TABLE IF NOT EXISTS thumbnails (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	imgData MEDIUMBLOB,
	source VARCHAR(2048)
);

CREATE TABLE IF NOT EXISTS files (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	libraryid INTEGER NOT NULL,
	path VARCHAR(2048) UNIQUE NOT NULL,
	size BIGINT NOT NULL,
	FOREIGN KEY(libraryid) REFERENCES libraries(id)
);

CREATE TABLE IF NOT EXISTS images (
	fileid INTEGER PRIMARY KEY,
	width INT NOT NULL,
	height INT NOT NULL,
	thumbnail INT,
	FOREIGN KEY(fileid) REFERENCES files(id),
	FOREIGN KEY(thumbnail) REFERENCES thumbnails(id)
);


CREATE TABLE IF NOT EXISTS audios (
	fileid INTEGER PRIMARY KEY,
	duration BIGINT NOT NULL,
	bitrate INT NOT NULL,
	FOREIGN KEY(fileid) REFERENCES files(id)
);

CREATE TABLE IF NOT EXISTS videos (
	fileid INTEGER PRIMARY KEY,
	duration BIGINT NOT NULL,
	width INT NOT NULL,
	height INT NOT NULL,
	framerate FLOAT NOT NULL,
	extractedAudioFile INT,
	FOREIGN KEY(fileid) REFERENCES files(id),
	FOREIGN KEY(extractedAudioFile) REFERENCES audios(fileid)
);

CREATE TABLE IF NOT EXISTS interpreters (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name VARCHAR(256) NOT NULL,
	thumbnail INT,
	FOREIGN KEY(thumbnail) REFERENCES thumbnails(id)
);

CREATE TABLE IF NOT EXISTS albums (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name VARCHAR(256) NOT NULL,
	interpret VARCHAR(256) NOT NULL,
	thumbnail INT,
	FOREIGN KEY(interpret) REFERENCES interprets(name),
	FOREIGN KEY(thumbnail) REFERENCES thumbnails(id)
);

CREATE TABLE IF NOT EXISTS songs (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name VARCHAR(256) NOT NULL,
	album VARCHAR(256) NOT NULL,
	interpret VARCHAR(256) NOT NULL,
	genre VARCHAR(256),
	file INT NOT NULL,
	thumbnail INT,
	FOREIGN KEY(album) REFERENCES albums(name),
	FOREIGN KEY(interpret) REFERENCES interprets(name),
	FOREIGN KEY(file) REFERENCES audios(fileid),
	FOREIGN KEY(thumbnail) REFERENCES thumbnails(id)
);

CREATE TABLE IF NOT EXISTS publishers (
	name VARCHAR(256) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS movies (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name VARCHAR(256) NOT NULL,
	regisseur VARCHAR(256) NOT NULL,
	publisher VARCHAR(256) NOT NULL,
	file INT NOT NULL,
	thumbnail INT,
	FOREIGN KEY(publisher) REFERENCES publishers(name),
	FOREIGN KEY(file) REFERENCES videofiles(fileid),
	FOREIGN KEY(thumbnail) REFERENCES thumbnails(id)
);

CREATE TABLE IF NOT EXISTS series (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name VARCHAR(256) NOT NULL,
	thumbnail INT,
	FOREIGN KEY(thumbnail) REFERENCES thumbnails(id)
);

CREATE TABLE IF NOT EXISTS seasons (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	number INT NOT NULL,
	series VARCHAR(256) NOT NULL,
	thumbnail INT,
	FOREIGN KEY(series) REFERENCES series(name),
	FOREIGN KEY(thumbnail) REFERENCES thumbnails(id)
);

CREATE TABLE IF NOT EXISTS episodes (
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	number INT NOT NULL,
	season INT NOT NULL,
	series VARCHAR(256) NOT NULL,
	thumbnail INT,
	FOREIGN KEY(series) REFERENCES series(name),
	FOREIGN KEY(season) REFERENCES season(number),
	FOREIGN KEY(thumbnail) REFERENCES thumbnails(id)
);

CREATE TABLE IF NOT EXISTS user_movie (
	userid INTEGER NOT NULL,
	movieid INTEGER NOT NULL,
	lastWatched DATE NOT NULL,
	pausedAt BIGINT NOT NULL,
	finishedOnce BOOLEAN DEFAULT(0),
	PRIMARY KEY(userid, movieid),
	FOREIGN KEY(userid) REFERENCES users(id),
	FOREIGN KEY(movieid) REFERENCES movies(id)
);

CREATE TABLE IF NOT EXISTS user_episode (
	userid INTEGER NOT NULL,
	episodeid INTEGER NOT NULL,
	lastWatched DATE NOT NULL,
	pausedAt BIGINT NOT NULL,
	finishedOnce BOOLEAN NOT NULL DEFAULT(0),
	PRIMARY KEY(userid, episodeid),
	FOREIGN KEY(userid) REFERENCES users(id),
	FOREIGN KEY(episodeid) REFERENCES episodes(id)
);