-- Created by mardao DAO generator 2010-06-06T06:47:53.042+0700
-- CREATE script for TABLE Employee corresponding to Entity Employee 
--
CREATE TABLE `Employee` (
	`id` INTEGER NOT NULL,
	`name` VARCHAR(255) DEFAULT NULL, 
	`currentEmployer` INTEGER DEFAULT NULL,
	PRIMARY KEY (`id`),	
	CONSTRAINT `FkEmployeeCurrentEmployer` FOREIGN KEY (`currentEmployer`) REFERENCES `Organization` (`id`)	
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
