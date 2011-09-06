-- Created by mardao DAO generator 2011-09-04T10:34:57.651+0700
-- CREATE script for TABLE Book corresponding to Entity Book 
--
CREATE TABLE `Book` (
	`ISBN` VARCHAR(255) NOT NULL,
	`title` VARCHAR(255)  DEFAULT 0 , 
	PRIMARY KEY (`ISBN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
