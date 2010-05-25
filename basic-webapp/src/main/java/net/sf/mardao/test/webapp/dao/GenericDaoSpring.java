package net.sf.mardao.test.webapp.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public abstract class GenericDaoSpring<T, ID extends Serializable> implements GenericDao<T, ID> {

   protected final Class<T> persistentClass;
   
   public GenericDaoSpring(Class<T> type) {
      this.persistentClass = type;
   }
   
   /**
    * Override to implement specific EntityManager factory functionality
    * @see getEntityManager
    */
   public void close() {
      // As the EntityManager is managed by the Container,
      // we should do nothing here.
      // This setting is controlled by generator plugin configuration <containerManagedEntityManager>.
	}   

   protected EntityTransaction beginTransaction() {
      return null;
   }

   protected void commitTransaction(EntityTransaction tx) {
      // Do nothing
   }

   protected Class<T> getPersistentClass() {
      return persistentClass;
   }

   public T findByPrimaryKey(ID id) {
   		// TODO: implement
      //final EntityManager em = getEntityManager();
      final T returnValue = null; //em.find(persistentClass, id);
      //close();
      return returnValue;
   }

   public void persist(T entity) {
   		// TODO: implement
      //final EntityManager em = getEntityManager();
      //final EntityTransaction tx = beginTransaction();
      //em.persist(entity);
      //commitTransaction(tx);
      //close();
   }

   public void update(T entity) {
   		// TODO: implement
      //final EntityManager em = getEntityManager();
      //final EntityTransaction tx = beginTransaction();
      //em.merge(entity);
      //commitTransaction(tx);
      //close();
   }

   public void delete(T entity) {
   		// TODO: implement
//      final EntityManager em = getEntityManager();
//      final EntityTransaction tx = beginTransaction();
//      em.remove(entity);
//      commitTransaction(tx);
//      close();
   }

   @SuppressWarnings("unchecked")
   public List<T> findAll() {
   		// TODO: implement
      final List<T> returnValue = null;
      
      // for eager fetch then close
      // returnValue.size();
      // close();
      return returnValue;
   }
   
	/**
	 * find-by method for generic unique attribute field 
	 * @param fieldName the generic unique attribute's field name
	 * @param param the generic unique attribute
	 * @return the unique entity for the specified attribute
	 */
	@SuppressWarnings("unchecked")
	protected final T findUniqueBy(String fieldName, Object param) {
		// TODO: implement
		//try {
		//    final EntityManager em = getEntityManager();
		//	final Query query =	em.createQuery("SELECT t FROM " + persistentClass.getName() + " t WHERE t." + fieldName + " = :param");
		//	query.setParameter("param", param);
		//	final T returnValue = (T) query.getSingleResult();
		//	return returnValue;
		//}
		//catch (javax.persistence.NoResultException nre) {
		//}
		//finally {
		//	close();
		//}
		return null;
	}
	
	/**
	 * find-by method for generic attribute field
	 * @param fieldName the generic unique attribute's field name
	 * @param param the specified generic attribute
	 * @return a List of entities with the specified attribute
	 */
	@SuppressWarnings("unchecked")
	protected final List<T> findBy(String fieldName, Object param) {
		// TODO: implement
	    //final EntityManager em = getEntityManager();
		//final Query query =	em.createQuery("SELECT t FROM " + persistentClass.getName() + " t WHERE t." + fieldName + " = :param");
		//query.setParameter("param", param);
		final List<T> returnValue = null; // query.getResultList();
		
		// Do this so we can eager load the list and close the EM: 
		//returnValue.size();
		
		//close();
		return returnValue;
	}
	
	/**
	 * find-by method for generic field
	 * @param fieldName the generic unique attribute's field name
	 * @param foreignClass the related entity's class
	 * @param key the specified foreign key
	 * @return a List of entities with the specified foreign key
	 */
	@SuppressWarnings("unchecked")
	protected final List<T> findByKey(String fieldName, Class foreignClass, Object key) {
		// TODO: implement
	    //final EntityManager em = getEntityManager();
		//final Object lazy = em.getReference(foreignClass, key);
		final List<T> returnValue = null; // findBy(fieldName, lazy);
		
		// Do this so we can eager load the list and close the EM: 
		//returnValue.size();
		
		//close();
		return returnValue;
	}
   
	/**
	 * find-by method for unique attributes
	 * @param args the specified attribute name-value map
	 * @return the unique entity for the specified attributes
	 */
	@SuppressWarnings("unchecked")
	protected final T findBy(Map<String,Object> args) {
		// TODO: implement
	    //final EntityManager em = getEntityManager();
	    //final StringBuffer ql = new StringBuffer("SELECT t FROM ");
	    //ql.append(persistentClass.getName());
	    //ql.append(" t");
	    //boolean isFirst = true;
	    //for (Entry<String,Object> entry : args.entrySet()) {
	    //	ql.append(isFirst ? " WHERE t." : " AND t.");
	    //	isFirst = false;
	    //	ql.append(entry.getKey());
	    //	ql.append(" = :pP");
	    //	ql.append(entry.getKey());
	    //}
		//final Query query =	em.createQuery(ql.toString());
	    //for (Entry<String,Object> entry : args.entrySet()) {
	    //	query.setParameter("pP" + entry.getKey(), entry.getValue());
	    //}
		final T returnValue = null; //(T) query.getSingleResult();
		//close();
		return returnValue;
	}

	/**
	 * find-by method for unique attributes
	 * @param args the specified attribute name-value map
	 * @param orderBy the attribute to order by
	 * @param ascending true if ascending
	 * @return the unique entity for the specified attributes
	 */
	@SuppressWarnings("unchecked")
	protected List<T> findBy(Map<String,Object> args, String orderBy, boolean ascending) {
		// TODO: implement
	    //final EntityManager em = getEntityManager();
	    //final StringBuffer ql = new StringBuffer("SELECT t FROM ");
	    //ql.append(persistentClass.getName());
	    //ql.append(" t");
	    //boolean isFirst = true;
	    //for (Entry<String,Object> entry : args.entrySet()) {
	    //	ql.append(isFirst ? " WHERE t." : " AND t.");
	    //	isFirst = false;
	    //	ql.append(entry.getKey());
	    //	ql.append(" = :pP");
	    //	ql.append(entry.getKey());
	    //}
	    
	    //ql.append(" ORDER BY t.");
	    //ql.append(orderBy);
	    //ql.append(ascending ? " ASC" : " DESC");
	    
		//final Query query =	em.createQuery(ql.toString());
	    //for (Entry<String,Object> entry : args.entrySet()) {
	    //	query.setParameter("pP" + entry.getKey(), entry.getValue());
	    //}
		final List<T> returnValue = null; //query.getResultList();
		
		// Do this so we can eager load the list and close the EM: 
		//returnValue.size();
		
		//close();
		return returnValue;
	}
	
	/**
	 * find-by method for many-to-many fields
	 * @param primaryKeyName name of this entity class' primary key, e.g. "id"
	 * @param fieldName name of this side's column in the join table
	 * @param foreignSimpleClass name of the related entity class (simple name) e.g. "role"
	 * @param foreignFieldName name of the related side's column in the join table
	 * @param foreignId the related entity's primary key value
	 * @return a List of entities belonging to the many-to-many relation
	 */
	@SuppressWarnings("unchecked")
	protected final List<T> findByManyToMany(String primaryKeyName, 
			String fieldName, 
			String foreignSimpleClass, String foreignFieldName, Object foreignId) {
		// TODO: implement
	    //final EntityManager em = getEntityManager();
		//final Query keyQuery =	em.createQuery("SELECT t." + fieldName + 
		//	" FROM " + persistentClass.getName() + "_" + foreignSimpleClass +  
		//		" t WHERE t." + foreignFieldName + " = :param");
		//keyQuery.setParameter("param", foreignId);
		//final List<ID> keyList = keyQuery.getResultList();
		
		//final Query query = em.createQuery("SELECT t FROM " + persistentClass.getName() + 
		//	" t WHERE t." + primaryKeyName + " IN :param");
		//query.setParameter("param", keyList);
		final List<T> returnValue = null; //query.getResultList();
		
		// Do this so we can eager load the list and close the EM: 
		//returnValue.size();
		
		//close();
		return returnValue;
	}
		
}
