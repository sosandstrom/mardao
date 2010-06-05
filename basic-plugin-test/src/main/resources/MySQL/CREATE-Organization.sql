-- Created by mardao DAO generator 2010-06-05T13:29:43.938+0700
-- CREATE script for TABLE Organization corresponding to Entity Organization 
--
CREATE TABLE `Organization` (
	`id` INTEGER NOT NULL,
	`name` VARCHAR(255) DEFAULT NULL, 
	PRIMARY KEY (`id`),	
	UNIQUE (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
