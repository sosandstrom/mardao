-- Created by mardao DAO generator 2010-06-01T20:14:17.262+0700
-- 
--
-- ----------- CREATE TABLE for entities in package net.sf.mardao.test.webapp.domain -----------
CREATE TABLE `Organization` (
	`id` INTEGER NOT NULL,
	`name` VARCHAR(255) DEFAULT NULL, 
	PRIMARY KEY (`id`)	
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `Employee` (
	`id` INTEGER NOT NULL,
	`name` VARCHAR(255) DEFAULT NULL, 
	`currentEmployer` INTEGER DEFAULT NULL,
	PRIMARY KEY (`id`),	
	CONSTRAINT `FkEmployeeCurrentEmployer` FOREIGN KEY (`currentEmployer`) REFERENCES `Organization` (`id`)	
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------- CREATE TABLE for sequence and db version -----------
create table id_sequence (highest int not null) type=MYISAM;
insert into id_sequence values(0);
create table db_version (currentVersion int not null) type=MYISAM;
insert into db_version values(1);
