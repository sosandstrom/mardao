package net.sf.mardao.plugin;

/*
 * #%L
 * net.sf.mardao:mardao-maven-plugin
 * %%
 * Copyright (C) 2010 - 2014 Wadpam
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.UniqueConstraint;
import net.sf.mardao.core.CreatedBy;
import net.sf.mardao.core.CreatedDate;
import net.sf.mardao.core.GeoLocation;
import net.sf.mardao.core.Parent;
import net.sf.mardao.core.UpdatedBy;
import net.sf.mardao.core.UpdatedDate;
import net.sf.mardao.dao.Cached;
import net.sf.mardao.domain.Entity;
import net.sf.mardao.domain.Field;
import net.sf.mardao.domain.Group;
import net.sf.mardao.domain.MergeTemplate;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * This is the Mojo that scans the domain classes and builds a graph; then, it
 * traverses the graph and generates DAO source files by merging templates.
 *
 * @requiresDependencyResolution test
 * @goal process-classes
 * @author f94os
 *
 */
public class ProcessDomainMojo extends AbstractMardaoMojo {
    
    /**
     * The plugin descriptor
     * 
     * @parameter default-value="${descriptor}"
     */
    private PluginDescriptor descriptor;    
    
    private final Map<String, Group> packages = new HashMap<String, Group>();
    private final Map<String, Entity> entities = new HashMap<String, Entity>();
    private final Map<File, Entity> entityFiles = new TreeMap<File, Entity>();
    private final Map<String, Field> inverseFields = new HashMap<String, Field>();

    /**
     * Calls super.execute(), then process the configured classpaths
     */
    @Override
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
        return null != name ? name.substring(0, 1).toUpperCase() + name.substring(1) : null;
    }

    public List<Entity> getEntitiesResolved(Map<String, Entity> entities) {
        List<Entity> resolved = new ArrayList<Entity>();
        List<Entity> remaining = new ArrayList<Entity>(entities.values());
        for (Entity e : entities.values()) {
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

            for (Field f : e.getFields()) {
                if (null != f.getEntity() && !e.getClassName().equals(f.getEntity().getClassName())) {
                    resolveEntity(resolved, f.getEntity(), remaining);
                }
            }
            for (Field f : e.getOneToOnes()) {
                if (null != f.getEntity() && !e.getClassName().equals(f.getEntity().getClassName())) {
                    e.getDependsOn().add(f.getEntity());
                    resolveEntity(resolved, f.getEntity(), remaining);
                }
            }
            for (Field f : e.getManyToOnes()) {
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

        vc.put("ancestors", en.getAncestors());
        vc.put("parents", en.getParents());
        vc.put("children", en.getChildren());

        for (MergeTemplate mt : mergeScheme.getTemplates()) {
            if (mt.isEntity()) {
                mergeTemplate(mt, en.getSimpleName());
            }
        }
    }

    private void mergePackages() {
        vc.put("packages", packages);
        vc.put("entities", entities);
        vc.put("inverseFields", inverseFields);

        for (Group p : packages.values()) {

            // calculate daoPackage from daoBasePackage
            if (p.getName().startsWith(domainBasePackage)) {
                p.setDaoPackageName(daoBasePackage + p.getName().substring(domainBasePackage.length()));
                vc.put("controllerPackage", controllerBasePackage + p.getName().substring(domainBasePackage.length()));
            } else {
                p.setDaoPackageName(daoBasePackage);
                vc.put("controllerPackage", controllerBasePackage);
            }

            vc.put("domainPackage", p);
            vc.put("daoPackage", p.getDaoPackageName());
            for (Entity e : getEntitiesResolved(p.getEntities())) {
                mergeEntity(e);
            }
        }

        // merge non-entity-specific templates
        for (MergeTemplate mt : mergeScheme.getTemplates()) {
            if (mt.isListingEntities()) {
                mergeTemplate(mt, null);
            }
        }
    }

    /**
     * Processes the default classpath (classpathElement), then any additional
     * elements (additionalClasspathElements).
     *
     * @return
     * @throws Exception
     */
    protected Map<String, Group> processClasspaths() throws Exception {
//            final ClassRealm realm = descriptor.getClassRealm();
            List<String> testClasspathElements = project.getTestClasspathElements();
            final URL[] testClasspathURLs = new URL[testClasspathElements.size()];
            int i = 0;
            for (String element : testClasspathElements) {
                File elementFile = new File(element);
                URL u = elementFile.toURI().toURL();
                getLog().info("Classpath component: " + u);
//                realm.addURL(u);
                testClasspathURLs[i++] = u;
            }
            loader = new URLClassLoader(testClasspathURLs, 
                    Thread.currentThread().getContextClassLoader());

        // default classpath element
        processClasspath(classpathElement);

        // and any additional elements:
        for (String s : additionalClasspathElements) {
            processClasspath(s);
        }

        // second pass, reflect classes fully:
        for (Entity e : entities.values()) {
            reflectSecond(e, e.getClazz());
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
//        getLog().info("Classpath is " + classpathElement);
        if (classpathElement.endsWith(".jar")) {
            // TODO: implement JAR scanning
        } else {
            final File dir = new File(classpathElement);

            processPackage(dir, dir);
        }
    }
    
    protected Class getAnnotation(java.lang.reflect.Field field, Class annotationClass) {
        for (Annotation a : field.getDeclaredAnnotations()) {
            getLog().debug(String.format("      --- annotation @%s", a.annotationType().getName()));
            if (annotationClass.getName().equals(a.annotationType().getName())) {
                return a.annotationType();
            }
        }
        return null;
    }
    
    protected boolean isField(java.lang.reflect.Field field, Class annotationClass) {
        return null != field.getAnnotation(annotationClass);
//        return null != getAnnotation(field, annotationClass);
    }
    
    protected static boolean isEntity(Class clazz) {
        for (Annotation a : clazz.getDeclaredAnnotations()) {
            if (javax.persistence.Entity.class.getName().equals(a.annotationType().getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Recursive method to process a folder or file; if a folder, call
     * recursively for each file. If for a file, process the file using the
     * classVisitor.
     *
     * @param root base folder
     * @param dir this (sub-)packages folder
     */
    private void processPackage(File root, File dir) {
        getLog().debug("- package: " + dir);
        if (null != dir && dir.isDirectory()) {
            for (File f : dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory() || pathname.getName().endsWith(".class");
                }
            })) {
                if (f.isDirectory()) {
                    processPackage(root, f);
                } else if (f.getParentFile().getAbsolutePath().replace(File.separatorChar, '.').endsWith(basePackage + '.' + domainPackageName)) {
                    final String simpleName = f.getName().substring(0, f.getName().lastIndexOf(".class"));
                    final String className = String.format("%s.%s.%s", basePackage, domainPackageName, simpleName);
                    getLog().debug(String.format("--- class %s", className));
                    try {
                        Class clazz = loader.loadClass(className);
                        if (!Modifier.isAbstract(clazz.getModifiers()) && isEntity(clazz)) {
                            getLog().debug("@Entity " + clazz.getName());
                            final Entity entity = new Entity();
                            entity.setClazz(clazz);

                            final String packageName = clazz.getPackage().getName();
                            Group group = packages.get(packageName);
                            if (null == group) {
                                group = new Group();
                                group.setName(packageName);
                                packages.put(packageName, group);
                            }

                            group.getEntities().put(simpleName, entity);
                            entities.put(entity.getClassName(), entity);
                        }
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(ProcessDomainMojo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
    
    protected void reflectSecond(Entity e, Class clazz) throws ClassNotFoundException {
        if (clazz.equals(e.getClazz())) {
            getLog().info("@Entity " + clazz.getName());
        }
        else {
            getLog().info("      extends " + clazz.getName());
        }
        // if superclass extends @Entity, reflect superclass first
        if (isEntity(clazz.getSuperclass())) {
            reflectSecond(e, clazz.getSuperclass());
        }
        
        // reflect all fields
        for (java.lang.reflect.Field f : clazz.getDeclaredFields()) {
            reflectField(e, clazz, f);
        }
        
        // build uniqueConstraints
        final javax.persistence.Table table = (javax.persistence.Table) clazz.getAnnotation(javax.persistence.Table.class);
        if (null != table) {
            for (UniqueConstraint uc : table.uniqueConstraints()) {
                final Set<Field> uniqueFieldsSet = new TreeSet<Field>();
                final Set<String> uniqueNamesSet = new TreeSet<String>();
                for (String columnName : uc.columnNames()) {
                    final Field f = e.getAllFields().get(columnName);
                    if (null != f) {
                        uniqueFieldsSet.add(f);
                    }
                    else if (null != e.getParent() && e.getParent().getName().equals(columnName)) {
                        uniqueFieldsSet.add(e.getParent());
                    }
                    else {
                        getLog().warn("Cannot find unique column field for " + columnName);
                    }
                    uniqueNamesSet.add(columnName);
                }
                getLog().info("         @Table( @UniqueConstraint( " + uniqueFieldsSet);
                e.getUniqueFieldsSets().add(uniqueFieldsSet);
                e.getUniqueConstraints().add(uniqueNamesSet);
            }
            getLog().info("                table " + e.getUniqueFieldsSets());
        }

        // build cache information
        final Cached cached = (Cached) clazz.getAnnotation(Cached.class);
        if (null != cached) {
            e.setCached(cached);
        }

    }
    
    protected void reflectField(Entity e, Class clazz, java.lang.reflect.Field field) throws ClassNotFoundException {
        
        Field f = new Field();
        f.setName(field.getName());
        f.setType(field.getType().getName());
        Entity foreign = entities.get(f.getType());
        f.setEntity(foreign);
        getLog().debug(String.format("   --- field %s %s;", f.getSimpleType(), f.getName()));
        
        // map it
        e.getAllFields().put(f.getName(), f);
        
        // is this the @Id
        if (isField(field, javax.persistence.Id.class)) {
            e.setPk(f);
            getLog().info(String.format("   @Id %s %s;", f.getSimpleType(), f.getName()));
        }
        // @Parent?
//        else if (isField(field, pClass)) {
        else if (isField(field, Parent.class)) { //pClass)) {
            Parent p = (Parent) field.getAnnotation(Parent.class);
            e.setParent(f);
            String parentClass = String.format("%s.%s", e.getClazz().getPackage().getName(), p.kind());
            Entity parentEntity = entities.get(parentClass);
            f.setEntity(parentEntity);
            getLog().info(String.format("   @Parent %s %s; kind=%s %s", f.getSimpleType(), f.getName(), parentClass, parentEntity));
        }
        // @Basic?
        else if (isField(field, javax.persistence.Basic.class)) {
            e.getFields().add(f);
            getLog().info(String.format("   @Basic %s %s;", f.getSimpleType(), f.getName()));
            
            // check @UpdatedBy, @Location etc
            if (isField(field, CreatedBy.class)) {
                e.setCreatedBy(f);
            }
            if (isField(field, CreatedDate.class)) {
                e.setCreatedDate(f);
            }
            if (isField(field, UpdatedBy.class)) {
                e.setUpdatedBy(f);
            }
            if (isField(field, UpdatedDate.class)) {
                e.setUpdatedDate(f);
            }
            if (isField(field, GeoLocation.class)) {
                e.setGeoLocation(f);
            }
        }
        // @OneToOne?
        else if (isField(field, javax.persistence.OneToOne.class)) {
            e.getOneToOnes().add(f);
            getLog().info(String.format("   @OneToOne %s %s;", f.getSimpleType(), f.getName()));
        }
        // @ManyToOne?
        else if (isField(field, javax.persistence.ManyToOne.class)) {
            e.getManyToOnes().add(f);
            getLog().info(String.format("   @ManyToOne %s %s;", f.getSimpleType(), f.getName()));
        }
        // @ManyToMany?
        else if (isField(field, javax.persistence.ManyToMany.class)) {
            javax.persistence.ManyToMany m2m = (javax.persistence.ManyToMany) field.getAnnotation(javax.persistence.ManyToMany.class);
//            for (Annotation a : field.getDeclaredAnnotations()) {
//                if (a instanceof javax.persistence.ManyToMany) {
                    e.getManyToManys().add(f);
//                    javax.persistence.ManyToMany m2m = (javax.persistence.ManyToMany) a;
                    f.setEntity(entities.get(m2m.targetEntity().getName()));
                    f.setMappedBy(m2m.mappedBy());
                    getLog().info(String.format("   @ManyToMany %s<%s> %s;", f.getSimpleType(), f.getEntity().getSimpleName(), f.getName()));
//                }
//            }
        }
    }
}
