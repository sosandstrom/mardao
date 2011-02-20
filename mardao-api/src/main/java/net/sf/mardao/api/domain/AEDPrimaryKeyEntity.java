package net.sf.mardao.api.domain;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public abstract class AEDPrimaryKeyEntity implements PrimaryKeyEntity {

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

    public abstract Object getSimpleKey();

    public Object getParentKey() {
        return null;
    }

    /**
     * 
     * @return a composite key using KeyFactory.createKey(getParentKey(), getKind(), getSimpleKey())
     */
    public final Object getPrimaryKey() {
        final Object sk = getSimpleKey();
        if (sk instanceof String) {
            return KeyFactory.createKey((Key) getParentKey(), getKind(), (String) sk);
        }
        else if (sk instanceof Long) {
            return KeyFactory.createKey((Key) getParentKey(), getKind(), (Long) sk);
        }
        return sk;
    }
}
