-- Created by mardao DAO generator 2010-06-12T14:40:04.780+0700
-- CREATE script for TABLE Model corresponding to Entity Model 
--
CREATE TABLE `Model` (
	`id` INTEGER NOT NULL,
	`name` VARCHAR(255) DEFAULT NULL, 
	`manufacturer` INTEGER DEFAULT NULL,
	`type` INTEGER DEFAULT NULL,
	PRIMARY KEY (`id`),	
	CONSTRAINT `FkModelManufacturer` FOREIGN KEY (`manufacturer`) REFERENCES `Manufacturer` (`id`),	
	CONSTRAINT `FkModelType` FOREIGN KEY (`type`) REFERENCES `Type` (`id`)	
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
