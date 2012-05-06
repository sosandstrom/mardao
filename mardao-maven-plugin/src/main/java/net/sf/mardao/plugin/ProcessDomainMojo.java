package net.sf.mardao.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.sf.mardao.domain.Entity;
import net.sf.mardao.domain.Field;
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
 * This is the Mojo that scans the domain classes and builds a graph; then, it traverses the graph and generates DAO source files
 * by merging templates.
 * 
 * @goal process-classes
 * @author f94os
 * 
 */
public class ProcessDomainMojo extends AbstractMardaoMojo {
    private final Map<String, Group>  packages    = new HashMap<String, Group>();
    private final Map<String, Entity> entities    = new HashMap<String, Entity>();
    private final Map<File, Entity>   entityFiles = new TreeMap<File, Entity>();
    private final Map<String, Field> inverseFields = new HashMap<String, Field>();

    /**
     * Calls super.execute(), then process the configured classpaths
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();

        // scan classes and build graph
        try {
            processClasspaths();
        }
        catch (Exception e) {
            throw new MojoExecutionException("Error processing entity classes", e);
        }
    }

    public static String firstToLower(final String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public static String firstToUpper(final String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public List<Entity> getEntitiesResolved(Map<String, Entity> entities) {
        List<Entity> resolved = new ArrayList<Entity>();
        List<Entity> remaining = new ArrayList<Entity>(entities.values());
        for(Entity e : entities.values()) {
            resolveEntity(resolved, e, remaining);
        }

        return resolved;
    }

    private void resolveEntity(List<Entity> resolved, Entity e, List<Entity> remaining) {
        System.out.println("resolveEntity " + e.getSimpleName() + " " + remaining.contains(e));
        // only process if remaining
        if (remaining.contains(e)) {

            // remove this entity from remaining to avoid circular recursion:
            remaining.remove(e);

            for(Field f : e.getFields()) {
                if (null != f.getEntity() && !e.getClassName().equals(f.getEntity().getClassName())) {
                    resolveEntity(resolved, f.getEntity(), remaining);
                }
            }
            for(Field f : e.getOneToOnes()) {
                if (null != f.getEntity() && !e.getClassName().equals(f.getEntity().getClassName())) {
                    e.getDependsOn().add(f.getEntity());
                    resolveEntity(resolved, f.getEntity(), remaining);
                }
            }
            for(Field f : e.getManyToOnes()) {
                if (null != f.getEntity() && !e.getClassName().equals(f.getEntity().getClassName())) {
                    e.getDependsOn().add(f.getEntity());
                    resolveEntity(resolved, f.getEntity(), remaining);
                }
            }

            System.out.println("   add resolved entity " + e.getSimpleName());
            resolved.add(e);

            // create ancestor and parents lists
            System.out.println("+ resolving parents for " + e.getSimpleName());
            final List<Entity> ancestors = new ArrayList<Entity>();
            final List<Entity> parents = new ArrayList<Entity>();
            Field f = e.getParent();
            Entity p = null;
            boolean direct = true;
            
            // break if self is parent
            while (null != f && p != f.getEntity()) {
                p = f.getEntity();
                if (direct) {
                    direct = false;
                    p.getChildren().add(e);
                }
                System.out.println(" - parent is " + p.getSimpleName());
                parents.add(p);
                ancestors.add(0, p);
                f = p.getParent();
            }
            e.setAncestors(ancestors);
            e.setParents(parents);

            // populate owner-inverse-map
            String key;
            for (Field m2m : e.getManyToManys()) {
                if (null != m2m.getMappedBy()) {
                    key = String.format("%s.%s", m2m.getEntity().getSimpleName(), m2m.getMappedBy());
                    System.out.println("  m2m " + key + "->" + m2m);
                    inverseFields.put(key,
                            m2m);
                }
            }
        }
    }

    private void mergeEntity(Entity en) {
        vc.put("entity", en);

        // // create ancestor and parents lists
        // System.out.println("+ resolving parents for " + en.getSimpleName());
        // final List<Entity> ancestors = new ArrayList<Entity>();
        // final List<Entity> parents = new ArrayList<Entity>();
        // Field f = en.getParent();
        // Entity p;
        // while (null != f) {
        // p = f.getEntity();
        // System.out.println(" - parent is " + p.getSimpleName());
        // parents.add(p);
        // ancestors.add(0, p);
        // f = p.getParent();
        // }
        // en.setAncestors(ancestors);
        // en.setParents(parents);
        vc.put("ancestors", en.getAncestors());
        vc.put("parents", en.getParents());
        vc.put("children", en.getChildren());

        for(MergeTemplate mt : mergeScheme.getTemplates()) {
            if (mt.isEntity()) {
                mergeTemplate(mt, en.getSimpleName());
            }
        }
    }

    private void mergePackages() {
        vc.put("packages", packages);
        vc.put("entities", entities);
        vc.put("inverseFields", inverseFields);

        for(Group p : packages.values()) {

            // calculate daoPackage from daoBasePackage
            if (p.getName().startsWith(domainBasePackage)) {
                p.setDaoPackageName(daoBasePackage + p.getName().substring(domainBasePackage.length()));
                vc.put("controllerPackage", controllerBasePackage + p.getName().substring(domainBasePackage.length()));
            }
            else {
                p.setDaoPackageName(daoBasePackage);
                vc.put("controllerPackage", controllerBasePackage);
            }

            vc.put("domainPackage", p);
            vc.put("daoPackage", p.getDaoPackageName());
            for(Entity e : getEntitiesResolved(p.getEntities())) {
                mergeEntity(e);
            }
        }

        // merge non-entity-specific templates
        for(MergeTemplate mt : mergeScheme.getTemplates()) {
            if (mt.isListingEntities()) {
                mergeTemplate(mt, null);
            }
        }
    }

    /**
     * Processes the default classpath (classpathElement), then any additional elements (additionalClasspathElements).
     * 
     * @return
     * @throws Exception
     */
    protected Map<String, Group> processClasspaths() throws Exception {

        // default classpath element
        processClasspath(classpathElement);

        // and any additional elements:
        for(String s : additionalClasspathElements) {
            processClasspath(s);
        }

        // second pass ClassVisitor:
        for(Entry<File, Entity> entry : entityFiles.entrySet()) {
            File f = entry.getKey();
            getLog().debug("--- file: " + f);
            try {
                final FileInputStream fis = new FileInputStream(f);
                final ClassReader cr = new ClassReader(fis);
                EntityClassVisitor fpcv = new EntityClassVisitor(getLog(), entities, entry.getValue());
                cr.accept(fpcv, 0);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }

        mergePackages();

        return packages;
    }

    /**
     * Process the classes for a specified package
     * 
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
            processPackage(dir, dir);
        }
    }

    /**
     * Recursive method to process a folder or file; if a folder, call recursively for each file. If for a file, process the file
     * using the classVisitor.
     * 
     * @param root
     *            base folder
     * @param dir
     *            this (sub-)packages folder
     */
    private void processPackage(File root, File dir) {
        getLog().debug("- package: " + dir);
        if (null != dir && dir.isDirectory()) {
            for(File f : dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory() || pathname.getName().endsWith(".class");
                }
            })) {
                if (f.isDirectory()) {
                    processPackage(root, f);
                }
                else if (f.getParentFile().getAbsolutePath().replace('/', '.').endsWith(basePackage + '.' + domainPackageName)) {
                    getLog().debug("--- file: " + f);
                    try {
                        final FileInputStream fis = new FileInputStream(f);
                        final ClassReader cr = new ClassReader(fis);
                        FirstPassClassVisitor fpcv = new FirstPassClassVisitor(getLog(), f, packages, entities, entityFiles);
                        cr.accept(fpcv, 0);
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
