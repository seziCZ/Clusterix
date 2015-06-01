package cz.muni.clusterix.entities;

import cz.muni.clusterix.helpers.ClusterixConstants;

/**
 * Right ascension (abbreviated RA; symbol α) is the angular distance measured
 * eastward along the celestial equator from the vernal equinox to the hour
 * circle of the point in question.
 *
 * @author Tomas Sezima
 */
public class RightAscension {    
    
    public static final int MIN_DEGREES = 0;
    public static final int MAX_DEGREES = 360;
    
    private int epoch;
    private float rightAscension;
    private float err;

    
    //constructors
    
    /**
     * Constructor.
     *
     * @param epoch Astronomy epoch    
     * @param degrees Right ascension
     * @param err Error of PM estimation in masyear^-1
     */
    public RightAscension(int epoch, float degrees, float err) {
        if(degrees < MIN_DEGREES || degrees > MAX_DEGREES){
            throw new IllegalArgumentException("Proposed right ascension (" +
                degrees + " degrees) is not a valid value.");
        }
        
        this.epoch = epoch;
        this.rightAscension = degrees;
        this.err = err;
    }

    /**
     * Constructor.
     *     
     * @param degrees Right ascension
     * @param err Error of PM estimation in masyear^-1
     */
    public RightAscension(float degrees, float err) {
        this(ClusterixConstants.CURRENT_EPOCH, degrees, err);        
    }
    
    /**
     * Constructor.
     * 
     * @param epoch Astronomy epoch
     * @param hours Hours of Right ascension
     * @param mins Minutes of Right ascension
     * @param secs Seconds of Right ascension
     * @param err Error in alpha
     */
    public RightAscension(int epoch, int hours, int mins, float secs, float err){
        this(epoch, (hours + (float) mins / ClusterixConstants.ARCMINS_IN_DEGREE + 
                secs / ClusterixConstants.ARCSECS_IN_DEGREE) * ClusterixConstants.SEC_TO_ARCSEC, err);
    }
    
    /**
     * Constructor.
     * 
     * @param hours Hours of Right acsension
     * @param minutes Minutes of Right acension
     * @param secs Seconds of Right ascension
     * @param err Error in alpha
     */
    public RightAscension(int hours, int minutes, float secs, float err){
        this(ClusterixConstants.CURRENT_EPOCH, hours, minutes, secs, err);
    }

    
    //setters and getters
    
    public int getEpoch() {
        return epoch;
    }

    public float getDegrees() {
        return rightAscension;
    }        
    
    public double getErr() {
        return err;
    }    
    
    
    //equals and hashcode

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.epoch;
        hash = 53 * hash + Float.floatToIntBits(this.rightAscension);
        hash = 53 * hash + Float.floatToIntBits(this.err);
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
        final RightAscension other = (RightAscension) obj;
        if (this.epoch != other.epoch) {
            return false;
        }
        if (Float.floatToIntBits(this.rightAscension) != Float.floatToIntBits(other.rightAscension)) {
            return false;
        }
        if (Float.floatToIntBits(this.err) != Float.floatToIntBits(other.err)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RightAscension{" + "epoch=" + epoch + ", rightAscension=" + rightAscension + 
                ", err=" + err + '}';
    }        

}
