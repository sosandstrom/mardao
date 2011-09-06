package net.sf.mardao.api.jdbc;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

public final class KeyFactory {

    public static Key createKey(Key parentKey, String kind, String name) {
        return new Key(parentKey, kind, name, null);
    }

    public static Key createKey(Key parentKey, String kind, Long id) {
        return new Key(parentKey, kind, null, id);
    }

    public static Key createKey(String kind, String name) {
        return new Key(null, kind, name, null);
    }

    public static Key createKey(String kind, Long id) {
        return new Key(null, kind, null, id);
    }

    public static Key parse(String keyString) {
        if (null == keyString) {
            return null;
        }
    
        Key parentKey = null;
        int index = keyString.lastIndexOf(Key.SEPARATOR);
        if (-1 < index) {
            parentKey = parse(keyString.substring(0, index));
        }
    
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decodeBase64(keyString.substring(index + 1)));
        DataInputStream dis = new DataInputStream(bais);
    
        try {
            String kind = dis.readUTF();
            byte type = dis.readByte();
            switch (type) {
                case Key.TYPE_STRING:
                    return createKey(parentKey, kind, dis.readUTF());
                case Key.TYPE_LONG:
                    return createKey(parentKey, kind, dis.readLong());
                default:
                    throw new IllegalArgumentException("Unsupported type: " + type);
            }
        }
        catch (IOException shouldNeverHappen) {
            throw new IllegalArgumentException("Cannot parse " + keyString, shouldNeverHappen);
        }
    }

}
