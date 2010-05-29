package net.sf.mardao.plugin.visitor;

import java.util.Map;

import net.sf.mardao.domain.Entity;
import net.sf.mardao.domain.Field;
import net.sf.mardao.domain.Group;

import org.apache.maven.plugin.logging.Log;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class EntityClassVisitor extends EmptyClassVisitor {
	
//	private Group group;
	private final Entity entity;
	private final Log LOG;
	private final Map<String, Entity> entities;
//	private String simpleName;
//	private String packageName;
//	private String className;
//	private EntityAnnotationVisitor annotationVisitor;
//	private EntityFieldVisitor fieldVisitor;

	private Field field;
//	private String fieldSign;

	public EntityClassVisitor(Log log, Map<String, Entity> entities, Entity entity) {
		this.LOG = log;
		this.entities = entities;
		this.entity = entity;
		LOG.debug("EntityClassVisitor.<init>");
//		annotationVisitor = new EntityAnnotationVisitor(null);
//		fieldVisitor = new EntityFieldVisitor();
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		LOG.info("---- Second pass for Entity " + entity.getSimpleName() + " ----");
//		className = name.replace('/', '.');
//		int index = className.lastIndexOf('.');
//		simpleName = className.substring(index+1);
//		packageName = className.substring(0, index);
//		entity = null;
//		group = null;
	}
	
	static final String DESC_ENTITY = "javax/persistence/Entity";
	static final String DESC_TABLE = "javax/persistence/Table";
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		final String internal = Type.getType(desc).getInternalName();
		LOG.debug("@" + internal + " visible=" + visible);
		if (DESC_TABLE.equals(internal)) {
			LOG.debug("   @" + entity);
			return new EntityAnnotationVisitor(LOG, entity, null, null);
		}
		return null;
	}
	
//	public Group getGroup() {
//		return group;
//	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		if (null != entity && null != desc && null != Type.getType(desc)) {
			field = new Field();
			field.setName(name);
			try {
				field.setType(Type.getType(desc).getInternalName().replace('/', '.'));
			}
			catch (NullPointerException npe) {
				LOG.info("        !!!!!!!! " + entity.getClassName() + " " + name + " desc " + desc + " " + Type.getType(desc));
			}
			entity.getFields().add(field);
			LOG.info("        " + field.getType() + " " + name + "; <" + signature + ">");
			return new EntityFieldVisitor(LOG, entities, entity, field, signature);
		}
		else if (null != entity){
			LOG.info("        !!!!!!!! " + name + " desc " + desc + " " + Type.getType(desc));
		}
		return null;
	}

}
