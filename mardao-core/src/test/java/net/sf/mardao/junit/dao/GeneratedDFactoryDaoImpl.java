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
import net.sf.mardao.junit.domain.DFactory;


/**
 * The DFactory domain-object specific finders and methods go in this POJO.
 * 
 * Generated on 2015-02-28T10:34:05.595+0100.
 * @author mardao DAO generator (net.sf.mardao.plugin.ProcessDomainMojo)
 */
public class GeneratedDFactoryDaoImpl
  extends AbstractDao<DFactory, java.lang.String> {

  public GeneratedDFactoryDaoImpl(Supplier<Object, Object, Object, Object> supplier) {
    super(new DFactoryMapper(supplier), supplier);
  }

// ----------------------- Caching -------------------------------------


// ----------------------- field finders -------------------------------

// ----------------------- DFactory builder -------------------------------

  public static DFactoryMapper.Builder newBuilder() {
    return DFactoryMapper.newBuilder();
  }

}
