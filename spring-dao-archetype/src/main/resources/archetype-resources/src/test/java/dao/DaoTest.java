#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dao;

import java.util.List;

import junit.framework.TestCase;
import ${package}.domain.Manufacturer;
import ${package}.domain.Model;
import ${package}.domain.Type;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class DaoTest extends TestCase {

	static final Logger LOG = Logger.getLogger(DaoTest.class);
	ManufacturerDao manufacturerDao;
	ModelDao modelDao;
	TypeDao typeDao;
	
	Manufacturer audi;
	Type sedan, suv;
	Model rs4, q7;
	
	protected void setUp() throws Exception {
		ApplicationContext springCtx = new GenericXmlApplicationContext("/${artifactId}-test.xml");
		manufacturerDao = (ManufacturerDao) springCtx.getBean("manufacturerDao");
		modelDao = (ModelDao) springCtx.getBean("modelDao");
		typeDao = (TypeDao) springCtx.getBean("typeDao");
		populate();
		LOG.info("--- setUp() " + getName() + " ---");
	}
	
	protected void tearDown() throws Exception {
		LOG.info("--- tearDown() " + getName() + " ---");
		delete();
	}

	private void populate() {
		// create Audi manufacturer
		audi = manufacturerDao.persist("Audi");
	
		// create Sedan and SUV types
		sedan = typeDao.persist("Sedan");
		suv = typeDao.persist("SUV");
		
		// create RS4 and Q7 models
		rs4 = modelDao.persist("RS4", audi, sedan);
		q7 = modelDao.persist("Q7", audi, suv);
	}

	public void testFindModelByForeignKey() {
		List<Model> actual = modelDao.findByManufacturer(audi);
		assertNotNull(actual);
		assertEquals(2, actual.size());
		
		actual = modelDao.findByTypeId(sedan.getId());
		assertNotNull(actual);
		assertEquals(1, actual.size());
		assertEquals(rs4.getName(), actual.get(0).getName());
	}
	
	public void testFindUniqueModel() {
		Model actual = modelDao.findByName(rs4.getName());
		assertNotNull(actual);
		assertEquals(rs4.getId(), actual.getId());
	}

	private void delete() {
		modelDao.delete(q7);
		modelDao.delete(rs4);
		
		typeDao.delete(suv);
		typeDao.delete(sedan);
		
		manufacturerDao.delete(audi);
	}
}
