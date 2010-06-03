package net.sf.mardao.test;

import net.sf.mardao.test.basic.dao.EmployeeDao;
import net.sf.mardao.test.basic.dao.OrganizationDao;
import net.sf.mardao.test.basic.dao.OrganizationUnitDao;
import net.sf.mardao.test.basic.domain.Employee;
import net.sf.mardao.test.basic.domain.Organization;
import net.sf.mardao.test.basic.domain.OrganizationUnit;

public class TestController {
	private final EmployeeDao employeeDao;
	private final OrganizationDao organizationDao;
	private final OrganizationUnitDao organizationUnitDao;
	
	public TestController(EmployeeDao employeeDao, OrganizationDao organizationDao, OrganizationUnitDao organizationUnitDao) {
		this.employeeDao = employeeDao;
		this.organizationDao = organizationDao;
		this.organizationUnitDao = organizationUnitDao;
	}
	
	public void populate() {
		Organization wadpam = organizationDao.persist("Wadpam AB");
		OrganizationUnit management = organizationUnitDao.persist("Management", wadpam, null);
		Employee ola = employeeDao.persist("Ola", "f94os", wadpam, management);
	}
}
