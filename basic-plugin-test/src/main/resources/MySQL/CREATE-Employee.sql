-- Created by mardao DAO generator 2010-06-05T13:29:43.938+0700
-- CREATE script for TABLE tblEmployee corresponding to Entity Employee 
--
CREATE TABLE `tblEmployee` (
	`id` INTEGER NOT NULL,
	`name` VARCHAR(255) DEFAULT NULL, 
	`signum` VARCHAR(255) DEFAULT NULL, 
	`currentEmployerID` INTEGER DEFAULT NULL,
	`currentUnitID` INTEGER DEFAULT NULL,
	PRIMARY KEY (`id`),	
	UNIQUE (`name`),
	UNIQUE (`currentEmployerID`,`signum`),
	CONSTRAINT `FkEmployeeCurrentEmployer` FOREIGN KEY (`currentEmployerID`) REFERENCES `Organization` (`id`),	
	CONSTRAINT `FkEmployeeCurrentUnit` FOREIGN KEY (`currentUnitID`) REFERENCES `tblOrganizationUnit` (`ouID`)	
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
