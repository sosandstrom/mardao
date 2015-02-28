package net.sf.mardao.test.dao;

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
import net.sf.mardao.core.filter.Filter;
import net.sf.mardao.core.geo.DLocation;
import net.sf.mardao.dao.AbstractDao;
import net.sf.mardao.dao.Supplier;
import net.sf.mardao.test.domain.DBasic;


/**
 * The DBasic domain-object specific finders and methods go in this POJO.
 * 
 * Generated on 2015-02-27T21:08:08.487+0100.
 * @author mardao DAO generator (net.sf.mardao.plugin.ProcessDomainMojo)
 */
public class GeneratedDBasicDaoImpl
  extends AbstractDao<DBasic, java.lang.Long> {

  public GeneratedDBasicDaoImpl(Supplier<Object, Object, Object, Object> supplier) {
    super(new DBasicMapper(supplier), supplier);
  }

// ----------------------- Caching -------------------------------------


// ----------------------- field finders -------------------------------
  /**
   * query-by method for field createdBy
   * @param createdBy the specified attribute
   * @return an Iterable of DBasics for the specified createdBy
   */
  public Iterable<DBasic> queryByCreatedBy(java.lang.String createdBy) {
    return queryByField(null, DBasicMapper.Field.CREATEDBY.getFieldName(), createdBy);
  }

  /**
   * query-page-by method for field createdBy
   * @param createdBy the specified attribute
   * @return a CursorPage of DBasics for the specified createdBy
   */
  public CursorPage<DBasic> queryPageByCreatedBy(java.lang.String createdBy,
      int requestedPageSize, String cursorString) {
    return queryPageByField(null, DBasicMapper.Field.CREATEDBY.getFieldName(), createdBy,
      requestedPageSize, cursorString);
  }

  /**
   * query-by method for field createdDate
   * @param createdDate the specified attribute
   * @return an Iterable of DBasics for the specified createdDate
   */
  public Iterable<DBasic> queryByCreatedDate(java.util.Date createdDate) {
    return queryByField(null, DBasicMapper.Field.CREATEDDATE.getFieldName(), createdDate);
  }

  /**
   * query-page-by method for field createdDate
   * @param createdDate the specified attribute
   * @return a CursorPage of DBasics for the specified createdDate
   */
  public CursorPage<DBasic> queryPageByCreatedDate(java.util.Date createdDate,
      int requestedPageSize, String cursorString) {
    return queryPageByField(null, DBasicMapper.Field.CREATEDDATE.getFieldName(), createdDate,
      requestedPageSize, cursorString);
  }

  /**
   * query-by method for field displayName
   * @param displayName the specified attribute
   * @return an Iterable of DBasics for the specified displayName
   */
  public Iterable<DBasic> queryByDisplayName(java.lang.String displayName) {
    return queryByField(null, DBasicMapper.Field.DISPLAYNAME.getFieldName(), displayName);
  }

  /**
   * query-page-by method for field displayName
   * @param displayName the specified attribute
   * @return a CursorPage of DBasics for the specified displayName
   */
  public CursorPage<DBasic> queryPageByDisplayName(java.lang.String displayName,
      int requestedPageSize, String cursorString) {
    return queryPageByField(null, DBasicMapper.Field.DISPLAYNAME.getFieldName(), displayName,
      requestedPageSize, cursorString);
  }

  /**
   * query-by method for field updatedBy
   * @param updatedBy the specified attribute
   * @return an Iterable of DBasics for the specified updatedBy
   */
  public Iterable<DBasic> queryByUpdatedBy(java.lang.String updatedBy) {
    return queryByField(null, DBasicMapper.Field.UPDATEDBY.getFieldName(), updatedBy);
  }

  /**
   * query-page-by method for field updatedBy
   * @param updatedBy the specified attribute
   * @return a CursorPage of DBasics for the specified updatedBy
   */
  public CursorPage<DBasic> queryPageByUpdatedBy(java.lang.String updatedBy,
      int requestedPageSize, String cursorString) {
    return queryPageByField(null, DBasicMapper.Field.UPDATEDBY.getFieldName(), updatedBy,
      requestedPageSize, cursorString);
  }

  /**
   * query-by method for field updatedDate
   * @param updatedDate the specified attribute
   * @return an Iterable of DBasics for the specified updatedDate
   */
  public Iterable<DBasic> queryByUpdatedDate(java.util.Date updatedDate) {
    return queryByField(null, DBasicMapper.Field.UPDATEDDATE.getFieldName(), updatedDate);
  }

  /**
   * query-page-by method for field updatedDate
   * @param updatedDate the specified attribute
   * @return a CursorPage of DBasics for the specified updatedDate
   */
  public CursorPage<DBasic> queryPageByUpdatedDate(java.util.Date updatedDate,
      int requestedPageSize, String cursorString) {
    return queryPageByField(null, DBasicMapper.Field.UPDATEDDATE.getFieldName(), updatedDate,
      requestedPageSize, cursorString);
  }


// ----------------------- DBasic builder -------------------------------

  public static DBasicMapper.Builder newBuilder() {
    return DBasicMapper.newBuilder();
  }

}
