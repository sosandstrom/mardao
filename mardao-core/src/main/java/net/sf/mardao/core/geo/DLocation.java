package net.sf.mardao.core.geo;

/**
 *
 * @author os
 */
public class DLocation {
    private float latitude;
    private float longitude;

    public DLocation() {
    }
    
    public DLocation(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
    
    
}
