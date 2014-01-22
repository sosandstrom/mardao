package net.sf.mardao.test;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Entity;
import net.sf.mardao.core.domain.AbstractLongEntity;

/**
 *
 * @author os
 */
@Entity
public class Book extends AbstractLongEntity {
    @Basic
    private String title;
    
    @Basic
    private Collection<String> roles;
    
    @Basic
    private Collection<Long> groups;
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }

   public Collection<Long> getGroups() {
        return groups;
    }

    public void setGroups(Collection<Long> groups) {
        this.groups = groups;
    }

  

}
