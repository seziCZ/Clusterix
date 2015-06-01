package cz.muni.clusterix.entities;

import cz.muni.clusterix.helpers.ClusterixConstants;

/**
 * Declination (abbreviated dec; symbol δ) is one of the two angles that locate
 * a point on the celestial sphere in the equatorial coordinate system, the
 * other being hour angle.
 *
 * @author Tomas Sezima
 */
public class Declination {      
    
    public static final int MIN_DEGREES = -90;
    public static final int MAX_DEGREES = 90;

    private int epoch;
    private float declination;
    private float err;
    
    /**
     * Constructor.
     *
     * @param epoch Astronomy epoch     
     * @param degrees Declination as decimal number    
     * @param err Error in delta
     */
    public Declination(int epoch, float degrees, float err) {
        if(degrees < MIN_DEGREES || degrees > MAX_DEGREES){
            throw new IllegalArgumentException("Proposed declination (" +
                degrees + " degrees) is not a valid value.");
        }
        
        this.epoch = epoch;
        this.declination = degrees;
        this.err = err;
    }

    /**
     * Constructor.
     *     
     * @param degrees Declination as decimal number
     * @param err Error in delta
     */
    public Declination(float degrees, float err) {
        this(ClusterixConstants.CURRENT_EPOCH, degrees, err);        
    }
    
    /**
     * Constructor.
     * 
     * @param epoch Astronomy epoch
     * @param deg Degrees of declination
     * @param arcmins Minutes of declination
     * @param arcsec Seconds of desclination
     * @param err Error in delta
     */
    public Declination(int epoch, int deg, int arcmins, float arcsec, float err){
        // negative declination may be proposed        
        float result =  Math.abs(deg) + Math.abs((float) arcmins) / ClusterixConstants.ARCMINS_IN_DEGREE + 
                Math.abs(arcsec) / ClusterixConstants.ARCSECS_IN_DEGREE;
        
        if(result > MAX_DEGREES){
            throw new IllegalArgumentException("Proposed declination (" +
                result + " degrees) is not a valid value.");
        }
        
        this.epoch = epoch;
        this.declination = deg < 0 || arcmins < 0 || arcsec < 0 ? -result : result;
        this.err = err;
    }

    /**
     * Constructor.
     * 
     * @param deg Degrees of declination
     * @param arcmins Minutes of declination 
     * @param arcsecs Seconds of declination
     * @param err Error in delta
     */
    public Declination(int deg, int arcmins, float arcsecs, float err){
        this(ClusterixConstants.CURRENT_EPOCH, deg, arcmins, arcsecs, err);
    }
        

    //setters and getters

    public float getDegrees() {
        return declination;
    }
            

    public double getErr() {
        return err;
    }    

    //equals and hashcode

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.epoch;
        hash = 67 * hash + Float.floatToIntBits(this.declination);
        hash = 67 * hash + Float.floatToIntBits(this.err);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Declination other = (Declination) obj;
        if (this.epoch != other.epoch) {
            return false;
        }
        if (Float.floatToIntBits(this.declination) != Float.floatToIntBits(other.declination)) {
            return false;
        }
        if (Float.floatToIntBits(this.err) != Float.floatToIntBits(other.err)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Declination{" + "epoch=" + epoch + ", declination=" + declination 
                + ", err=" + err + '}';
    }    
    
}
