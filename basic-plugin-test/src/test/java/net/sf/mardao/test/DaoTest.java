package net.sf.mardao.test;

import java.util.List;

import junit.framework.TestCase;
import net.sf.mardao.test.basic.dao.EmployeeDao;
import net.sf.mardao.test.basic.dao.OrganizationDao;
import net.sf.mardao.test.basic.dao.OrganizationUnitDao;
import net.sf.mardao.test.basic.domain.Employee;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class DaoTest extends TestCase {
	
	static final Logger LOG = Logger.getLogger(DaoTest.class);
	
	EmployeeDao employeeDao;
	OrganizationDao organizationDao;
	OrganizationUnitDao orgUnitDao;

	protected void setUp() throws Exception {
		LOG.info("=== setUp() " + getName() + " ===");
		super.setUp();
		ApplicationContext springCtx = new GenericXmlApplicationContext("/spring-test-context.xml");
		employeeDao = (EmployeeDao) springCtx.getBean("employeeDao");
		assertNotNull(employeeDao);
		organizationDao = (OrganizationDao) springCtx.getBean("organizationDao");
		assertNotNull(organizationDao);
		orgUnitDao = (OrganizationUnitDao) springCtx.getBean("organizationUnitDao");
		assertNotNull(orgUnitDao);
		LOG.info("--- setUp() " + getName() + " ---");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testFindAll() {
		List<Employee> employees = employeeDao.findAll();
		assertNotNull("findAll", employees);
		assertFalse("findAll", employees.isEmpty());
	}
	
	public void testFindByPrimaryKey() {
		Employee ola = employeeDao.findByName("Ola");
		assertNotNull("findByPrimaryKey", ola);
		Employee actual = employeeDao.findByPrimaryKey(ola.getId());
		assertEquals("findByPrimaryKey", ola.getName(), actual.getName());
	}
}
