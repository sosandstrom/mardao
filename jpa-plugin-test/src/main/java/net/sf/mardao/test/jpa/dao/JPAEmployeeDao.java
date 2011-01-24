package net.sf.mardao.test.jpa.dao;

import java.util.List;

import net.sf.mardao.test.jpa.domain.JPAEmployee;

/**
 * Business Methods interface for entity JPAEmployee.
 */
public interface JPAEmployeeDao extends AbstractJPAEmployeeDaoInterface {

	int deleteAll();
	
	List<JPAEmployee> findByExpression(String name, int maxResult);
	
	int updateAll(String signum);
	
}
