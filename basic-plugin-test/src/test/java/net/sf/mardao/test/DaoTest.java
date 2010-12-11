package net.sf.mardao.test;

import java.util.List;

import junit.framework.TestCase;
import net.sf.mardao.test.basic.dao.EmployeeDao;
import net.sf.mardao.test.basic.dao.OrganizationDao;
import net.sf.mardao.test.basic.dao.OrganizationUnitDao;
import net.sf.mardao.test.basic.dao.StatusDao;
import net.sf.mardao.test.basic.domain.Employee;
import net.sf.mardao.test.basic.domain.Organization;
import net.sf.mardao.test.basic.domain.OrganizationUnit;
import net.sf.mardao.test.basic.domain.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class DaoTest extends TestCase {
	
	static final Logger LOG = LoggerFactory.getLogger(DaoTest.class);
	
	EmployeeDao employeeDao;
	OrganizationDao organizationDao;
	OrganizationUnitDao orgUnitDao;
	StatusDao statusDao;
	
	Employee boss, empl;
	Organization acme;
	OrganizationUnit RnD, swDev;
	Status idle;
	
	long orgSize, unitSize, emplSize;

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
		statusDao = (StatusDao) springCtx.getBean("statusDao");
		
		// populate for tests:
		orgSize = organizationDao.findAll().size();
		acme = organizationDao.persist("ACME");
		
		unitSize = orgUnitDao.findAll().size();
		RnD = orgUnitDao.persist("R & D", acme, null);
		swDev = orgUnitDao.persist("SW Development", acme, RnD);
		
		emplSize = employeeDao.findAll().size();
		boss = employeeDao.persist("the Boss", "boss", acme, RnD);
		empl = employeeDao.persist("Mr Employee", "empl", acme, swDev);
		
		idle = statusDao.persist("Idle", boss);
		
		LOG.info("--- setUp() " + getName() + " ---");
	}

	protected void tearDown() throws Exception {
		LOG.info("--- tearDown() " + getName() + " ---");
		
		// delete test entities
		statusDao.delete(idle);
		
		employeeDao.delete(empl);
		employeeDao.delete(boss);
		
		orgUnitDao.delete(swDev);
		orgUnitDao.delete(RnD);
		organizationDao.delete(acme);

		assertEquals(orgSize, organizationDao.findAll().size());
		assertEquals(unitSize, orgUnitDao.findAll().size());
		assertEquals(emplSize, employeeDao.findAll().size());
		super.tearDown();
	}

	public void testFindAll() {
		List<Employee> employees = employeeDao.findAll();
		assertNotNull("findAll", employees);
		assertFalse("findAll", employees.isEmpty());
	}
	
	public void testFindByPrimaryKey() {
		Employee actual = employeeDao.findByPrimaryKey(empl.getId());
		assertEquals("findByPrimaryKey", empl.getName(), actual.getName());
	}
	
	public void testFindByName() {
		Employee actual = employeeDao.findByName(boss.getName());
		assertEquals("findByName", boss.getId(), actual.getId());
	}
	
	public void testFindByOrg() {
		List<Employee> actual = employeeDao.findByCurrentEmployer(acme);
		for (Employee e : actual) {
			LOG.info("+++ findByOrg " + e.getName());
		}
		assertEquals("employees", 2, actual.size());
	}
	
	public void testFindByOrgUnit() {
		List<Employee> actual = employeeDao.findByCurrentUnit(RnD);
		assertEquals("RnDs", 1, actual.size());
		Employee b = actual.get(0);
		
		actual = employeeDao.findByCurrentUnitKey(swDev.getKey());
		assertEquals("SW Devs", 1, actual.size());
		Employee s = actual.get(0);
		
		assertEquals("Orgs", b.getCurrentEmployer().getId(), s.getCurrentEmployer().getId());
		assertNotSame("Names", b.getName(), s.getName());
	}
	
	public void testFindBy() {
		Employee actual = employeeDao.findBy(boss.getName(), boss.getSignum(), 
				boss.getCurrentUnit(), boss.getCurrentEmployer());
		assertNotNull(actual);
		assertEquals(boss.getId(), actual.getId());
	}
	
	public void testFindByOrgName() {
		List<Employee> actual = employeeDao.findByOrganization(acme.getName());
		assertEquals(2, actual.size());
	}
	
	public void testUpdateEmployee() {
		empl.setCurrentUnit(RnD);
		employeeDao.update(empl);
		Employee actual = employeeDao.findByPrimaryKey(empl.getId());
		assertEquals(RnD.getKey(), actual.getCurrentUnit().getKey());
	}
	
	public void testFindStatusByEmployee() {
		Status actual = statusDao.findByEmployee(boss);
		assertEquals(idle.getId(), actual.getId());
		
		actual = statusDao.findByEmployee(empl);
		assertNull(actual);
	}
	
	public void testUpdateStatus() {
		Status status = statusDao.findByEmployee(boss);
		status.setText("Busy");
		status.setEmployee(empl);
		statusDao.update(status);
		
		Status actual = statusDao.findByEmployee(empl);
		assertEquals("Busy", actual.getText());
		
		assertNull(statusDao.findByEmployee(boss));
	}
}
