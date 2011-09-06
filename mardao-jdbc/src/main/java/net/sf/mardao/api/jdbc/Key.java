package net.sf.mardao.api.jdbc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public final class Key implements Serializable {
    private static final long   serialVersionUID = 3389855762666829697L;

    protected static final byte TYPE_STRING      = '/';
    protected static final byte TYPE_LONG        = ',';
    protected static final char SEPARATOR        = '.';

    private final Key           parentKey;
    private final String        kind;
    private final String        name;
    private final Long          id;

    protected Key(Key parentKey, String kind, String name, Long id) {
        this.parentKey = parentKey;
        this.kind = kind;
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        final Key other = (Key) obj;
        if ((null == kind) != (null == other.kind)) {
            return false;
        }

        if ((null == name) != (null == other.name)) {
            return false;
        }

        if ((null == id) != (null == other.id)) {
            return false;
        }

        if ((null == parentKey) != (null == other.parentKey)) {
            return false;
        }

        if (false == kind.equals(other.kind)) {
            return false;
        }

        if (null != id) {
            return id.equals(other.id);
        }

        if (false == name.equals(other.name)) {
            return false;
        }

        return null == parentKey || parentKey.equals(other.parentKey);
    }

    public final Long getId() {
        return id;
    }

    public final String getKind() {
        return kind;
    }

    public final String getName() {
        return name;
    }

    public final Key getParentKey() {
        return parentKey;
    }

    public String keyString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        String s = null;
        try {
            dos.writeUTF(kind);
            if (null != name) {
                dos.writeByte(TYPE_STRING);
                dos.writeUTF(name);
            }
            else {
                dos.writeByte(TYPE_LONG);
                dos.writeLong(id);
            }
            s = new String(Base64.encodeBase64(baos.toByteArray(), false, true));
        }
        catch (UnsupportedEncodingException willNeverHappen) {
        }
        catch (IOException willNeitherHappen) {
        }
        return String.format("%s%s", null != parentKey ? parentKey.keyString() + SEPARATOR : "", s);
    }

    @Override
    public String toString() {
        return String.format("%s/%s(%s)", null != parentKey ? parentKey.toString() : "", kind, null != name ? name : id);
    }

}
