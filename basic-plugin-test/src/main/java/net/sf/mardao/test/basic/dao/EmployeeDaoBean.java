package net.sf.mardao.test.basic.dao;

import java.util.HashMap;
import java.util.List;

import net.sf.mardao.test.basic.domain.Employee;
import net.sf.mardao.test.basic.domain.Organization;
import net.sf.mardao.test.basic.domain.OrganizationUnit;


/**
 * Implementation of Business Methods related to entity Employee.
 */
public class EmployeeDaoBean 
	extends AbstractEmployeeDao
		implements EmployeeDao 
{
	private final OrganizationDao organizationDao;
	
	public EmployeeDaoBean(OrganizationDao organizationDao) {
		this.organizationDao = organizationDao;
	}
	
	// Implement your business methods here
	public Employee findBy(String name, String signum, OrganizationUnit unit, Organization org) {
		HashMap<String,Object> args = new HashMap<String,Object>();
		
		args.put(COLUMN_NAME_CURRENTEMPLOYER, org);
		args.put(COLUMN_NAME_CURRENTUNIT, unit);
		args.put(COLUMN_NAME_NAME, name);
		args.put(COLUMN_NAME_SIGNUM, signum);
		
		return genericDao.findBy(args);
	}

	public List<Employee> findByOrganization(String orgName) {
		Expression org = new Expression(organizationDao.COLUMN_NAME_NAME, "=", orgName);
		Expression.Foreign<Organization, Long> foreign = new Expression.Foreign<Organization, Long>(
				COLUMN_NAME_CURRENTEMPLOYER, "=", (GenericDaoImpl<Organization, Long>) organizationDao, org);
		return findBy(COLUMN_NAME_NAME, true, -1, foreign);
	}
}
