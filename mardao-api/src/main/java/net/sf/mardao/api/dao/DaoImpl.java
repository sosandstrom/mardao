package net.sf.mardao.api.dao;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import net.sf.mardao.api.domain.CreatedUpdatedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * This is the base class for all implementations of the Dao Bean.
 * 
 * @author os
 * 
 * @param <T>
 *            domain object type
 * @param <ID>
 *            domain object simple key type
 * @param <P>
 *            domain object parent key type
 * @param <C>
 *            database core key type
 */
public abstract class DaoImpl<T extends CreatedUpdatedEntity, ID extends Serializable, P extends Serializable, E extends Serializable, C extends Serializable>
        implements Dao<T, ID, P, C> {

    /** Using slf4j logging */
    protected final Logger   LOG = LoggerFactory.getLogger(getClass());
    
    /** mostly for logging */
    protected final Class<T> persistentClass;

    protected DaoImpl(Class<T> type) {
        this.persistentClass = type;
    }

    /**
     * Converts a datastore Entity into the domain object. Implemented in Generated<T>DaoImpl
     * 
     * @param entity
     *            the datastore Entity
     * @return a domain object
     */
    protected abstract T createDomain(E entity);

    /**
     * Converts a datastore Key into the domain primary key. Implemented in Generated<T>DaoImpl
     * 
     * @param key
     *            the datastore Key
     * @return a domain primary key
     */
    protected abstract ID convert(C key);

    protected final List<ID> convert(List<C> keys) {
        final List<ID> ids = new ArrayList<ID>();
        ID id;
        for(C key : keys) {
            id = convert(key);
            ids.add(id);
        }
        return ids;
    }

    protected abstract E createEntity(ID primaryKey);

    protected E createEntity(Map<String, Object> nameValuePairs) {
        @SuppressWarnings("unchecked")
        ID primaryKey = (ID) nameValuePairs.get(getPrimaryKeyColumnName());
        final E entity = createEntity(primaryKey);
        populate(entity, nameValuePairs);
        return entity;
    }

    protected abstract E createEntity(T domain);

    protected List<E> createEntities(Iterable<T> domains) {
        final ArrayList<E> entities = new ArrayList<E>();
        for(T domain : domains) {
            entities.add(createEntity(domain));
        }
        return entities;
    }

    protected abstract Expression createEqualsFilter(String fieldName, Object param);

    protected abstract C createKey(T entity);

    public final C createKey(ID primaryKey) {
        return createKey(null, primaryKey);
    }

    @SuppressWarnings("unchecked")
    public final C createKey(Object parentKey, ID primaryKey) {
        return createKey((P) parentKey, primaryKey);
    }

    public final Iterable<C> createKeys(P parentKey, Iterable<ID> simpleKeys) {
        final List<C> returnValue = new ArrayList<C>();

        for(ID primaryKey : simpleKeys) {
            returnValue.add(createKey(parentKey, primaryKey));
        }

        return returnValue;
    }

    @SuppressWarnings("unchecked")
    public final Iterable<C> createKeys(Object parentKey, Iterable<ID> simpleKeys) {
        return createKeys((P) parentKey, simpleKeys);
    }

    public final Iterable<C> createKeys(Iterable<ID> primaryKeys) {
        return createKeys(null, primaryKeys);
    }

    public final void delete(T domain) {
        @SuppressWarnings("unchecked")
        final C key = (C) domain.getPrimaryKey();
        deleteByCore(key);
    }

    @SuppressWarnings("unchecked")
    public final void delete(Iterable<T> domains) {
        final List<C> keys = new ArrayList<C>();
        for(T domain : domains) {
            keys.add((C) domain.getPrimaryKey());
        }
        deleteByCore(keys);
    }

    public final void delete(ID key) {
        deleteByCore(createKey(key));
    }

    public final void delete(P parentKey, ID simpleKey) {
        deleteByCore(createKey(parentKey, simpleKey));
    }

    public final void delete(P parentKey, Iterable<ID> simpleKeys) {
        deleteByCore(createKeys(parentKey, simpleKeys));
    }

    @SuppressWarnings("unchecked")
    public void deleteByCore(C primaryKey) {
        deleteByCore(Arrays.asList(primaryKey));
    }

    public final void deleteByKeys(List<ID> primaryKeys) {
        deleteByCore(createKeys(primaryKeys));
    }

    public void deleteByParent(P parentKey) {
        deleteByCore(findCoreKeysByParent(parentKey));
    }

    // ----------------------- find methods -------------------------

    public List<T> findAll() {
        return findBy(null, false, -1, 0);
    }

    protected abstract T findBy(Expression... filters);

    protected abstract List<T> findBy(String orderBy, boolean ascending, int limit, int offset, P parentKey, Expression... filters);

    protected final List<T> findBy(String orderBy, boolean ascending, int limit, int offset, Expression... filters) {
        return findBy(orderBy, ascending, limit, offset, (P) null, filters);
    }

    protected List<T> findBy(String fieldName, Object param) {
        return findBy(null, false, -1, 0, createEqualsFilter(fieldName, param));
    }

//    protected List<T> findBy(Map<String, Object> args) {
//        return findBy(args, null, false, -1, 0);
//    }
//
    protected List<T> findBy(Map<String, Object> args, String orderBy, boolean ascending) {
        return findBy(args, orderBy, ascending, -1, 0);
    }

    protected List<T> findBy(String orderBy, boolean ascending, int limit, Expression... args) {
        return findBy(orderBy, ascending, limit, 0, args);
    }

    protected List<T> findBy(Map<String, Object> args, String orderBy, boolean ascending, String secondaryOrderBy,
            boolean secondaryDirection) {
        return findBy(args, orderBy, ascending, secondaryOrderBy, secondaryDirection, -1, 0);
    }

    protected List<T> findBy(Map<String, Object> filters, String orderBy, boolean ascending, int limit, int offset) {
        return findBy(filters, orderBy, ascending, null, false, limit, offset);
    }

    protected abstract List<T> findBy(Map<String, Object> filters, String primaryOrderBy, boolean primaryDirection,
            String secondaryOrderBy, boolean secondaryDirection, int limit, int offset);

    protected List<T> findByParent(P parentKey) {
        // TODO Auto-generated method stub
        return null;
    }

    public T findByPrimaryKey(ID primaryKey) {
        return findByPrimaryKey((P) null, primaryKey);
    }

    @SuppressWarnings("unchecked")
    public final T findByPrimaryKey(Object parentKey, ID primaryKey) {
        return findByPrimaryKey((P) parentKey, primaryKey);
    }

    public Map<ID, T> findByPrimaryKeys(Iterable<ID> primaryKeys) {
        return findByPrimaryKeys(null, primaryKeys);
    }

    @SuppressWarnings("unchecked")
    public final Map<ID, T> findByPrimaryKeys(Object parentKey, Iterable<ID> primaryKeys) {
        return findByPrimaryKeys((P) parentKey, primaryKeys);
    }

    protected abstract List<C> findCoreKeysByParent(P parentKey);

    protected abstract List<ID> findKeysBy(String orderBy, boolean ascending, int limit, int offset, Expression... filters);

    protected List<ID> findKeysBy(String fieldName, Object param) {
        return findKeysBy(null, false, -1, 0, createEqualsFilter(fieldName, param));
    }

    protected abstract List<ID> findKeysByParent(P parentKey);

    protected T findUniqueBy(String fieldName, Object param) {
        return findBy(createEqualsFilter(fieldName, param));
    }

    // ----------------------- persist methods -------------------------

    protected abstract C persistEntity(E entity);

    @SuppressWarnings("unchecked")
    public ID persist(T domain) {
        final List<ID> keys = persist(Arrays.asList(domain));
        return keys.get(0);
    }

    public T persist(Map<String, Object> nameValuePairs) {
        final E entity = createEntity(nameValuePairs);
        persistEntity(entity);
        return createDomain(entity);
    }

    protected abstract void persistUpdateDates(CreatedUpdatedEntity domain, E entity, Date date);

    protected abstract void persistUpdateKeys(T domain, C key);

    private final void persistUpdateKeys(Iterable<T> domains, Iterable<C> keys) {
        final Iterator<T> i = domains.iterator();

        for(C key : keys) {
            persistUpdateKeys(i.next(), key);
        }
    }

    protected abstract void populate(E entity, Map<String, Object> nameValuePairs);

    public ID update(T domain) {
        @SuppressWarnings("unchecked")
        final List<ID> keys = update(Arrays.asList(domain));
        return keys.get(0);
    }

    public final List<ID> update(Iterable<T> domains) {
        List<E> entities = new ArrayList<E>();
        final Date date = new Date();
        for(T domain : domains) {
            final E entity = createEntity(domain);
            persistUpdateDates(domain, entity, date);
            entities.add(entity);
        }
        List<C> keys = updateByCore(entities);
        persistUpdateKeys(domains, keys);
        return convert(keys);
    }

    protected abstract List<C> updateByCore(Iterable<E> entities);
    
    // ----------------------- XML marshalling methods -------------------------

    /**
     * Override to convert more types. This method supports Date
     * @param value
     * @return 
     */
    protected String xmlFieldValue(Object value) {
        if (null == value) {
            return null;
        }
        
        if (value instanceof Date) {
            return Long.toString(((Date)value).getTime());
        }
        return value.toString();
    }
    
    public static final String NAMESPACE_URI = "http://mardao.sf.net/xml1";
    public static final String FIELD_NAME_PARENT = "_parent";
    public static final String TAG_FIELD = "field";
    public static final String TAG_VALUE = "value";
    
    protected void xmlGenerateMultiValue(ContentHandler ch, Object value) throws SAXException {
        if (null != value) {
            final AttributesImpl atts = new AttributesImpl();
            atts.addAttribute(NAMESPACE_URI, "", "value", "String", xmlFieldValue(value));
            ch.startElement(NAMESPACE_URI, "", TAG_VALUE, atts);
            ch.endElement(NAMESPACE_URI, "", TAG_VALUE);
        }
    }

    public static final char CDATA_BEGIN[] = "<![CDATA[".toCharArray();
    public static final char CDATA_END[] = "]]>".toCharArray();
    protected void xmlGenerateFieldCData(ContentHandler ch, String name, String value) throws SAXException {
        if (null != value) {
            final AttributesImpl atts = new AttributesImpl();
            atts.addAttribute(NAMESPACE_URI, "", "name", "String", name);
            ch.startElement(NAMESPACE_URI, "", TAG_FIELD, atts);
            ch.characters(CDATA_BEGIN, 0, CDATA_BEGIN.length);
            final char v[] = value.toCharArray();
            ch.characters(v, 0, v.length);
            ch.characters(CDATA_END, 0, CDATA_END.length);
            ch.endElement(NAMESPACE_URI, "", TAG_FIELD);
        }
    }
    
    protected void xmlGenerateField(ContentHandler ch, String name, Object value) throws SAXException {
        if (null != value) {
            final AttributesImpl atts = new AttributesImpl();
            atts.addAttribute(NAMESPACE_URI, "", "name", "String", name);
            if (value instanceof Collection) {
                ch.startElement(NAMESPACE_URI, "", TAG_FIELD, atts);
                for (Object v : (Collection)value) {
                    xmlGenerateMultiValue(ch, v);
                }
            }
            else {
                atts.addAttribute(NAMESPACE_URI, "", "value", "", xmlFieldValue(value));
                ch.startElement(NAMESPACE_URI, "", TAG_FIELD, atts);
            }
            ch.endElement(NAMESPACE_URI, "", TAG_FIELD);
        }
    }
    
    /**
     * Override this method to generate the domain object's properties.
     * This implementation adds the createdDate and updatedDate properties.
     * @param ch
     * @param domain
     * @throws SAXException 
     */
    protected void xmlGenerateFields(ContentHandler ch, T domain) throws SAXException {
        
        final Date createdDate = domain.getCreatedDate();
        if (null != domain._getNameCreatedDate() && null != createdDate) {
            xmlGenerateField(ch, domain._getNameCreatedDate(), createdDate);
        }
        
        final Date updatedDate = domain.getUpdatedDate();
        if (null != domain._getNameUpdatedDate() && null != updatedDate) {
            xmlGenerateField(ch, domain._getNameUpdatedDate(), updatedDate);
        }
    }

    public static final String TAG_ENTITY = "entity";
    protected void xmlGenerateEntity(ContentHandler ch, T domain) throws SAXException {
        final String id = xmlFieldValue(domain.getSimpleKey());
//        LOG.info("      entity id={}", id);
                
        final AttributesImpl atts = new AttributesImpl();
        atts.addAttribute(NAMESPACE_URI, "", "id", "", id);
        atts.addAttribute(NAMESPACE_URI, "", "class", "String", domain.getClass().getName());
        ch.startElement(NAMESPACE_URI, "", TAG_ENTITY, atts);
        xmlGenerateFields(ch, domain);
        ch.endElement(NAMESPACE_URI, "", TAG_ENTITY);
    }

    public static final String TAG_ENTITIES = "entities";
    @Override
    public void xmlGenerateEntities(Writer writer, Object appArg0, Iterable<T> cursor) throws SAXException, IOException, TransformerConfigurationException {
        LOG.debug("   Entities for {}", getTableName());

        SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactory.newInstance();
        TransformerHandler ch = factory.newTransformerHandler();

        ch.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
        ch.getTransformer().setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        final StreamResult streamResult = new StreamResult(writer);
        ch.setResult(streamResult);

        ch.startDocument();
        final AttributesImpl atts = new AttributesImpl();
        ch.startElement(NAMESPACE_URI, "", TAG_ENTITIES, atts);
        for (T domain : cursor) {
            xmlGenerateEntity(ch, domain);
        }
        ch.endElement(NAMESPACE_URI, "", TAG_ENTITIES);
        ch.endDocument();
    }
    
    /**
     * Override to not return findAll()
     * @return findAll()
     */
    public Iterable<T> xmlFindAll() {
        return findAll();
    }
    
    public static void xmlGenerateEntityDaos(Writer writer, Object appArg0, Dao... daos) throws SAXException, IOException, TransformerConfigurationException {
        for (Dao dao : daos) {
            dao.xmlGenerateEntities(writer, appArg0, dao.xmlFindAll());
        }
    }

}
