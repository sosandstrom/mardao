package net.sf.mardao.test.basic.dao;

import java.util.List;

import net.sf.mardao.test.basic.domain.Employee;
import net.sf.mardao.test.basic.domain.Organization;
import net.sf.mardao.test.basic.domain.OrganizationUnit;

/**
 * Business Methods interface for entity Employee.
 */
public interface EmployeeDao extends AbstractEmployeeDaoInterface {

	Employee findBy(String name, String signum, OrganizationUnit unit, Organization org);
	
	List<Employee> findByOrganization(String orgName);
}
