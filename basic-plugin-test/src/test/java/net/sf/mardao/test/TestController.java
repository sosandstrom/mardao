package net.sf.mardao.test;

import net.sf.mardao.test.basic.dao.EmployeeDao;
import net.sf.mardao.test.basic.dao.OrganizationDao;
import net.sf.mardao.test.basic.domain.Employee;
import net.sf.mardao.test.basic.domain.Organization;

public class TestController {
	private final EmployeeDao employeeDao;
	private final OrganizationDao organizationDao;
	
	public TestController(EmployeeDao employeeDao, OrganizationDao organizationDao) {
		this.employeeDao = employeeDao;
		this.organizationDao = organizationDao;
	}
	
	public void populate() {
		Organization wadpam = organizationDao.persist("Wadpam AB");
		Employee ola = employeeDao.persist("Ola", "f94os", wadpam);
	}
}
