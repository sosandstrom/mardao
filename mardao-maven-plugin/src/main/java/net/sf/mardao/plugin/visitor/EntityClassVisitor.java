package net.sf.mardao.plugin.visitor;

import java.util.Map;

import net.sf.mardao.domain.Entity;
import net.sf.mardao.domain.Field;

import org.apache.maven.plugin.logging.Log;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;

public class EntityClassVisitor extends EmptyClassVisitor {

    public static final int           ACCESS_FINAL   = 0x10;
    public static final int           ACCESS_STATIC  = 0x08;
    public static final int           ACCESS_PRIVATE = 0x02;

    // private Group group;
    private final Entity              entity;
    private final Log                 LOG;
    private final Map<String, Entity> entities;

    private Field                     field;

    // private String fieldSign;

    public EntityClassVisitor(Log log, Map<String, Entity> entities, Entity entity) {
        this.LOG = log;
        this.entities = entities;
        this.entity = entity;
        LOG.debug("EntityClassVisitor.<init>");
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        LOG.info("---- Second pass for Entity " + entity.getSimpleName() + " ----");
    }

    static final String DESC_ENTITY = "javax/persistence/Entity";
    static final String DESC_TABLE  = "javax/persistence/Table";

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        final String internal = Type.getType(desc).getInternalName();
        LOG.debug("@" + internal + " visible=" + visible);
        if (DESC_TABLE.equals(internal)) {
            LOG.debug("   @" + entity);
            return new EntityAnnotationVisitor(LOG, internal, entity, null, null, null);
        }
        return null;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (0 == (access & ACCESS_STATIC) && null != entity && null != desc && null != Type.getType(desc)) {
            field = new Field();
            field.setName(name);
            try {
                String type = Type.getType(desc).getInternalName().replace('/', '.');
                
                // replace [L[Ltype;; with type[][]
                while (null != type && type.startsWith("[L") && type.endsWith(";")) {
                    int beginIndex = type.lastIndexOf("[L");
                    int endIndex = type.indexOf(";", beginIndex);
                    type = type.substring(beginIndex+2, endIndex) + "[]" + type.substring(endIndex+1);
                }
                
                // replace [J with long[]
                if ("[J".equals(type)) {
                    type = "long[]";
                }
                field.setType(type);
            }
            catch (NullPointerException npe) {
                LOG.warn("        !!!!!!!! " + entity.getClassName() + " " + name + " desc " + desc + " " + Type.getType(desc));
            }
            entity.getFields().add(field);
            LOG.info("        " + field.getType() + " " + name + "; <" + signature + ">");
            return new EntityFieldVisitor(LOG, entities, entity, field, signature);
        }
        else if (null != entity) {
            LOG.info("        !!!!!!!! SKIP " + name + " desc " + desc + " " + Type.getType(desc));
        }
        return null;
    }

}
