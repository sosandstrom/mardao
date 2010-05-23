package net.sf.mardao.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * The abstract Mojo for the mardao generator. 
 * First pass is parsing the domain classes and build a graph,
 * second pass is generating DAO classes while traversing the graph. 
 * @author f94os
 *
 */
public class AbstractMardaoMojo extends AbstractMojo {
	
	/** 
	 * @parameter expression="${generate.additionalClasspathElements}" 
	 */ 
	protected ArrayList<String> additionalClasspathElements; 	
	
	/** 
	 * @parameter expression="${generate.classpathElement}" default-value="${basedir}/target/classes"
	 * ${project.compileClasspathElements}
	 * required 
	 * readonly
	 */ 
	protected String classpathElement; 	
	
	/** 
	 * The target folder 
	 * @parameter expression="${generate.targetFolder}" default-value="${basedir}/target/generated-sources/dao" 
	 */ 
	protected File targetFolder;
	
	/** 
	 * The source folder 
	 * @parameter expression="${generate.sourceFolder}" default-value="${basedir}/src/main/java" 
	 */ 
	protected File sourceFolder;
	
	/** 
	 * The resource folder 
	 * @parameter expression="${generate.resourceFolder}" default-value="${basedir}/src/main/resources" 
	 */ 
	protected File resourceFolder;
	
	/**
	 * @parameter expression="${generate.templateFolder}"
	 */
	protected String templateFolder;
	
	/**
	 * @parameter expression="${generate.templateList}" default-value="/WEB-INF/templates.xml"
	 */
	protected String templateList = "/WEB-INF/templates.xml";
	
	/**
	 * @parameter expression="${generate.basePackage}" default-value="${project.groupId}.${project.artifactId}"
	 */
	protected String basePackage;
	/**
	 * @parameter expression="${generate.daoPackageName}" default-value="dao"
	 */
	protected String daoPackageName;
	/**
	 * @parameter expression="${generate.domainPackageName}" default-value="domain"
	 */
	protected String domainPackageName;

	/**
	 * @parameter expression="${generate.persistenceUnitName}" default-value="transactions-optional"
	 */
	protected String persistenceUnitName;
	
	/**
	 * @parameter expression="${generate.generateEntityManager}" default-value="true"
	 */
	protected boolean containerManagedEntityManager;
	/**
	 * @parameter expression="${generate.generateEntityTransaction}" default-value="true"
	 */
	protected boolean containerManagedTransactionManager;
	
	/**
	 * @parameter expression="${generate.persistenceType}" default-value="Spring"
	 */
	protected String persistenceType;
	
	public static final java.text.DateFormat DATEFORMAT = new java.text.SimpleDateFormat(
		"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	protected String daoBasePackage;
	protected String domainBasePackage;

	protected final VelocityContext vc = new VelocityContext();

	protected File targetDaoFolder;

	protected File srcDaoFolder;

	private void mkdirs() {
		// create target/generated-sources/dao folder
		if (false == targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		if (false == sourceFolder.exists()) {
			sourceFolder.mkdirs();
		}
		if (false == resourceFolder.exists()) {
			resourceFolder.mkdirs();
		}
		
		// group folder, where entity daos are created
		targetDaoFolder = new File(targetFolder, daoBasePackage.replace('.', '/'));
		if (false == targetDaoFolder.exists()) {
			targetDaoFolder.mkdirs();
		}
		srcDaoFolder = new File(sourceFolder, daoBasePackage.replace('.', '/'));
		if (false == srcDaoFolder.exists()) {
			srcDaoFolder.mkdirs();
		}

	}

	/**
	 * Creates and populates the velocity context, then calls mkdirs to create output directories.
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM, this);
		vc.put("helper", this);
		Date current = new Date();
		vc.put("currentDate", DATEFORMAT.format(current));
		vc.put("persistenceUnitName", persistenceUnitName);
		vc.put("containerManagedEntityManager", containerManagedEntityManager);
      vc.put("containerManagedTransactionManager", containerManagedTransactionManager);
        vc.put("persistenceType", persistenceType);
		
		getLog().debug("templateFolder=" + templateFolder);
		final Properties p = new Properties();
		p.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
        p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		
		// load templates from external folder?
		if (null == templateFolder) {
			p.setProperty("resource.loader", "class");
		}
		else {
			p.setProperty("resource.loader", "file, class");
			p.setProperty("file.resource.loader.description", "Velocity File Resource Loader");
			p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
			p.setProperty("file.resource.loader.path", templateFolder);
			p.setProperty("file.resource.loader.cache", "true");
			p.setProperty("file.resource.loader.modificationCheckInterval", "0");
		}
		try {
			Velocity.init(p);
		} catch (Exception e) {
			throw new MojoExecutionException(getClass().getSimpleName(), e);
		}
		
		daoBasePackage = basePackage + "." + daoPackageName;
		domainBasePackage = basePackage + "." + domainPackageName;
		
		vc.put("basePackage", basePackage);
		vc.put("daoBasePackage", daoBasePackage);
		vc.put("domainBasePackage", domainBasePackage);
		
		mkdirs();
	}

	/**
	 * Merges a Velocity template for a specified file, unless it already exists.
	 * @param templateFilename
	 * @param folder
	 * @param javaFilename
	 */
	protected void mergeTemplate(String templateFilename, File folder, String javaFilename) {
		final File javaFile = new File(folder, javaFilename);
		// up-to-date?
		if (false == javaFile.exists())
		{
			getLog().info("Merging " + templateFilename + " for " + javaFilename);
			try {
				final PrintWriter writer = new PrintWriter(javaFile);
				Template template = Velocity.getTemplate(templateFilename);
				template.merge(vc, writer);
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ResourceNotFoundException e) {
				e.printStackTrace();
			} catch (ParseErrorException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			getLog().info("Skipping " + templateFilename + " for " + javaFilename);
		}
	}

}
