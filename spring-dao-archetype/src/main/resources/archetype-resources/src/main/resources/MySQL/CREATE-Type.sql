-- Created by mardao DAO generator 2010-06-15T06:03:11.383+0700
-- CREATE script for TABLE tblType corresponding to Entity Type 
--
CREATE TABLE `tblType` (
	`id` INTEGER NOT NULL,
	`name` VARCHAR(255) DEFAULT NULL, 
	PRIMARY KEY (`id`),	
	UNIQUE (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
