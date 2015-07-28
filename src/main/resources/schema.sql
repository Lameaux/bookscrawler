CREATE DATABASE books CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE IF NOT EXISTS books (
	id INT auto_increment PRIMARY KEY, 
	title VARCHAR(255),
) ENGINE=InnoDB;

