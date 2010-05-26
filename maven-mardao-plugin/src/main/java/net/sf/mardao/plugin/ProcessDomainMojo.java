package net.sf.mardao.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import net.sf.mardao.domain.Entity;
import net.sf.mardao.domain.Field;
import net.sf.mardao.domain.Group;
import net.sf.mardao.domain.MergeTemplate;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * This is the Mojo that scans the domain classes and builds a graph. Then, it 
 * traverses the graph and generates DAO source files.
 * @goal process-classes
 * @author f94os
 *
 */
public class ProcessDomainMojo extends AbstractMardaoMojo {
	private EntityClassVisitor classVisitor;
	private final Map<String,Group> packages = new HashMap<String,Group>();
	private final Map<String,Entity> entities = new HashMap<String,Entity>();

	/**
	 * Calls super.execute(), then process the configured classpaths
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();
		
		// scan classes and build graph
		try {
			processClasspaths();
		} catch (Exception e) {
			throw new MojoExecutionException("Error processing entity classes", e);
		}
	}
	
	public static String firstToLower(final String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}
	
	public static String firstToUpper(final String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	private void mergeEntity(Entity en) {
		vc.put("entity", en);
		
		for (MergeTemplate mt : mergeScheme.getTemplates()) {
			if (mt.isEntity()) {
				mergeTemplate(mt, en.getSimpleName());
			}
		}
		// AbstractEntityDaoInterface in target:
//		mergeTemplate("AbstractDaoInterface.vm", targetDaoFolder, "Abstract" + en.getSimpleName() + "DaoInterface.java");
//		mergeTemplate("AbstractDao.vm", targetDaoFolder, "Abstract" + en.getSimpleName() + "Dao.java");
//		mergeTemplate("Dao.vm", srcDaoFolder, en.getSimpleName() + "Dao.java");
//		mergeTemplate("DaoBean.vm", srcDaoFolder, en.getSimpleName() + "DaoBean.java");
	}

	private void mergePackages() {
		vc.put("packages", packages);
		vc.put("entities", entities);
		
		for (Group p : packages.values()) {
			
			// calculate daoPackage from daoBasePackage
			if (p.getName().startsWith(domainBasePackage)) {
				p.setDaoPackageName(daoBasePackage + p.getName().substring(domainBasePackage.length()));
			}
			else {
				p.setDaoPackageName(daoBasePackage);
			}
			
			vc.put("domainPackage", p);
			vc.put("daoPackage", p.getDaoPackageName());
			for (Entity e : p.getEntities().values()) {
				mergeEntity(e);
			}
		}
//		mergeTemplate("spring-beans-xml.vm", resourceFolder, "spring-dao.xml");
		for (MergeTemplate mt : mergeScheme.getTemplates()) {
			if (mt.isListingEntities()) {
				mergeTemplate(mt, null);
			}
		}
	}
	
	/**
	 * Processes the default classpath (classpathElement), then any additional elements (additionalClasspathElements).
	 * @return
	 * @throws Exception
	 */
	protected Map<String,Group> processClasspaths() throws Exception {
		classVisitor = new EntityClassVisitor();
		
		// default classpath element
		processClasspath(classpathElement);

		// and any additional elements:
		for (String s : additionalClasspathElements) {
			processClasspath(s);
		}
		
		mergePackages();
		
		return packages;
	}
	
	/**
	 * Process the classes for a specified package
	 * @param classpathElement
	 * @throws ResourceNotFoundException
	 * @throws ParseErrorException
	 * @throws Exception
	 */
	private void processClasspath(String classpathElement) throws ResourceNotFoundException, ParseErrorException, Exception {
		if (classpathElement.endsWith(".jar")) {
			// TODO: implement JAR scanning
		}
		else {
			final File dir = new File(classpathElement);
			getLog().info("Classpath: " + dir.getAbsolutePath());
			processPackage(dir , dir);
		}
	}

	/**
	 * Recursive method to process a folder or file; if a folder, call recursively for each file. 
	 * If for a file, process the file using the classVisitor.
	 * @param root base folder
	 * @param dir this (sub-)packages folder
	 */
	private void processPackage(File root, File dir) {
		getLog().debug("- package: " + dir);
		for (File f : dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() || pathname.getName().endsWith(".class");
			}
		})) {
			if (f.isDirectory()) {
				processPackage(root, f);
			}
			else {
				getLog().debug("--- file: " + f);
				try {
					final FileInputStream fis = new FileInputStream(f);
					final ClassReader cr = new ClassReader(fis);
					cr.accept(classVisitor, 0);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class EntityClassVisitor implements ClassVisitor {
		
		
		private Group group;
		private Entity entity;
		private String simpleName;
		private String packageName;
		private String className;
		private EntityAnnotationVisitor annotationVisitor;

		private EntityFieldVisitor fieldVisitor;

		private Field field;
		private String fieldSign;

		public EntityClassVisitor() {
			getLog().debug("EntityClassVisitor.<init>");
			annotationVisitor = new EntityAnnotationVisitor(null);
			fieldVisitor = new EntityFieldVisitor();
		}

		@Override
		public void visit(int version, int access, String name, String signature,
				String superName, String[] interfaces) {
			className = name.replace('/', '.');
			int index = className.lastIndexOf('.');
			simpleName = className.substring(index+1);
			packageName = className.substring(0, index);
			entity = null;
			group = null;
		}
		
		static final String DESC_ENTITY = "javax/persistence/Entity";
		static final String DESC_TABLE = "javax/persistence/Table";
		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			final String internal = Type.getType(desc).getInternalName();
			getLog().debug("@" + internal + " visible=" + visible);
			if (DESC_ENTITY.equals(internal)) {
				entity = new Entity();
				entity.setClassName(className);
				entity.setSimpleName(simpleName);
				return null;
			}
			else if (null != entity && DESC_TABLE.equals(internal)) {
				getLog().debug("   @" + entity);
				return annotationVisitor;
			}
			return null;
		}
		
		@Override
		public void visitEnd() {
			if (null != entity) {
				getLog().info("@Entity " + packageName + "." + simpleName);
				
				Group group = packages.get(packageName);
				if (null == group) {
					group = new Group();
					group.setName(packageName);
					packages.put(packageName, group);
				}
				
				group.getEntities().put(simpleName, entity);
				entities.put(entity.getClassName(), entity);
			}
		}

		public Group getGroup() {
			return group;
		}

		@Override
		public void visitAttribute(Attribute arg0) {
			// TODO Auto-generated method stub
			
		}

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
					getLog().info("        !!!!!!!! " + entity.getClassName() + " " + name + " desc " + desc + " " + Type.getType(desc));
				}
				entity.getFields().add(field);
				this.fieldSign = signature;
				getLog().info("        " + field.getType() + " " + name + "; <" + signature + ">");
				return fieldVisitor;
			}
			else if (null != entity){
				getLog().info("        !!!!!!!! " + name + " desc " + desc + " " + Type.getType(desc));
			}
			return null;
		}

		@Override
		public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {
//			getLog().debug("        visitMethod " + name);
//			return methodVisitor;
			return null;
		}

		@Override
		public void visitOuterClass(String arg0, String arg1, String arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void visitSource(String arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}
		
		class EntityAnnotationVisitor implements AnnotationVisitor {

			private String arrayName;
			private final TreeSet<String> uniqueConstraint;
			private int order = 0;

			public EntityAnnotationVisitor(TreeSet<String> uniqueConstraint2) {
				this.uniqueConstraint = uniqueConstraint2;
			}

			@Override
			public void visit(String name, Object value) {
				getLog().info("   visit @" + name + " " + value + " (" + arrayName + ")");
				if ("mappedBy".equals(name)) {
					// for this side's resolution:
					field.setMappedBy(value.toString());
					
					// and remote side's resolution:
					entity.getMappedBy().put(value.toString(), field);
					getLog().info("         visit " + entity.getSimpleName() + "." + value + "->" + field.getName());
				}
				else if (null != uniqueConstraint) {
					uniqueConstraint.add(value.toString());
				}
			}

			@Override
			public AnnotationVisitor visitAnnotation(String name, String desc) {
				getLog().debug("   @ visitAnnotation " + name + " " + desc);
				if ("uniqueConstraints".equals(arrayName) && null == name && "Ljavax/persistence/UniqueConstraint;".equals(desc)) {
					return new EntityAnnotationVisitor(new TreeSet<String>());
				}
				return null;
			}

			@Override
			public AnnotationVisitor visitArray(String name) {
				getLog().info("   @ visitArray " + name);
				arrayName = name;
				order++;
				return this;
			}

			@Override
			public void visitEnd() {
				getLog().debug("   @ visitEnd(" + arrayName + "," + order + ")");
				order--;
				if (null != entity && null != uniqueConstraint && 0 == order) {
					entity.getUniqueConstraints().add(uniqueConstraint);
				}
			}

			@Override
			public void visitEnum(String arg0, String arg1, String arg2) {
				// TODO Auto-generated method stub
				getLog().debug("   @ visitEnum()" + arg0);

			}
		}
		
		class EntityFieldVisitor implements FieldVisitor {

			static final String DESC_ID = "javax_persistence_Id";
			static final String DESC_MANY_TO_MANY = "javax_persistence_ManyToMany";
			static final String DESC_MANY_TO_ONE = "javax_persistence_ManyToOne";
			static final String DESC_ONE_TO_MANY = "javax_persistence_OneToMany";
			static final String DESC_ONE_TO_ONE = "javax_persistence_OneToOne";

			@Override
			public AnnotationVisitor visitAnnotation(String name, boolean visible) {
				final String internal = Type.getType(name).getInternalName().replace('/', '_');
				getLog().info("             @" + internal);
				
				// primary key
				if (DESC_ID.equals(internal)) {
					entity.getFields().remove(field);
					entity.setPk(field);
				}
				// many-to-one
				else if (DESC_MANY_TO_ONE.equals(internal)) {
					entity.getFields().remove(field);
					entity.getManyToOnes().add(field);
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
					}
					return annotationVisitor;
				}
				return null;
			}

			@Override
			public void visitAttribute(Attribute attr) {
				getLog().info("   visitFieldAttribute " + attr);
				
			}

			@Override
			public void visitEnd() {
				// TODO Auto-generated method stub
				
			}
			
		}
		
	}
	

}
