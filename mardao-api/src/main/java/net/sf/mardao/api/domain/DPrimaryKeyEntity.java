package net.sf.mardao.api.domain;

import java.io.Serializable;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public abstract class DPrimaryKeyEntity<ID extends Serializable> implements CreatedUpdatedEntity<ID> {

    /** Using slf4j logging */
    protected static final Logger LOG = LoggerFactory.getLogger(DPrimaryKeyEntity.class);

//    public String getKeyString() {
//        final Object pk = getPrimaryKey();
//        if (pk instanceof Key) {
//            return (null != pk) ? KeyFactory.keyToString((Key) pk) : null;
//        }
//        else if (pk instanceof String) {
//            return (String) pk;
//        }
//        else if (null != pk) {
//            return pk.toString();
//        }
//        return null;
//    }
    
    private Serializable primaryKey;
    
    private Serializable parentKey;

    public String toString() {
        return String.format("%s{simpleKey:%s", getClass().getSimpleName(), getSimpleKey());
    }

    public String getKind() {
        return getClass().getSimpleName();
    }

    public abstract Class<ID> getIdClass();

    public Serializable getParentKey() {
        return parentKey;
    }

    public void setParentKey(Object parentKey) {
        this.parentKey = (Serializable) parentKey;
    }

    public void setPrimaryKey(Object primaryKey) {
        this.primaryKey = (Serializable) primaryKey;
    }

    /**
     * 
     * @return a composite key using KeyFactory.createKey(getParentKey(), getKind(), getSimpleKey())
     */
    public final Serializable getPrimaryKey() {
        return primaryKey;
    }
//        final ID sk = getSimpleKey();
//        final Class<ID> idClass = getIdClass();
//
//        if (null == sk) {
//            return null;
//        }
//
//        // String ID
//        if (String.class.equals(idClass)) {
//            return KeyFactory.createKey((Key) getParentKey(), getKind(), (String) sk);
//        }
//
//        // Long ID
//        if (Long.class.equals(idClass)) {
//            return KeyFactory.createKey((Key) getParentKey(), getKind(), (Long) sk);
//        }
//
//        // Key ID
//        if (Key.class.equals(idClass)) {
//            return (Key) sk;
//        }
//
//        throw new UnsupportedOperationException("Unsupported primary key type: " + idClass.getName());
//    }

    public Date getCreatedDate() {
        return null;
    }

    public Date getUpdatedDate() {
        return null;
    }

    public String _getNameCreatedDate() {
        return null;
    }

    public String _getNameUpdatedDate() {
        return null;
    }

    public void _setCreatedBy(String name) {
    }

    public void _setCreatedDate(Date createdDate) {
    }

    public void _setUpdatedBy(String name) {
    }

    public void _setUpdatedDate(Date updatedDate) {
    }

//    public static void populate(Entity entity, String name, Object value) {
//        if (null != name && null != entity) {
//            if (null != value) {
//                // String properties must be 500 characters or less.
//                // Instead, use com.google.appengine.api.datastore.Text, which can store strings of any length.
//                if (value instanceof String) {
//                    final String s = (String) value;
//                    if (500 < s.length()) {
//                        value = new Text(s);
//                    }
//                }
//            }
//            entity.setProperty(name, value);
//        }
//    }
//
}
