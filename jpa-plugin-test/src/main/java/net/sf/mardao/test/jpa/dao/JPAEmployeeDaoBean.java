package net.sf.mardao.test.jpa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mardao.test.jpa.domain.JPAEmployee;

/**
 * Implementation of Business Methods related to entity JPAEmployee.
 */
public class JPAEmployeeDaoBean 
	extends AbstractJPAEmployeeDao
		implements JPAEmployeeDao 
{

	public int deleteAll() {
		return genericDao.deleteAll();
	}
	
	
	public List<JPAEmployee> findByExpression(String name, int maxResult) {
		Expression gt = new Expression(COLUMN_NAME_NAME, ">", name);
		
		return findBy(COLUMN_NAME_SIGNUM, true, maxResult, gt);
	}
	
	public int updateAll(String signum) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put(COLUMN_NAME_SIGNUM, signum);
		return genericDao.update(values);
	}

}
