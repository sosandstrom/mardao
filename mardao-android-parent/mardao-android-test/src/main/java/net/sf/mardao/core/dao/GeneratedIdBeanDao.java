package net.sf.mardao.core.dao;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.dao.Dao;
import net.sf.mardao.core.domain.IdBean;
import net.sf.mardao.core.geo.DLocation;

/**
 * DAO interface with finder methods for IdBean entities.
 *
 * Generated on 2013-07-11T16:57:11.736+0700.
 * @author mardao DAO generator (net.sf.mardao.plugin.ProcessDomainMojo)
 */
public interface GeneratedIdBeanDao extends Dao<IdBean, java.lang.Long> {

	/** Column name for primary key attribute is "_id" */
	static final String COLUMN_NAME__ID = "_id";


	/** Column name for field message is "message" */
	static final String COLUMN_NAME_MESSAGE = "message";

	/** The list of attribute names */
	static final List<String> COLUMN_NAMES = Arrays.asList(		COLUMN_NAME_MESSAGE);
	/** The list of Basic attribute names */
	static final List<String> BASIC_NAMES = Arrays.asList(		COLUMN_NAME_MESSAGE);
	/** The list of attribute names */
	static final List<String> MANY_TO_ONE_NAMES = Arrays.asList();


	// ----------------------- field finders -------------------------------
	/**
	 * query-by method for field message
	 * @param message the specified attribute
	 * @return an Iterable of IdBeans for the specified message
	 */
	Iterable<IdBean> queryByMessage(java.lang.String message);
		
	/**
	 * query-keys-by method for field message
	 * @param message the specified attribute
	 * @return an Iterable of IdBeans for the specified message
	 */
	Iterable<java.lang.Long> queryKeysByMessage(java.lang.String message);

	/**
	 * query-page-by method for field message
	 * @param message the specified attribute
         * @param pageSize the number of domain entities in the page
         * @param cursorString non-null if get next page
	 * @return a Page of IdBeans for the specified message
	 */
	CursorPage<IdBean> queryPageByMessage(java.lang.String message,
            int pageSize, String cursorString);


		  
	// ----------------------- one-to-one finders -------------------------

	// ----------------------- many-to-one finders -------------------------
	
	// ----------------------- many-to-many finders -------------------------

	// ----------------------- uniqueFields finders -------------------------
	
	
	// ----------------------- populate / persist method -------------------------

	/**
	 * Persist an entity given all attributes
	 */
	IdBean persist(		java.lang.Long _id);	

}
