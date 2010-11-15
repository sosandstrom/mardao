-- Created by mardao DAO generator 2010-11-03T10:56:35.616+0700
-- CREATE script for TABLE tStatus corresponding to Entity Status 
--
CREATE TABLE `tStatus` (
	`id` BIGINT NOT NULL,
	`cText` VARCHAR(255)  DEFAULT 0 , 
	`employee` BIGINT NOT NULL,
	CONSTRAINT `FkStatusEmployee` FOREIGN KEY (`employee`) REFERENCES `tblEmployee` (`id`),
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
