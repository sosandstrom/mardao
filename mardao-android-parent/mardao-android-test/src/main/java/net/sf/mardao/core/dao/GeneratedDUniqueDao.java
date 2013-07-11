package net.sf.mardao.core.dao;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.dao.Dao;
import net.sf.mardao.core.domain.DUnique;
import net.sf.mardao.core.geo.DLocation;

/**
 * DAO interface with finder methods for DUnique entities.
 *
 * Generated on 2013-07-11T16:57:11.736+0700.
 * @author mardao DAO generator (net.sf.mardao.plugin.ProcessDomainMojo)
 */
public interface GeneratedDUniqueDao extends Dao<DUnique, java.lang.Long> {

	/** Column name for primary key attribute is "_id" */
	static final String COLUMN_NAME__ID = "_id";


	/** Column name for field createdBy is "createdBy" */
	static final String COLUMN_NAME_CREATEDBY = "createdBy";
	/** Column name for field createdDate is "createdDate" */
	static final String COLUMN_NAME_CREATEDDATE = "createdDate";
	/** Column name for field email is "email" */
	static final String COLUMN_NAME_EMAIL = "email";
	/** Column name for field message is "message" */
	static final String COLUMN_NAME_MESSAGE = "message";
	/** Column name for field updatedBy is "updatedBy" */
	static final String COLUMN_NAME_UPDATEDBY = "updatedBy";
	/** Column name for field updatedDate is "updatedDate" */
	static final String COLUMN_NAME_UPDATEDDATE = "updatedDate";

	/** The list of attribute names */
	static final List<String> COLUMN_NAMES = Arrays.asList(		COLUMN_NAME_CREATEDBY,
		COLUMN_NAME_CREATEDDATE,
		COLUMN_NAME_EMAIL,
		COLUMN_NAME_MESSAGE,
		COLUMN_NAME_UPDATEDBY,
		COLUMN_NAME_UPDATEDDATE);
	/** The list of Basic attribute names */
	static final List<String> BASIC_NAMES = Arrays.asList(		COLUMN_NAME_CREATEDBY,
		COLUMN_NAME_CREATEDDATE,
		COLUMN_NAME_EMAIL,
		COLUMN_NAME_MESSAGE,
		COLUMN_NAME_UPDATEDBY,
		COLUMN_NAME_UPDATEDDATE);
	/** The list of attribute names */
	static final List<String> MANY_TO_ONE_NAMES = Arrays.asList();


	// ----------------------- field finders -------------------------------
	/**
	 * query-by method for field createdBy
	 * @param createdBy the specified attribute
	 * @return an Iterable of DUniques for the specified createdBy
	 */
	Iterable<DUnique> queryByCreatedBy(java.lang.String createdBy);
		
	/**
	 * query-keys-by method for field createdBy
	 * @param createdBy the specified attribute
	 * @return an Iterable of DUniques for the specified createdBy
	 */
	Iterable<java.lang.Long> queryKeysByCreatedBy(java.lang.String createdBy);

	/**
	 * query-page-by method for field createdBy
	 * @param createdBy the specified attribute
         * @param pageSize the number of domain entities in the page
         * @param cursorString non-null if get next page
	 * @return a Page of DUniques for the specified createdBy
	 */
	CursorPage<DUnique> queryPageByCreatedBy(java.lang.String createdBy,
            int pageSize, String cursorString);


	/**
	 * query-by method for field createdDate
	 * @param createdDate the specified attribute
	 * @return an Iterable of DUniques for the specified createdDate
	 */
	Iterable<DUnique> queryByCreatedDate(java.util.Date createdDate);
		
	/**
	 * query-keys-by method for field createdDate
	 * @param createdDate the specified attribute
	 * @return an Iterable of DUniques for the specified createdDate
	 */
	Iterable<java.lang.Long> queryKeysByCreatedDate(java.util.Date createdDate);

	/**
	 * query-page-by method for field createdDate
	 * @param createdDate the specified attribute
         * @param pageSize the number of domain entities in the page
         * @param cursorString non-null if get next page
	 * @return a Page of DUniques for the specified createdDate
	 */
	CursorPage<DUnique> queryPageByCreatedDate(java.util.Date createdDate,
            int pageSize, String cursorString);


	/**
	 * find-by method for unique field email
	 * @param email the unique attribute
	 * @return the unique DUnique for the specified email
	 */
	DUnique findByEmail(java.lang.String email);

        /**
	 * find-key-by method for unique attribute field email
	 * @param email the unique attribute
	 * @return the unique DUnique for the specified attribute
	 */
	java.lang.Long findKeyByEmail(java.lang.String email);

	/**
	 * query-by method for field message
	 * @param message the specified attribute
	 * @return an Iterable of DUniques for the specified message
	 */
	Iterable<DUnique> queryByMessage(java.lang.String message);
		
	/**
	 * query-keys-by method for field message
	 * @param message the specified attribute
	 * @return an Iterable of DUniques for the specified message
	 */
	Iterable<java.lang.Long> queryKeysByMessage(java.lang.String message);

	/**
	 * query-page-by method for field message
	 * @param message the specified attribute
         * @param pageSize the number of domain entities in the page
         * @param cursorString non-null if get next page
	 * @return a Page of DUniques for the specified message
	 */
	CursorPage<DUnique> queryPageByMessage(java.lang.String message,
            int pageSize, String cursorString);


	/**
	 * query-by method for field updatedBy
	 * @param updatedBy the specified attribute
	 * @return an Iterable of DUniques for the specified updatedBy
	 */
	Iterable<DUnique> queryByUpdatedBy(java.lang.String updatedBy);
		
	/**
	 * query-keys-by method for field updatedBy
	 * @param updatedBy the specified attribute
	 * @return an Iterable of DUniques for the specified updatedBy
	 */
	Iterable<java.lang.Long> queryKeysByUpdatedBy(java.lang.String updatedBy);

	/**
	 * query-page-by method for field updatedBy
	 * @param updatedBy the specified attribute
         * @param pageSize the number of domain entities in the page
         * @param cursorString non-null if get next page
	 * @return a Page of DUniques for the specified updatedBy
	 */
	CursorPage<DUnique> queryPageByUpdatedBy(java.lang.String updatedBy,
            int pageSize, String cursorString);


	/**
	 * query-by method for field updatedDate
	 * @param updatedDate the specified attribute
	 * @return an Iterable of DUniques for the specified updatedDate
	 */
	Iterable<DUnique> queryByUpdatedDate(java.util.Date updatedDate);
		
	/**
	 * query-keys-by method for field updatedDate
	 * @param updatedDate the specified attribute
	 * @return an Iterable of DUniques for the specified updatedDate
	 */
	Iterable<java.lang.Long> queryKeysByUpdatedDate(java.util.Date updatedDate);

	/**
	 * query-page-by method for field updatedDate
	 * @param updatedDate the specified attribute
         * @param pageSize the number of domain entities in the page
         * @param cursorString non-null if get next page
	 * @return a Page of DUniques for the specified updatedDate
	 */
	CursorPage<DUnique> queryPageByUpdatedDate(java.util.Date updatedDate,
            int pageSize, String cursorString);


		  
	// ----------------------- one-to-one finders -------------------------

	// ----------------------- many-to-one finders -------------------------
	
	// ----------------------- many-to-many finders -------------------------

	// ----------------------- uniqueFields finders -------------------------
	
	
	// ----------------------- populate / persist method -------------------------

	/**
	 * Persist an entity given all attributes
	 */
	DUnique persist(		java.lang.Long _id, 
		java.lang.String email, 
		java.lang.String message);	

	/**
	 * Persists an entity unless it already exists
	 */
	 DUnique persist(java.lang.String email, 
                java.lang.String message);

}
