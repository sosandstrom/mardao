package net.sf.mardao.api.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public abstract class AEDDaoImpl<T, ID extends Serializable> implements Dao<T, ID>{

	/** Using slf4j logging */
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	/** mostly for logging */
	protected final Class<T> persistentClass;
	
	protected AEDDaoImpl(Class<T> type) {
		this.persistentClass = type;
	}
   
	/**
	 * Converts a datastore Entity into the domain object.
	 * Implemented in <T>AEDHelper 
	 * @param entity the datastore Entity
	 * @return a domain object
	 */
	protected abstract T convert(Entity entity);
	
	protected abstract Key createKey(T entity);
	
	protected abstract String getTableName();
		
	protected abstract String getPrimaryKeyColumnName();
	
	protected abstract List<String> getColumnNames();
	
	
	protected DatastoreService getDatastoreService() {
		return DatastoreServiceFactory.getDatastoreService();
	}
	
	protected List<T> asIterable(PreparedQuery pq, int limit, int offset) {
		final List<T> returnValue = new ArrayList<T>();
		
		FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
		
		if (0 < limit) {
			fetchOptions.limit(limit);
		}
		
		if (0 < offset) {
			fetchOptions.offset(offset);
		}
		
		T domain;
		for (Entity entity : pq.asIterable(fetchOptions)) {
			domain = convert(entity);
			returnValue.add(domain);
//			LOGGER.debug("  entity {} -> domain {}", entity, domain);
		}
		return returnValue;
	}
	
	
	/**
	 * @param ascending 
	 * @param orderBy 
	 * @param filters 
	 *
	 */
	protected PreparedQuery prepare(Map<String, Object> filters, String orderBy, boolean ascending) {
		final DatastoreService datastore = getDatastoreService();
		
		Query q = new Query(getTableName());
		
		// filter query:
		if (null != filters) {
			for (Entry<String, Object> filter : filters.entrySet()) {
				q.addFilter(filter.getKey(), FilterOperator.EQUAL, filter.getValue());
			}
		}
		
		// sort query?
		if (null != orderBy) {
			q.addSort(orderBy, ascending ? SortDirection.ASCENDING : SortDirection.DESCENDING);
		}
		
		return datastore.prepare(q);
	}

	/**
	 * @param ascending 
	 * @param orderBy 
	 * @param filters 
	 *
	 */
	protected PreparedQuery prepare(String orderBy, boolean ascending, Expression[] filters) {
		final DatastoreService datastore = getDatastoreService();
		
		Query q = new Query(getTableName());
		
		// filter query:
		if (null != filters) {
			for (Expression expression : filters) {
				q.addFilter(expression.getColumn(), (FilterOperator) expression.getOperation(), expression.getOperand());
			}
		}
		
		// sort query?
		if (null != orderBy) {
			q.addSort(orderBy, ascending ? SortDirection.ASCENDING : SortDirection.DESCENDING);
		}
		
		return datastore.prepare(q);
	}

	protected T findByPrimaryKey(Key key) {
		T domain = null;
		final DatastoreService datastore = getDatastoreService();
		try {
			final Entity entity = datastore.get(key);
			domain = convert(entity);
		} catch (EntityNotFoundException ignore) {
		}
		LOGGER.debug("{} -> {}", key.toString(), domain);
		
		return domain;
	}
	
	protected Key persist(Entity entity) {
		final DatastoreService datastore = getDatastoreService();
		
		return datastore.put(entity);
	}

	public void update(T entity) {
		persist(entity);
	}

	public void delete(T entity) {
		final Key key = createKey(entity);
		final DatastoreService datastore = getDatastoreService();
		datastore.delete(key);
	}

	public List<T> findAll() {
		LOGGER.debug(persistentClass.getSimpleName());
		return findBy(null, false, -1, 0);
	}

	public T findUniqueBy(String fieldName, Object param) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<T> findBy(String fieldName, Object param) {
		LOGGER.debug("{} = {}", fieldName, param);
		return findBy(null, false, -1, 0, new Expression(fieldName, Query.FilterOperator.EQUAL, param));
	}

	public List<T> findByKey(String fieldName, Class foreignClass, Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	public T findBy(Map<String, Object> args) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<T> findBy(Map<String, Object> args, String orderBy,
			boolean ascending) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<T> findBy(Map<String, Object> filters, String orderBy,
			boolean ascending, int limit, int offset) {
		PreparedQuery pq = prepare(filters, orderBy, ascending);
		return asIterable(pq, limit, offset);
	}

	protected List<T> findBy(String orderBy,
			boolean ascending, int limit, int offset, Expression... filters) {
		PreparedQuery pq = prepare(orderBy, ascending, filters);
		return asIterable(pq, limit, offset);
	}
	
	public List<T> findBy(String orderBy, boolean ascending, int limit,
			Expression... args) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<T> findByManyToMany(String primaryKeyName, String fieldName,
			String foreignSimpleClass, String foreignFieldName, Object foreignId) {
		// TODO Auto-generated method stub
		return null;
	}

	public int deleteAll() {
		// TODO Auto-generated method stub
		return 0;
	}

	public T persist(Map<String, Object> nameValuePairs) {
		// TODO Auto-generated method stub
		return null;
	}

	public int update(Map<String, Object> values, Expression... expressions) {
		// TODO Auto-generated method stub
		return 0;
	}

}
