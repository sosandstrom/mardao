package net.sf.mardao.junit.dao;

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
import net.sf.mardao.junit.domain.DUser;


/**
 * The DUser domain-object specific finders and methods go in this POJO.
 * 
 * Generated on 2015-02-28T10:34:05.595+0100.
 * @author mardao DAO generator (net.sf.mardao.plugin.ProcessDomainMojo)
 */
public class GeneratedDUserDaoImpl
  extends AbstractDao<DUser, java.lang.Long> {

  public GeneratedDUserDaoImpl(Supplier<Object, Object, Object, Object> supplier) {
    super(new DUserMapper(supplier), supplier);
  }

// ----------------------- Caching -------------------------------------


// ----------------------- field finders -------------------------------
  /**
   * query-by method for field birthDate
   * @param birthDate the specified attribute
   * @return an Iterable of DUsers for the specified birthDate
   */
  public Iterable<DUser> queryByBirthDate(java.util.Date birthDate) {
    return queryByField(null, DUserMapper.Field.BIRTHDATE.getFieldName(), birthDate);
  }

  /**
   * query-page-by method for field birthDate
   * @param birthDate the specified attribute
   * @return a CursorPage of DUsers for the specified birthDate
   */
  public CursorPage<DUser> queryPageByBirthDate(java.util.Date birthDate,
      int requestedPageSize, String cursorString) {
    return queryPageByField(null, DUserMapper.Field.BIRTHDATE.getFieldName(), birthDate,
      requestedPageSize, cursorString);
  }

  /**
   * query-by method for field createdBy
   * @param createdBy the specified attribute
   * @return an Iterable of DUsers for the specified createdBy
   */
  public Iterable<DUser> queryByCreatedBy(java.lang.String createdBy) {
    return queryByField(null, DUserMapper.Field.CREATEDBY.getFieldName(), createdBy);
  }

  /**
   * query-page-by method for field createdBy
   * @param createdBy the specified attribute
   * @return a CursorPage of DUsers for the specified createdBy
   */
  public CursorPage<DUser> queryPageByCreatedBy(java.lang.String createdBy,
      int requestedPageSize, String cursorString) {
    return queryPageByField(null, DUserMapper.Field.CREATEDBY.getFieldName(), createdBy,
      requestedPageSize, cursorString);
  }

  /**
   * query-by method for field displayName
   * @param displayName the specified attribute
   * @return an Iterable of DUsers for the specified displayName
   */
  public Iterable<DUser> queryByDisplayName(java.lang.String displayName) {
    return queryByField(null, DUserMapper.Field.DISPLAYNAME.getFieldName(), displayName);
  }

  /**
   * query-page-by method for field displayName
   * @param displayName the specified attribute
   * @return a CursorPage of DUsers for the specified displayName
   */
  public CursorPage<DUser> queryPageByDisplayName(java.lang.String displayName,
      int requestedPageSize, String cursorString) {
    return queryPageByField(null, DUserMapper.Field.DISPLAYNAME.getFieldName(), displayName,
      requestedPageSize, cursorString);
  }

  /**
   * find-by method for unique field email
   * @param email the unique attribute
   * @return the unique DUser for the specified email
   */
  public DUser findByEmail(java.lang.String email) {
    return queryUniqueByField(null, DUserMapper.Field.EMAIL.getFieldName(), email);
  }


// ----------------------- DUser builder -------------------------------

  public static DUserMapper.Builder newBuilder() {
    return DUserMapper.newBuilder();
  }

}
