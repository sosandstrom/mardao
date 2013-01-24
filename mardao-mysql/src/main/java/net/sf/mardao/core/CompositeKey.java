package net.sf.mardao.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author os
 */
public class CompositeKey implements Serializable {
    protected static final int TYPE_NULL = 0;
    protected static final int TYPE_LONG = 1;
    protected static final int TYPE_STRING = 2;
    
    private CompositeKey parentKey;
    private Long id;
    private String name;

    public CompositeKey(CompositeKey parentKey, Long id, String name) {
        this.parentKey = parentKey;
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s:%s/%d", parentKey, name, id);
    }

    public CompositeKey getParentKey() {
        return parentKey;
    }

    public void setParentKey(CompositeKey parentKey) {
        this.parentKey = parentKey;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public static String keyToString(CompositeKey key) {
        if (null == key) {
            return null;
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {

            writeKeyString(dos, key);
            
            dos.flush();
            final String s = Base64.encodeBase64URLSafeString(baos.toByteArray());
            return s;
        } catch (IOException ex) {
            throw new RuntimeException("Encoding Key " + key, ex);
        }
        finally {
            try {
                dos.close();
            } catch (IOException ex) {
            }
        }
    }

    protected static void writeKeyString(DataOutputStream dos, CompositeKey key) throws IOException {
        final boolean hasParent = null != key.parentKey;
        dos.writeBoolean(hasParent);
        if (hasParent) {
            writeKeyString(dos, key.parentKey);
        }

        final boolean isLong = null != key.id;
        if (isLong) {
            dos.writeByte(TYPE_LONG);
            dos.writeLong(key.id);
        }
        else {
            dos.writeByte(null != key.name ? TYPE_STRING : TYPE_NULL);
            if (null != key.name) {
                dos.writeUTF(key.name);
            }
        }
    }
    
    protected static CompositeKey readKeyString(DataInputStream dis) throws IOException {
        final boolean hasParent = dis.readBoolean();
        final CompositeKey parentKey = hasParent ? readKeyString(dis) : null;
        final int type = dis.readByte();
        final Long id = TYPE_LONG == type ? dis.readLong() : null;
        final String name = TYPE_STRING == type ? dis.readUTF() : null;
        
        final CompositeKey key = new CompositeKey(parentKey, id, name);
        return key;
    }
    
    public static CompositeKey stringToKey(String keyString) {
        if (null == keyString) {
            return null;
        }
        
        final ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decodeBase64(keyString));
        final DataInputStream dis = new DataInputStream(bais);
        
        final CompositeKey key;
        try {
            key = readKeyString(dis);
            return key;
        } catch (IOException ex) {
            throw new RuntimeException("Decoding keyString " + keyString, ex);
        }
        finally {
            try {
                dis.close();
            } catch (IOException ex) {
            }
        }
    }
}
