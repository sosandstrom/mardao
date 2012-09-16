package net.sf.mardao.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sf.mardao.domain.MergeScheme;
import net.sf.mardao.domain.MergeTemplate;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * The abstract Mojo for the mardao generator, which declares all configuration parameters. First pass is parsing the domain
 * classes and build a graph, second pass is generating DAO classes while traversing the graph.
 * 
 * @author f94os
 * 
 */
public class AbstractMardaoMojo extends AbstractMojo {

    /**
     * @parameter expression="${generate.additionalClasspathElements}"
     */
    protected ArrayList<String>              additionalClasspathElements;

    /**
     * @parameter expression="${generate.classpathElement}" default-value="${basedir}/target/classes"
     *            ${project.compileClasspathElements} required readonly
     */
    protected String                         classpathElement;

    /**
     * The target folder
     * 
     * @parameter expression="${generate.targetFolder}" default-value="${basedir}/target/generated-sources/dao"
     */
    protected File                           targetFolder;

    /**
     * The target folder
     * 
     * @parameter expression="${generate.targetResourcesFolder}" default-value="${basedir}/target/generated-resources/web"
     */
    protected File                           targetResourcesFolder;

    /**
     * The source folder
     * 
     * @parameter expression="${generate.sourceFolder}" default-value="${basedir}/src/main/java"
     */
    protected File                           sourceFolder;

    /**
     * The resource folder
     * 
     * @parameter expression="${generate.resourceFolder}" default-value="${basedir}/src/main/resources"
     */
    protected File                           resourceFolder;

    /**
     * The webapp folder
     * 
     * @parameter expression="${generate.webappFolder}" default-value="${basedir}/src/main/webapp"
     */
    protected File                           webappFolder;

    /**
     * @parameter expression="${generate.templateFolder}"
     */
    protected String                         templateFolder;

    /**
     * @parameter expression="${generate.templateList}" default-value="/WEB-INF/templates.xml"
     */
    protected String                         templateList = "/WEB-INF/templates.xml";

    /**
     * @parameter expression="${generate.basePackage}" default-value="${project.groupId}.${project.artifactId}"
     */
    protected String                         basePackage;
    /**
     * @parameter expression="${generate.daoPackageName}" default-value="dao"
     */
    protected String                         daoPackageName;
    /**
     * @parameter expression="${generate.domainPackageName}" default-value="domain"
     */
    protected String                         domainPackageName;
    /**
     * @parameter expression="${generate.controllerPackageName}" default-value="web"
     */
    protected String                         controllerPackageName;

    /**
     * @parameter expression="${generate.persistenceUnitName}" default-value="transactions-optional"
     */
    protected String                         persistenceUnitName;

    /**
     * @parameter expression="${generate.generateEntityManager}" default-value="true"
     */
    protected boolean                        containerManagedEntityManager;
    /**
     * @parameter expression="${generate.generateEntityTransaction}" default-value="true"
     */
    protected boolean                        containerManagedTransactionManager;

    /**
     * @parameter expression="${generate.persistenceType}" default-value="v2"
     */
    protected String                         persistenceType;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @parameter expression="${generate.sourceVersion}" default-value="${maven.compiler.source}"
     */
    protected String                         sourceVersion;
    
    /**
     * @parameter expression="${generate.mardaoApiPath}" default-value="${user.home}/.m2/repository/net/sf/mardao/mardao-api/${project.version}/mardao-api-${project.version}.jar"
     */
    protected File                         mardaoApiPath;
    
    /**
     * @parameter expression="${generate.jpaApiPath}" default-value="${user.home}/.m2/repository/org/apache/geronimo/specs/geronimo-jpa_3.0_spec/1.1.1/geronimo-jpa_3.0_spec-1.1.1.jar"
     */
    protected File                         jpaApiPath;
    
    public static final java.text.DateFormat DATEFORMAT   = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    protected String                         daoBasePackage;
    protected String                         domainBasePackage;
    protected String                         controllerBasePackage;

    protected final VelocityContext          vc           = new VelocityContext();

    protected File                           targetDaoFolder;
    protected File                           targetControllerFolder;
    protected File                           targetJspFolder;

    protected File                           srcDaoFolder;
    protected File                           srcDomainFolder;
    protected File                           srcControllerFolder;
    protected File                           srcJspFolder;

    protected MergeScheme                    mergeScheme;

    private HashMap<String, File>            destFolders;

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
        srcDomainFolder = new File(sourceFolder, domainBasePackage.replace('.', '/'));
        if (false == srcDomainFolder.exists()) {
            srcDomainFolder.mkdirs();
        }
        
        if ("WEB".equals(persistenceType)) {
            srcControllerFolder = new File(sourceFolder, controllerBasePackage.replace('.', '/'));
            if (false == srcControllerFolder.exists()) {
                srcControllerFolder.mkdirs();
            }
            targetControllerFolder = new File(targetFolder, controllerBasePackage.replace('.', '/'));
            if (false == targetControllerFolder.exists()) {
                targetControllerFolder.mkdirs();
            }
            srcJspFolder = new File(webappFolder, "WEB-INF/jsp");
            if (false == srcJspFolder.exists()) {
                srcJspFolder.mkdirs();
            }
            targetJspFolder = new File(targetResourcesFolder, "WEB-INF/jsp");
            if (false == targetJspFolder.exists()) {
                targetJspFolder.mkdirs();
            }
        }

        // create destFolders
        destFolders = new HashMap<String, File>();
        destFolders.put("srcDao", srcDaoFolder);
        destFolders.put("srcDomain", srcDomainFolder);
        destFolders.put("srcController", srcControllerFolder);
        destFolders.put("targetDao", targetDaoFolder);
        destFolders.put("targetController", targetControllerFolder);
        destFolders.put("targetJsp", targetJspFolder);
        destFolders.put("webappJsp", srcJspFolder);
        destFolders.put("webapp", webappFolder);
        destFolders.put("resources", resourceFolder);
    }
    
    

    /**
     * Creates and populates the velocity context, then calls mkdirs to create output directories.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM, this);
        vc.put("helper", this);
        vc.put("ESC", "$");

        if (null != sourceVersion) {
            getLog().info("sourceVersion=" + sourceVersion);
            vc.put("sourceVersion", sourceVersion);
        }

        Map<String, String> mySqlTypes = new HashMap<String, String>();
        Map<String, String> sqLiteTypes = new HashMap<String, String>();
        Map<String, String> mySqlDefaults = new HashMap<String, String>();
        // for MySQL
        mySqlTypes.put(Long.class.getName(), "BIGINT");
        mySqlTypes.put(Integer.class.getName(), "INTEGER");
        mySqlTypes.put(Short.class.getName(), "SMALLINT");
        mySqlTypes.put(Byte.class.getName(), "TINYINT");
        mySqlTypes.put(Double.class.getName(), "DOUBLE PRECISION");
        // MySQL max key length is 767:
        mySqlTypes.put(String.class.getName(), "VARCHAR(255)");
        mySqlTypes.put(Boolean.class.getName(), "BIT(1)");
        mySqlTypes.put(java.util.Date.class.getName(), "TIMESTAMP");

        mySqlDefaults.put(Long.class.getName(), "DEFAULT NULL");
        mySqlDefaults.put(Integer.class.getName(), "DEFAULT NULL");
        mySqlDefaults.put(Short.class.getName(), "DEFAULT NULL");
        mySqlDefaults.put(Byte.class.getName(), "DEFAULT NULL");
        mySqlDefaults.put(Double.class.getName(), "DEFAULT NULL");
        mySqlDefaults.put(String.class.getName(), "DEFAULT NULL");
        mySqlDefaults.put(Boolean.class.getName(), "DEFAULT NULL");
        mySqlDefaults.put(java.util.Date.class.getName(), "NOT NULL DEFAULT CURRENT_TIMESTAMP");
        
        // for SQLite
        sqLiteTypes.put(Long.class.getName(), "INT");
        sqLiteTypes.put(String.class.getName(), "TEXT");
        sqLiteTypes.put(Boolean.class.getName(), "TINYINT");
        sqLiteTypes.put(Float.class.getName(), "REAL");
        sqLiteTypes.put(Double.class.getName(), "REAL");

        if ("Android".equals(persistenceType)) {
            vc.put("dbTypes", sqLiteTypes);
        }
        else {
            // FIXME: other DB types
            vc.put("dbTypes", mySqlTypes);
            vc.put("dbDefaults", mySqlDefaults);
        }

        Date current = new Date();
        vc.put("currentDate", DATEFORMAT.format(current));
        vc.put("persistenceUnitName", persistenceUnitName);
        vc.put("containerManagedEntityManager", containerManagedEntityManager);
        vc.put("containerManagedTransactionManager", containerManagedTransactionManager);

        // persistence type and merge scheme:
        vc.put("persistenceType", persistenceType);
        ApplicationContext springCtx = new GenericXmlApplicationContext("/META-INF/merge-scheme-beans.xml");
        mergeScheme = (MergeScheme) springCtx.getBean("mergeScheme" + persistenceType);

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
        }
        catch (Exception e) {
            throw new MojoExecutionException(getClass().getSimpleName(), e);
        }

        daoBasePackage = basePackage + "." + daoPackageName;
        domainBasePackage = basePackage + "." + domainPackageName;
        controllerBasePackage = basePackage + "." + controllerPackageName;

        vc.put("basePackage", basePackage);
        vc.put("daoBasePackage", daoBasePackage);
        vc.put("domainBasePackage", domainBasePackage);
        vc.put("controllerBasePackage", controllerBasePackage);

        mkdirs();
    }

    protected void mergeTemplate(MergeTemplate mt, String entityName) {
        // {prefix} {entitiyName} {middle} {persistenceType} {suffix}
        // Abstract Employee Dao Spring .java

        // compose template name:
        StringBuffer templateName = new StringBuffer();
        if (mt.isTypeSpecific()) {
            templateName.append(persistenceType);
            templateName.append('/');
        }
        templateName.append(mt.getTemplatePrefix());
        // no entityName in template filename!
        templateName.append(mt.getTemplateMiddle());
        if (mt.isTypeSpecific()) {
            templateName.append(persistenceType);
        }
        templateName.append(mt.getTemplateSuffix());

        // compose output filename:
        StringBuffer fileName = new StringBuffer(mt.getFilePrefix());
        if (mt.isEntity()) {
            fileName.append(entityName);
        }
        fileName.append(mt.getFileMiddle());
        if (mt.isTypeSpecific() && mt.isTypeAppend()) {
            fileName.append(persistenceType);
        }
        fileName.append(mt.getFileSuffix());

        // lookup destination folder, and merge the template:
        File folder = destFolders.get(mt.getDestFolder());

        mergeTemplate(templateName.toString(), folder, fileName.toString());
    }

    /**
     * Merges a Velocity template for a specified file, unless it already exists.
     * 
     * @param templateFilename
     * @param folder
     * @param javaFilename
     */
    private void mergeTemplate(String templateFilename, File folder, String javaFilename) {
        final File javaFile = new File(folder, javaFilename);

        // create destination folder?
        File destinationFolder = javaFile.getParentFile();
        if (false == destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }

        // up-to-date?
        if (false == javaFile.exists()) {
            getLog().info("Merging " + templateFilename + " for " + javaFilename);
            try {
                final PrintWriter writer = new PrintWriter(javaFile);
                Template template = Velocity.getTemplate(templateFilename);
                template.merge(vc, writer);
                writer.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (ResourceNotFoundException e) {
                e.printStackTrace();
            }
            catch (ParseErrorException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            getLog().info("Skipping " + templateFilename + " for " + javaFilename);
        }
    }

}
