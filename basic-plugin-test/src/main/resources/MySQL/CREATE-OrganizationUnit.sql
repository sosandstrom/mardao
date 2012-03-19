-- Created by mardao DAO generator 2012-03-19T14:09:46.745+0700
-- CREATE script for TABLE tblOrganizationUnit corresponding to Entity OrganizationUnit 
--
CREATE TABLE `tblOrganizationUnit` (
	`ouID` BIGINT NOT NULL,
	`name` VARCHAR(255)  DEFAULT 0 , 
	`orgID` BIGINT DEFAULT NULL,
	`parentID` BIGINT DEFAULT NULL,
	UNIQUE (`name`),
	CONSTRAINT `FkOrganizationUnitOrganization` FOREIGN KEY (`orgID`) REFERENCES `Organization` (`id`),
	CONSTRAINT `FkOrganizationUnitParentUnit` FOREIGN KEY (`parentID`) REFERENCES `tblOrganizationUnit` (`ouID`),
	PRIMARY KEY (`ouID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
