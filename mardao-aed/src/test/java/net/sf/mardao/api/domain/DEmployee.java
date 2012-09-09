package net.sf.mardao.api.domain;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import net.sf.mardao.api.Parent;

/**
 *
 * @author os
 */
@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"fingerprint"}))
public class DEmployee extends DLongEntity {
    @Id
    private Long id;

    @Parent(kind="DOrganization")
    private Serializable organizationKey;
    
    @Basic
    private String fingerprint;
    
    @Basic
    private String nickname;
    
    @ManyToOne
    private DEmployee manager;
    
    @ManyToMany(mappedBy="members", targetEntity=DGroup.class)
    private Collection<DGroup> groups;
    
    @Override
    public Long getSimpleKey() {
        return id;
    }

    @Override
    public void setSimpleKey(Long simpleKey) {
        this.id = simpleKey;
    }

    @Override
    public Serializable getParentKey() {
        return organizationKey;
    }

    @Override
    public void setParentKey(Object parentKey) {
        this.organizationKey = (Serializable) parentKey;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getOrganizationKey() {
        return organizationKey;
    }

    public void setOrganizationKey(Serializable organizationKey) {
        this.organizationKey = organizationKey;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public DEmployee getManager() {
        return manager;
    }

    public void setManager(DEmployee manager) {
        this.manager = manager;
    }

    public Collection<DGroup> getGroups() {
        return groups;
    }

    public void setGroups(Collection<DGroup> groups) {
        this.groups = groups;
    }
    
    
}
