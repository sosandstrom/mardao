package net.sf.mardao.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import net.sf.mardao.domain.Entity;
import net.sf.mardao.domain.Group;
import net.sf.mardao.domain.MergeTemplate;
import net.sf.mardao.plugin.visitor.EntityClassVisitor;
import net.sf.mardao.plugin.visitor.FirstPassClassVisitor;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.objectweb.asm.ClassReader;

/**
 * This is the Mojo that scans the domain classes and builds a graph; then, it 
 * traverses the graph and generates DAO source files by merging templates.
 * @goal process-classes
 * @author f94os
 *
 */
public class ProcessDomainMojo extends AbstractMardaoMojo {
	private final Map<String,Group> packages = new HashMap<String,Group>();
	private final Map<String,Entity> entities = new HashMap<String,Entity>();
	private final Map<File, Entity> entityFiles = new TreeMap<File, Entity>();

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
		
		// default classpath element
		processClasspath(classpathElement);

		// and any additional elements:
		for (String s : additionalClasspathElements) {
			processClasspath(s);
		}
		
		// second pass ClassVisitor:
		for (Entry<File, Entity> entry : entityFiles.entrySet()) {
			File f = entry.getKey();
			getLog().debug("--- file: " + f);
			try {
				final FileInputStream fis = new FileInputStream(f);
				final ClassReader cr = new ClassReader(fis);
				EntityClassVisitor fpcv = new EntityClassVisitor(
						getLog(), entities, entry.getValue());
				cr.accept(fpcv, 0);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
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
					FirstPassClassVisitor fpcv = new FirstPassClassVisitor(
							getLog(), f, packages, entities, entityFiles);
					cr.accept(fpcv, 0);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
