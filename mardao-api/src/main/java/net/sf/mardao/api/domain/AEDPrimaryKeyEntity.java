package net.sf.mardao.api.domain;

import java.io.Serializable;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public abstract class AEDPrimaryKeyEntity<ID extends Serializable> implements PrimaryKeyEntity, CreatedUpdatedEntity {

    /** Using slf4j logging */
    protected static final Logger LOG = LoggerFactory.getLogger(AEDPrimaryKeyEntity.class);

    public String getKeyString() {
        final Object pk = getPrimaryKey();
        if (pk instanceof Key) {
            return (null != pk) ? KeyFactory.keyToString((Key) pk) : null;
        }
        else if (pk instanceof String) {
            return (String) pk;
        }
        else if (null != pk) {
            return pk.toString();
        }
        return null;
    }

    public String toString() {
        return getClass().getSimpleName() + ',' + getPrimaryKey();
    }

    public String getKind() {
        return getClass().getSimpleName();
    }

    public abstract ID getSimpleKey();

    public abstract Class<ID> getIdClass();

    public Object getParentKey() {
        return null;
    }

    /**
     * 
     * @return a composite key using KeyFactory.createKey(getParentKey(), getKind(), getSimpleKey())
     */
    public final Key getPrimaryKey() {
        final ID sk = getSimpleKey();
        final Class<ID> idClass = getIdClass();

        LOG.debug("{}/{} for " + idClass.getSimpleName(), getParentKey(), sk);

        // String ID
        if (String.class.equals(idClass)) {
            return KeyFactory.createKey((Key) getParentKey(), getKind(), (String) sk);
        }

        // Long ID
        if (Long.class.equals(idClass)) {
            return KeyFactory.createKey((Key) getParentKey(), getKind(), (Long) sk);
        }

        // Key ID
        if (Key.class.equals(idClass)) {
            return (Key) sk;
        }

        throw new UnsupportedOperationException("Unsupported primary key type: " + idClass.getName());
    }

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

    public static void populate(Entity entity, String name, Object value) {
        if (null != value) {
            // String properties must be 500 characters or less.
            // Instead, use com.google.appengine.api.datastore.Text, which can store strings of any length.
            if (value instanceof String) {
                final String s = (String) value;
                if (500 < s.length()) {
                    value = new Text(s);
                }
            }
        }
        entity.setProperty(name, value);
    }

    public Entity _createEntity() {
        final ID sk = getSimpleKey();
        if (null == sk) {
            final Key pk = (Key) getParentKey();
            if (null == pk) {
                return new Entity(getKind());
            }
            return new Entity(getKind(), pk);
        }
        return new Entity(getPrimaryKey());
    }
}
