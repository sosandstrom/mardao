package net.sf.mardao.test.basic.dao;

import java.util.HashMap;

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

	// Implement your business methods here
	public Employee findBy(String name, String signum, OrganizationUnit unit, Organization org) {
		HashMap<String,Object> args = new HashMap<String,Object>();
		
		args.put(COLUMN_NAME_CURRENTEMPLOYER, org.getId());
		args.put(COLUMN_NAME_CURRENTUNIT, unit.getKey());
		args.put(COLUMN_NAME_NAME, name);
		args.put(COLUMN_NAME_SIGNUM, signum);
		
		return genericDao.findBy(args);
	}

}
