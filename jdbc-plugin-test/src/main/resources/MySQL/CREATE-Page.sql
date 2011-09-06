-- Created by mardao DAO generator 2011-09-04T10:34:57.651+0700
-- CREATE script for TABLE Page corresponding to Entity Page 
--
CREATE TABLE `Page` (
	`pageNumber` BIGINT NOT NULL,
	`body` VARCHAR(255)  DEFAULT 0 , 
	`book` VARCHAR(255)  DEFAULT 0 , 
	PRIMARY KEY (`pageNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
