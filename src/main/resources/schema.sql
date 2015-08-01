CREATE DATABASE books CHARACTER SET utf8 COLLATE utf8_general_ci;

-- DROP TABLE author;
-- DROP TABLE genre;
-- DROP TABLE book_genre;
-- DROP TABLE book;


CREATE TABLE IF NOT EXISTS author (
	id VARCHAR(255) PRIMARY KEY, 
	first_name VARCHAR(100),
	last_name VARCHAR(100)	
) ENGINE=InnoDB;
CREATE INDEX author_last_name on author(last_name);

CREATE TABLE IF NOT EXISTS genre (
	id VARCHAR(100) primary key,
	title VARCHAR(255),
	active INT DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS book_genre (
	book_id INT,
	genre_id VARCHAR(100),
	primary key(book_id, genre_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS book (
	id INT PRIMARY KEY,
	author_id VARCHAR(255),
	title VARCHAR(255),
	url VARCHAR(255),
	annotation TEXT,
	publisher VARCHAR(255),
	year VARCHAR(255),
	isbn VARCHAR(255)
) ENGINE=InnoDB;

