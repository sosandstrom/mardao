package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.util.List;

public interface Dao<T, ID extends Serializable> {
	
	int deleteAll();

	List<T> findAll();
	
	T findByPrimaryKey(ID primaryKey);

	void persist(T entity);

}
