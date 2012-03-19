-- Created by mardao DAO generator 2012-03-19T14:03:16.812+0700
-- CREATE script for TABLE tStatus corresponding to Entity Status 
--
CREATE TABLE `tStatus` (
	`id` BIGINT NOT NULL,
	`cText` VARCHAR(255)  DEFAULT 0 , 
	`employee` BIGINT NOT NULL,
	CONSTRAINT `FkStatusEmployee` FOREIGN KEY (`employee`) REFERENCES `tblEmployee` (`id`),
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
