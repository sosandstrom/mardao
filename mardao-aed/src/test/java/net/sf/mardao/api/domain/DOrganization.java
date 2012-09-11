package net.sf.mardao.api.domain;

import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import net.sf.mardao.api.geo.DLocation;
import net.sf.mardao.api.geo.GeoModel;

/**
 *
 * @author os
 */
@Entity
public class DOrganization extends DLongEntity implements GeoModel {
    @Id
    private Long id;
    
    @Basic
    private String name;
    
    @Basic
    private DLocation officeLocation;
    
    @Basic
    private Collection<Long> _geoboxes;

    @Override
    public Long getSimpleKey() {
        return id;
    }

    @Override
    public void setSimpleKey(Long simpleKey) {
        this.id = simpleKey;
    }

    @Override
    public DLocation getLocation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setGeoboxes(Collection<Long> _geoboxes) {
        this._geoboxes = _geoboxes;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DLocation getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(DLocation officeLocation) {
        this.officeLocation = officeLocation;
    }

    public Collection<Long> get_geoboxes() {
        return _geoboxes;
    }

    public void set_geoboxes(Collection<Long> _geoboxes) {
        this._geoboxes = _geoboxes;
    }
    
}
