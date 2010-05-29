package net.sf.mardao.plugin.visitor;

import java.io.File;
import java.util.Map;
import java.util.Set;

import net.sf.mardao.domain.Entity;
import net.sf.mardao.domain.Group;

import org.apache.maven.plugin.logging.Log;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

/**
 * Scans the class to see if it is an Entity class
 * @author f94os
 */
public class FirstPassClassVisitor extends EmptyClassVisitor {
	private final Log LOG;
	private final Map<String, Group> packages;
	private final Map<String, Entity> entities;
	private final File file;
	private final Map<File, Entity> entityFiles;
	private String simpleName;
	private String packageName;
	private String className;

	public FirstPassClassVisitor(Log log, File file, Map<String, Group> packages, 
			Map<String, Entity> entities, Map<File, Entity> entityFiles) {
		LOG = log;
		this.file = file;
		this.packages = packages;
		this.entities = entities;
		this.entityFiles = entityFiles;
		LOG.debug("FirstPassClassVisitor.<init>");
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		className = name.replace('/', '.');
		int index = className.lastIndexOf('.');
		simpleName = className.substring(index+1);
		packageName = className.substring(0, index);
	}
	
	static final String DESC_ENTITY = "javax/persistence/Entity";
	static final String DESC_TABLE = "javax/persistence/Table";
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		final String internal = Type.getType(desc).getInternalName();
		LOG.debug("@" + internal + " visible=" + visible);
		if (DESC_ENTITY.equals(internal)) {
			LOG.info("@Entity " + packageName + "." + simpleName);
			Entity entity = new Entity();
			entity.setClassName(className);
			entity.setSimpleName(simpleName);
			
			Group group = packages.get(packageName);
			if (null == group) {
				group = new Group();
				group.setName(packageName);
				packages.put(packageName, group);
			}
			
			group.getEntities().put(simpleName, entity);
			entities.put(entity.getClassName(), entity);
			entityFiles.put(file, entity);
		}
		return null;
	}
}
