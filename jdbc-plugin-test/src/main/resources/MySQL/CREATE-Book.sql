-- Created by mardao DAO generator 2012-01-04T13:29:33.474+0700
-- CREATE script for TABLE Book corresponding to Entity Book 
--
CREATE TABLE `Book` (
	`ISBN` VARCHAR(255) NOT NULL,
	`title` VARCHAR(255)  DEFAULT 0 , 
	PRIMARY KEY (`ISBN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
