package net.sf.mardao.api.domain;

public abstract class AEDPrimaryKeyEntity implements PrimaryKeyEntity {
    public String getKeyString() {
    	final Object pk = getPrimaryKey();
    	if ("com.google.appengine.api.datastore.Key".equals(pk.getClass().getName())) {
        	return (null != pk) ? com.google.appengine.api.datastore.KeyFactory.keyToString(
        			(com.google.appengine.api.datastore.Key) pk) : null;
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
}
