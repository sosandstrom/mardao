package net.sf.mardao.plugin.visitor;

import java.util.Map;

import net.sf.mardao.domain.Entity;
import net.sf.mardao.domain.Field;

import org.apache.maven.plugin.logging.Log;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;

public class EntityFieldVisitor implements FieldVisitor {
    static final String               DESC_COLUMN       = "javax_persistence_Column";
    static final String               DESC_ID           = "javax_persistence_Id";
    static final String               DESC_MANY_TO_MANY = "javax_persistence_ManyToMany";
    static final String               DESC_MANY_TO_ONE  = "javax_persistence_ManyToOne";
    static final String               DESC_ONE_TO_MANY  = "javax_persistence_OneToMany";
    static final String               DESC_ONE_TO_ONE   = "javax_persistence_OneToOne";
    static final String               DESC_PARENT       = "net_sf_mardao_api_Parent";
    static final String               DESC_CREATED_BY   = "net_sf_mardao_api_CreatedBy";
    static final String               DESC_CREATED_DATE = "net_sf_mardao_api_CreatedDate";
    static final String               DESC_UPDATED_BY   = "net_sf_mardao_api_UpdatedBy";
    static final String               DESC_UPDATED_DATE = "net_sf_mardao_api_UpdatedDate";

    private final Entity              entity;
    private final Field               field;
    private final Log                 LOG;
    private final Map<String, Entity> entities;
    private final String              fieldSign;

    public EntityFieldVisitor(Log log, Map<String, Entity> entities, Entity entity, Field field, String fieldSign) {
        this.LOG = log;
        this.entities = entities;
        this.entity = entity;
        this.field = field;
        this.fieldSign = fieldSign;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, boolean visible) {
        final String internal = Type.getType(name).getInternalName().replace('/', '_');
        LOG.info("             @" + internal);

        // column
        if (DESC_COLUMN.equals(internal)) {
            return new EntityAnnotationVisitor(LOG, internal, entity, field, null, entities);
        }
        // primary key
        else if (DESC_ID.equals(internal)) {
            entity.getFields().remove(field);
            entity.setPk(field);
        }
        // parent key
        else if (DESC_PARENT.equals(internal)) {
            entity.getFields().remove(field);
            entity.setParent(field);
            return new EntityAnnotationVisitor(LOG, internal, entity, field, null, entities);
        }
        // created by and date
        else if (DESC_CREATED_BY.equals(internal)) {
            entity.getFields().remove(field);
            entity.setCreatedBy(field);
        }
        else if (DESC_CREATED_DATE.equals(internal)) {
            entity.getFields().remove(field);
            entity.setCreatedDate(field);
        }
        // created by and date
        else if (DESC_UPDATED_BY.equals(internal)) {
            entity.getFields().remove(field);
            entity.setUpdatedBy(field);
        }
        else if (DESC_UPDATED_DATE.equals(internal)) {
            entity.getFields().remove(field);
            entity.setUpdatedDate(field);
        }
        // one-to-one
        else if (DESC_ONE_TO_ONE.equals(internal)) {
            entity.getFields().remove(field);
            entity.getOneToOnes().add(field);
            field.setEntity(entities.get(field.getType()));
            LOG.info("                remote=" + field.getEntity());
        }
        // many-to-one
        else if (DESC_MANY_TO_ONE.equals(internal)) {
            entity.getFields().remove(field);
            entity.getManyToOnes().add(field);
            field.setEntity(entities.get(field.getType()));
            LOG.info("                remote=" + field.getEntity());
        }
        // one-to-many; nothing to generate!
        else if (DESC_ONE_TO_MANY.equals(internal)) {
            entity.getFields().remove(field);
        }
        else if (DESC_MANY_TO_MANY.equals(internal)) {
            entity.getFields().remove(field);
            entity.getManyToManys().add(field);
            // update field type to generic type T (List<T>)
            if (null != fieldSign) {
                int beginIndex = fieldSign.indexOf('<') + 1;
                int endIndex = fieldSign.indexOf('>', beginIndex);
                String desc = fieldSign.substring(beginIndex, endIndex);
                String generic = Type.getType(desc).getInternalName().replace('/', '.');
                field.setType(generic);
                field.setEntity(entities.get(generic));
            }
            return new EntityAnnotationVisitor(LOG, internal, entity, field, null, entities);
        }
        return null;
    }

    @Override
    public void visitAttribute(Attribute attr) {
        LOG.info("   visitFieldAttribute " + attr);
    }

    @Override
    public void visitEnd() {
        // TODO Auto-generated method stub

    }

}
