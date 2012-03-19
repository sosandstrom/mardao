-- Created by mardao DAO generator 2012-03-19T14:09:46.745+0700
-- CREATE script for TABLE tblEmployee corresponding to Entity Employee 
--
CREATE TABLE `tblEmployee` (
	`id` BIGINT NOT NULL,
	`name` VARCHAR(255)  DEFAULT 0 , 
	`signum` VARCHAR(255)  DEFAULT 0 , 
	`currentEmployerID` BIGINT DEFAULT NULL,
	`currentUnitID` BIGINT DEFAULT NULL,
	UNIQUE (`name`),
	UNIQUE (`currentEmployerID`,`signum`),
	CONSTRAINT `FkEmployeeCurrentEmployer` FOREIGN KEY (`currentEmployerID`) REFERENCES `Organization` (`id`),
	CONSTRAINT `FkEmployeeCurrentUnit` FOREIGN KEY (`currentUnitID`) REFERENCES `tblOrganizationUnit` (`ouID`),
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
