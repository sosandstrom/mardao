package net.sf.mardao.test;

import java.util.List;

import junit.framework.TestCase;
import net.sf.mardao.test.basic.dao.EmployeeDao;
import net.sf.mardao.test.basic.dao.OrganizationDao;
import net.sf.mardao.test.basic.domain.Employee;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class DaoTest extends TestCase {
	
	EmployeeDao employeeDao;
	OrganizationDao organizationDao;

	protected void setUp() throws Exception {
		super.setUp();
		ApplicationContext springCtx = new GenericXmlApplicationContext("/spring-dao.xml");
		employeeDao = (EmployeeDao) springCtx.getBean("employeeDao");
		assertNotNull(employeeDao);
		organizationDao = (OrganizationDao) springCtx.getBean("organizationDao");
		assertNotNull(organizationDao);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testFindAll() {
		List<Employee> employees = employeeDao.findAll();
		assertNotNull(employees);
		
	}
}
