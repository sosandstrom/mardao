package net.sf.mardao.scratch.dao;

import java.util.List;

import junit.framework.TestCase;
import net.sf.mardao.scratch.domain.Manufacturer;
import net.sf.mardao.scratch.domain.Model;
import net.sf.mardao.scratch.domain.Type;

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
	
	private void populate() {
//		audi = manufacturerDao.
	}
	
	protected void setUp() throws Exception {
		ApplicationContext springCtx = new GenericXmlApplicationContext("/spring-dao-test.xml");
		modelDao = (ModelDao) springCtx.getBean("modelDao");
		populate();
		LOG.info("--- setUp() " + getName() + " ---");
	}
	
	public void testFindModelByType() {
		List<Model> actual = modelDao.findByManufacturer(audi);
	}

	protected void tearDown() throws Exception {
		LOG.info("--- tearDown() " + getName() + " ---");
	}

}
