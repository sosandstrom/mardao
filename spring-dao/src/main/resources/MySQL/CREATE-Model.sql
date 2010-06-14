-- Created by mardao DAO generator 2010-06-15T06:03:11.383+0700
-- CREATE script for TABLE Model corresponding to Entity Model 
--
CREATE TABLE `Model` (
	`id` INTEGER NOT NULL,
	`name` VARCHAR(255) DEFAULT NULL, 
	`manufacturerId` INTEGER DEFAULT NULL,
	`typeId` INTEGER DEFAULT NULL,
	PRIMARY KEY (`id`),	
	UNIQUE (`name`),
	CONSTRAINT `FkModelManufacturer` FOREIGN KEY (`manufacturerId`) REFERENCES `Manufacturer` (`id`),	
	CONSTRAINT `FkModelType` FOREIGN KEY (`typeId`) REFERENCES `tblType` (`id`)	
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
