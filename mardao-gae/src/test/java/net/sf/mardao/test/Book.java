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
    private String appArg0;
    
    @Basic
    private Collection<String> appArg1;
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAppArg0() {
        return appArg0;
    }

    public void setAppArg0(String appArg0) {
        this.appArg0 = appArg0;
    }

    public Collection<String> getAppArg1() {
        return appArg1;
    }

    public void setAppArg1(Collection<String> appArg1) {
        this.appArg1 = appArg1;
    }

}
