/* WORK IN PROGRESS */

CREATE TABLE IF NOT EXISTS users (
	name VARCHAR(256) PRIMARY KEY,
	password CHARACTER(256) NOT NULL,
	groupname VARCHAR(256),
	FOREIGN KEY(groupname) REFERENCES groups(name)
);

CREATE TABLE IF NOT EXISTS groups (
	name VARCHAR(256) PRIMARY KEY,
	permissions VARCHAR(2048)
);

/* Add user name=admin password=admin */
INSERT INTO users(name,password) VALUES("admin","8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918");