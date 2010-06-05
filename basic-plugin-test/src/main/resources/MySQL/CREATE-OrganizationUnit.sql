-- Created by mardao DAO generator 2010-06-05T13:29:43.938+0700
-- CREATE script for TABLE tblOrganizationUnit corresponding to Entity OrganizationUnit 
--
CREATE TABLE `tblOrganizationUnit` (
	`ouID` INTEGER NOT NULL,
	`name` VARCHAR(255) DEFAULT NULL, 
	`orgID` INTEGER DEFAULT NULL,
	`parentID` INTEGER DEFAULT NULL,
	PRIMARY KEY (`ouID`),	
	UNIQUE (`name`),
	CONSTRAINT `FkOrganizationUnitOrganization` FOREIGN KEY (`orgID`) REFERENCES `Organization` (`id`),	
	CONSTRAINT `FkOrganizationUnitParentUnit` FOREIGN KEY (`parentID`) REFERENCES `tblOrganizationUnit` (`ouID`)	
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
