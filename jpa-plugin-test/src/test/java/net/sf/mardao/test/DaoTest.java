package net.sf.mardao.test;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.After;
import org.junit.Before;
import java.util.List;

import net.sf.mardao.test.jpa.dao.JPAEmployeeDao;
import net.sf.mardao.test.jpa.dao.JPAOrganizationDao;
import net.sf.mardao.test.jpa.dao.JPAOrganizationUnitDao;
import net.sf.mardao.test.jpa.domain.JPAEmployee;
import net.sf.mardao.test.jpa.domain.JPAOrganization;
import net.sf.mardao.test.jpa.domain.JPAOrganizationUnit;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/spring-test-context.xml"})
@Transactional()
public class DaoTest {

   static final Logger LOG = Logger.getLogger(DaoTest.class);

   @Autowired
   private JPAEmployeeDao employeeDao;
   @Autowired
   private JPAOrganizationDao organizationDao;
   @Autowired
   private JPAOrganizationUnitDao orgUnitDao;
   private JPAEmployee boss, empl;
   private JPAOrganization acme;
   private JPAOrganizationUnit RnD, swDev;
   private long orgSize, unitSize, emplSize;

   @Before
   public void setUp() throws Exception {
      LOG.info("=== setUp() ===");

      // populate for tests:
      orgSize = organizationDao.findAll().size();
      acme = new JPAOrganization();
      acme.setId(1L);
      acme.setName("ACME");
		organizationDao.persist(acme);

      unitSize = orgUnitDao.findAll().size();
      RnD = new JPAOrganizationUnit();
      RnD.setId(1L);
      RnD.setName("R & D");
      RnD.setOrganization(acme);
      orgUnitDao.persist(RnD);

      swDev = new JPAOrganizationUnit();
      swDev.setId(2L);
      swDev.setName("SW Department");
      swDev.setOrganization(acme);
      swDev.setParentUnit(RnD);
      orgUnitDao.persist(swDev);

      emplSize = employeeDao.findAll().size();
      boss = new JPAEmployee();
      boss.setId(1L);
      boss.setName("the Boss");
      boss.setSignum("boss");
      boss.setCurrentEmployer(acme);
      boss.setCurrentUnit(RnD);
      employeeDao.persist(boss);

      empl = new JPAEmployee();
      empl.setId(2L);
      empl.setName("Mr Employee");
      empl.setSignum("empl");
      empl.setCurrentEmployer(acme);
      empl.setCurrentUnit(swDev);
      employeeDao.persist(empl);
   }

   @After
   public void tearDown() throws Exception {
      LOG.info("--- tearDown() ---");

      // delete test entities
      employeeDao.delete(empl);
      employeeDao.delete(boss);

      orgUnitDao.delete(swDev);
      orgUnitDao.delete(RnD);
      organizationDao.delete(acme);

      assertEquals(orgSize, organizationDao.findAll().size());
      assertEquals(unitSize, orgUnitDao.findAll().size());
      assertEquals(emplSize, employeeDao.findAll().size());
   }

   @Test
   public void testFindAll() {
      List<JPAEmployee> employees = employeeDao.findAll();
      assertNotNull("findAll", employees);
      assertFalse("findAll", employees.isEmpty());
   }

   @Test
   public void testFindByPrimaryKey() {
      JPAEmployee actual = employeeDao.findByPrimaryKey(1L);
      assertEquals("findByPrimaryKey", "the Boss", actual.getName());
   }

   @Test
   public void testFindByName() {
      JPAEmployee actual = employeeDao.findByName("the Boss");
      assertEquals("findByName", 1L, actual.getId().longValue());
   }

   @Test
   public void testFindByOrg() {
      List<JPAEmployee> actual = employeeDao.findByCurrentEmployer(acme);
      for (JPAEmployee e : actual) {
         LOG.info("+++ findByOrg " + e.getName());
      }
      assertEquals("employees", 2, actual.size());
   }

   @Test
   public void testFindByOrgUnit() {
      List<JPAEmployee> actual = employeeDao.findByCurrentUnit(RnD);
      assertEquals("RnDs", 1, actual.size());
      JPAEmployee b = actual.get(0);

      actual = employeeDao.findByCurrentUnitId(2L);
      assertEquals("SW Devs", 1, actual.size());
      JPAEmployee s = actual.get(0);

      assertEquals("Orgs", b.getCurrentEmployer().getId(), s.getCurrentEmployer().getId());
      assertNotSame("Names", b.getName(), s.getName());
   }

   @Test
   public void testUpdateEmployee() {
      empl.setCurrentUnit(RnD);
      employeeDao.update(empl);
      JPAEmployee actual = employeeDao.findByPrimaryKey(empl.getId());
      assertEquals(RnD.getId(), actual.getCurrentUnit().getId());
   }
}
