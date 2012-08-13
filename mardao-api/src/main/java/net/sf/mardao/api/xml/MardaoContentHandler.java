package net.sf.mardao.api.xml;

import java.util.Map;
import java.util.Properties;
import net.sf.mardao.api.dao.Dao;
import net.sf.mardao.api.dao.DaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author os
 */
public class MardaoContentHandler extends DefaultHandler {
    static final Logger LOG = LoggerFactory.getLogger(MardaoContentHandler.class);
    
    private final Map<String, Dao> daoMap;
    private Dao currentDao;
    private Properties properties;
    private String name;

    public MardaoContentHandler(Map<String, Dao> daoMap, String blobUrl) {
        this.daoMap = daoMap;
        LOG.info("Persisting for blob {}", blobUrl);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            if (DaoImpl.TAG_ENTITY.equals(qName)) {
                    final String cl = attributes.getValue(DaoImpl.ATTR_CLASS);
                    final Class clazz = getClass().getClassLoader().loadClass(cl);
                    currentDao = daoMap.get(clazz.getSimpleName());
                    if (null == currentDao) {
                        throw new SAXException("No Dao for " + clazz.getSimpleName());
                    }
                    properties = new Properties();
                    properties.setProperty(DaoImpl.ATTR_ID, attributes.getValue(DaoImpl.ATTR_ID));
            }
            else if (DaoImpl.TAG_FIELD.equals(qName)) {
                name = attributes.getValue(DaoImpl.ATTR_NAME);
                final String value = attributes.getValue(DaoImpl.ATTR_VALUE);
                if (null != value) {
                    properties.setProperty(name, value);
                }
            }
            else if (DaoImpl.TAG_VALUE.equals(qName)) {
                final String value = attributes.getValue(DaoImpl.ATTR_VALUE);
                if (null != value) {
                    properties.setProperty(name, value);
                }
            }
            else {
                LOG.warn("startElement {},{}", localName, qName);
            }
        } catch (ClassNotFoundException ex) {
            throw new SAXException("Class not found", ex);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (DaoImpl.TAG_ENTITY.equals(qName)) {
            currentDao.xmlPersistEntity(properties);
        }
    }
    
    
}
