package net.sf.mardao.plugin.visitor;

import java.util.TreeSet;

import net.sf.mardao.domain.Entity;
import net.sf.mardao.domain.Field;

import org.apache.maven.plugin.logging.Log;
import org.objectweb.asm.AnnotationVisitor;

public class EntityAnnotationVisitor implements AnnotationVisitor {

	private final String annotation;
	private final Entity entity;
	private final Field field;
	private final Log LOG;
	private String arrayName;
	private final TreeSet<String> uniqueConstraint;
	private int order = 0;

	public EntityAnnotationVisitor(Log log, String annotation, Entity entity, Field field, TreeSet<String> uniqueConstraint2) {
		this.LOG = log;
		this.annotation = annotation;
		this.entity = entity;
		this.field = field;
		this.uniqueConstraint = uniqueConstraint2;
	}

	@Override
	public void visit(String name, Object value) {
		LOG.info("   visit @" + name + " " + value + " (" + arrayName + ")");
		if ("mappedBy".equals(name)) {
			// for this side's resolution:
			field.setMappedBy(value.toString());
			
			// and remote side's resolution:
			entity.getMappedBy().put(value.toString(), field);
			LOG.info("         visit " + entity.getSimpleName() + "." + value + "->" + field.getName());
		}
		else if ("name".equals(name)) {
			if (EntityClassVisitor.DESC_TABLE.equals(annotation)) {
				entity.setTableName(value.toString());
			}
			else if (EntityFieldVisitor.DESC_COLUMN.equals(annotation)) {
				field.setColumnName(value.toString());
			} 
		}
		else if (null != uniqueConstraint) {
			uniqueConstraint.add(value.toString());
		}
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		LOG.debug("   @ visitAnnotation " + name + " " + desc);
		if ("uniqueConstraints".equals(arrayName) && null == name && "Ljavax/persistence/UniqueConstraint;".equals(desc)) {
			return new EntityAnnotationVisitor(LOG, annotation, entity, field, new TreeSet<String>());
		}
		return null;
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		LOG.info("   @ visitArray " + name);
		arrayName = name;
		order++;
		return this;
	}

	@Override
	public void visitEnd() {
		LOG.debug("   @ visitEnd(" + arrayName + "," + order + ")");
		order--;
		if (null != entity && null != uniqueConstraint && 0 == order) {
			entity.getUniqueConstraints().add(uniqueConstraint);
		}
	}

	@Override
	public void visitEnum(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
	}

}
