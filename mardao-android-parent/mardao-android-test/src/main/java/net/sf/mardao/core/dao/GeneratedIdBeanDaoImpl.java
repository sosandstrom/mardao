package net.sf.mardao.core.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.Filter;
import net.sf.mardao.core.dao.DaoImpl;
import net.sf.mardao.core.dao.TypeDaoImpl;
import net.sf.mardao.core.geo.DLocation;
import net.sf.mardao.core.domain.IdBean;

/**
 * The IdBean domain-object specific finders and methods go in this POJO.
 * 
 * Generated on 2013-04-09T16:13:40.169+0700.
 * @author mardao DAO generator (net.sf.mardao.plugin.ProcessDomainMojo)
 */
public class GeneratedIdBeanDaoImpl extends TypeDaoImpl<IdBean, java.lang.Long> 
	implements GeneratedIdBeanDao {


    /** to list the property names for ManyToOne relations */
    @Override
    protected List<String> getBasicColumnNames() {
        return BASIC_NAMES;
    }

    /** to list the property names for ManyToOne relations */
    @Override
    protected List<String> getManyToOneColumnNames() {
        return MANY_TO_ONE_NAMES;
    }

    private final Map<String, DaoImpl> MANY_TO_ONE_DAOS = new TreeMap<String, DaoImpl>();

    /** Default constructor */
   public GeneratedIdBeanDaoImpl() {
      super(IdBean.class, java.lang.Long.class);
   }

   // ------ BEGIN DaoImpl overrides -----------------------------
   
   public String getPrimaryKeyColumnName() {
   		return COLUMN_NAME__ID;
   }
   
   public List<String> getColumnNames() {
        return COLUMN_NAMES;
   }

   @Override
   protected DaoImpl getManyToOneDao(String columnName) {
       return MANY_TO_ONE_DAOS.get(columnName);
   }

    @Override
    protected Object getDomainProperty(IdBean domain, String name) {
        Object value;
        // simple key?
        if (COLUMN_NAME__ID.equals(name)) {
            value = domain.get_id();
        }
        // fields
        else if (COLUMN_NAME_MESSAGE.equals(name)) {
            value = domain.getMessage();
        }
        // one-to-ones
        // many-to-ones
        // many-to-manys
        else {
            value = super.getDomainProperty(domain, name);
        }

        return value;
    }

    /**
     * Returns the class of the domain property for specified column
     * @param name
     * @return the class of the domain property
     */
    public Class getColumnClass(String name) {
        Class clazz;
        // simple key?
        if (COLUMN_NAME__ID.equals(name)) {
            clazz = java.lang.Long.class;
        }
        // fields
        else if (COLUMN_NAME_MESSAGE.equals(name)) {
            clazz = java.lang.String.class;
        }
        // one-to-ones
        // many-to-ones
        // many-to-manys
        else {
            throw new IllegalArgumentException("No such column " + name);
        }

        return clazz;
    }
      
    @Override
    protected void setDomainProperty(final IdBean domain, final String name, final Object value) {
        // simple key?
        if (COLUMN_NAME__ID.equals(name)) {
            domain.set_id((java.lang.Long) value);
        }
        // fields
        else if (COLUMN_NAME_MESSAGE.equals(name)) {
            domain.setMessage((java.lang.String) value);
        }
        // one-to-ones
        // many-to-ones
        // many-to-manys
        else {
            super.setDomainProperty(domain, name, value);
        }
    }

    @Override
    protected void setDomainStringProperty(final IdBean domain, final String name, final Map<String, String> properties) {
        final String value = properties.get(name);
        Class clazz = getColumnClass(name);
        // many-to-ones
        setDomainProperty(domain, name, parseProperty(value, clazz));
    }

    /**
     * Overrides to substitute Entity properties with foreign keys
     */
    @Override
    protected void setCoreProperty(Object core, String name, Object value) {
        if (null == core || null == name) {
            return;
        }
        else if (null == value) {
            // do nothing in particular, will call super at end
        }
        super.setCoreProperty(core, name, value);
    }

   // ------ END DaoImpl overrides -----------------------------

        // IdBean has no parent

        /**
         * @return the simple key for specified IdBean domain object
         */
        public Long getSimpleKey(IdBean domain) {
            if (null == domain) {
                return null;
            }
            return domain.get_id();
        }

        /**
         * @return the simple key for specified IdBean domain object
         */
        public void setSimpleKey(IdBean domain, Long _id) {
            domain.set_id(_id);
        }

	// ----------------------- field finders -------------------------------
	/**
         * {@inheritDoc}
	 */
	public final Iterable<IdBean> queryByMessage(java.lang.String message) {
            final Filter filter = createEqualsFilter(COLUMN_NAME_MESSAGE, message);
            return queryIterable(false, 0, -1, null, null, null, false, null, false, filter);
	}
	
	/**
	 * query-key-by method for attribute field message
	 * @param message the specified attribute
	 * @return an Iterable of keys to the IdBeans with the specified attribute
	 */
	public final Iterable<java.lang.Long> queryKeysByMessage(java.lang.String message) {
            final Filter filter = createEqualsFilter(COLUMN_NAME_MESSAGE, message);
            return queryIterableKeys(0, -1, null, null, null, false, null, false, filter);
	}

	/**
	 * query-page-by method for field message
	 * @param message the specified attribute
         * @param pageSize the number of domain entities in the page
         * @param cursorString non-null if get next page
	 * @return a Page of IdBeans for the specified message
	 */
	public final CursorPage<IdBean, java.lang.Long> queryPageByMessage(java.lang.String message,
            int pageSize, String cursorString) {
            final Filter filter = createEqualsFilter(COLUMN_NAME_MESSAGE, message);
            return queryPage(false, pageSize, null, null, null, false, null, false, cursorString, filter);
        }

	 
	// ----------------------- one-to-one finders -------------------------

	// ----------------------- many-to-one finders -------------------------

	// ----------------------- many-to-many finders -------------------------

	// ----------------------- uniqueFields finders -------------------------

	// ----------------------- populate / persist method -------------------------

	/**
	 * Persist an entity given all attributes
	 */
	public IdBean persist(		java.lang.Long _id) {

            IdBean domain = null;
            // if primaryKey specified, use it
            if (null != _id) {
                    domain = findByPrimaryKey(_id);
            }
		
            // create new?
            if (null == domain) {
                    domain = new IdBean();
                    // generate Id?
                    if (null != _id) {
                            domain.set_id(_id);
                    }
                    // fields
                    // one-to-ones
                    // many-to-ones
			
                    persist(domain);
            }
            return domain;
	}



}
