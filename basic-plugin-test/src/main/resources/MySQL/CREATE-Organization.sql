-- Created by mardao DAO generator 2012-03-19T14:09:46.745+0700
-- CREATE script for TABLE Organization corresponding to Entity Organization 
--
CREATE TABLE `Organization` (
	`id` BIGINT NOT NULL,
	`name` VARCHAR(255)  DEFAULT 0 , 
	UNIQUE (`name`),
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
