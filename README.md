### Mardao Architect's Java DAO generator

Latest Stable Version: 2.3.6

License: http://www.gnu.org/licenses/gpl-3.0.html

Mardao is an ORM and DAO generator, packaged as a maven plugin.
It generates DAO classes using your annotated domain classes as input.

### Build from scratch with

	 mvn clean install -DskipTests -Dmaven.javadoc.skip=true -Dmaven.test.skip=true && mvn install -PpluginTest

For more info and support, visit the mardao wiki at
(Mardao Wiki)[https://github.com/sosandstrom/mardao/wiki]
or the Open Pockets blog at
(Open Pockets)[http://blog.wadpam.com]

# Release History

## Release 3.0.4

* First 3.x release, introducing Mapper and Supplier.

## Release 2.3.1 Overview:

* Dao-supported transactions

## Release date: 2013-05-15

## New features:

## Fixed bugs:

--------------------------------------------------------------------------------

## Release 2.3.0 Overview:

* Support for Android again

## Release date: 2013-04-20

## New features:

## Fixed bugs:

--------------------------------------------------------------------------------

## Release 2.0.3 Overview:

* Float bugfix

## Release date: 2012-10-

## New features:

## Fixed bugs:
* Float conversion from Double (GAE)

--------------------------------------------------------------------------------

## Release 2.0.2 Overview:

* queryByParent bugfix
* Support for Windows backslash

## Release date: 2012-10-16

## New features:

## Fixed bugs:
* queryByParent bugfix
* Support for Windows backslash

--------------------------------------------------------------------------------

## Release 2.0.1 Overview:

* queryBy(Collection<?> field) is now queryBy(Object field)
* Exposes a few more methods in public

## Release date: 2012-10-06

## New features:
* Exposes a few more methods in public

## Fixed bugs:
* queryBy(Collection<?> field) is now queryBy(Object field)

--------------------------------------------------------------------------------

## Release 2.0.0 Overview:

Version 2.0.0 is the first release of the completely refactorized and rewritten version of Mardao 2.

## Release date: 2012-09-22

## New features:
Support for Android

## Fixed bugs:
Not applicable

--------------------------------------------------------------------------------
## Release 1.37 Overview:

Latest bugfix release of Mardao 1

## Release date: 2012-09-09

## New features:

Fixed bugs:

--------------------------------------------------------------------------------
Release 1.20 Overview:
Support for @Basic arrays, rename to mardao-maven-plugin
Release date: 2012-03-19

New features:
Support for @Basic arrays

Fixed bugs:

--------------------------------------------------------------------------------
Release 1.17 Overview:
Support for Android DAO generation, based on SQLiteDatabase and SQLiteHelper
Release date: 2011-12-18
You set the plugin configuration

<persistenceType>Android</persistenceType>

and use the dependency

<dependency>
	<groupId>net.sf.mardao</groupId>
	<artifactId>mardao-android</artifactId>
	<version>${mardao.version}</version>
<dependency>

to use for Android.

New features:
Support for Android

Fixed bugs:

--------------------------------------------------------------------------------
Release 1.9 Overview:
Good support for AppEngine low-level API, and parent keys.
You set
<persistenceType>AED</persistenceType>
to generate for AppEngine low-level.

New features:

Fixed bugs:

--------------------------------------------------------------------------------
Release 1.4 Overview:
One important Date bugfix, and support for Expression queries

New features:
3093502 	protected findBy(Expression... ) 	Closed 	2010-10-23 	f94os 	5 

Fixed bugs:
3093501 	Use ResultSet.getTimestamp() instead of getDate() 	Closed 	2010-10-23 	f94os 	Fixed 	7

--------------------------------------------------------------------------------
Release 1.3 Overview:
Only one bugfix, for findBy(Map args) methods

New features in 1.3:

Fixed bugs in 1.3:
3038659  	 findBy(Map args) does not find entities

--------------------------------------------------------------------------------
Release 1.2 Overview:
Only one bugfix for Eclipse IDE

New features in 1.2:

Fixed bugs in 1.2:
3017379  	 Generated source does not compile in java 1.5 (Eclipse IDE only?)

--------------------------------------------------------------------------------
Release 1.1 Overview:
Support @Table and @Column.
Generate per-table CREATE scripts
Use Column names instead of Entity attribute names
More JUnit tests

New features in 1.1:
3010142  	 @Column(name="currentEmployerID")
3010141 	 @Table(name="tblEmployee")

Fixed bugs in 1.1:
3011930  	 Use column names in AbstractDao, not attribute names 

--------------------------------------------------------------------------------
Version: 1.0 Initial release.
