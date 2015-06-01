package cz.muni.clusterix.entities;

import static java.lang.Math.hypot;

/**
 * This class represents proper motion of an object.
 * 
 * @author Tomas Sezima
 */
public class ProperMotion {
    
    private final float muAlpha;
    private final float muDelta;
    private final Float muAlphaErr;
    private final Float muDeltaErr;    
    
    /**
     * Constustors.
     * 
     * @param muAlpha Proper motion in alpha, mas/year^-1
     * @param muAlphaErr Error of proper motion in alpha, mas/year^-1
     * @param muDelta Proper motion in delta, mas/year^-1
     * @param muDeltaErr Error of proper motion in alpha, mas/year^-1
     */
    public ProperMotion(float muAlpha, Float muAlphaErr, float muDelta, Float muDeltaErr){
        this.muAlpha = muAlpha;
        this.muAlphaErr = muAlphaErr;
        this.muDelta = muDelta;
        this.muDeltaErr = muDeltaErr;        
    }
    
    public ProperMotion(float muAlpha, float muDelta){
        this(muAlpha, null, muDelta, null);        
    }
    
    // public methods
    
    /**
     * This method retrieves mean value of propper motions from both directions
     * (alpha, delta) by formula sqrt(muAlpha^2 + muDelta^2).
     *
     * @return mean of alpha and delta PMs
     */
    public double getMeanMu() {        
        return hypot(muAlpha, muDelta);        
    }

    /**
     * This method retrieves mean value of errors of propper motions from both
     * directions (alpha, delta) by formula sqrt(errAlpha^2 + errDelta^2).
     *
     * @return mean error of alpha and delta PMs
     */
    public double getMeanMuErr() {
        if (muAlphaErr != null && muDeltaErr != null) {
            return hypot(muAlphaErr, muDeltaErr);
        }
        return 0;
    }
    
    
    // getters and setters

    public float getMuAlpha() {
        return muAlpha;
    }

    public float getMuDelta() {
        return muDelta;
    }

    public Float getMuAlphaErr() {
        return muAlphaErr;
    }

    public Float getMuDeltaErr() {
        return muDeltaErr;
    }
    
    
    // equals and hashcode

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Float.floatToIntBits(this.muAlpha);
        hash = 59 * hash + Float.floatToIntBits(this.muDelta);
        hash = 59 * hash + (this.muAlphaErr != null ? this.muAlphaErr.hashCode() : 0);
        hash = 59 * hash + (this.muDeltaErr != null ? this.muDeltaErr.hashCode() : 0);
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
        final ProperMotion other = (ProperMotion) obj;
        if (Float.floatToIntBits(this.muAlpha) != Float.floatToIntBits(other.muAlpha)) {
            return false;
        }
        if (Float.floatToIntBits(this.muDelta) != Float.floatToIntBits(other.muDelta)) {
            return false;
        }
        if (this.muAlphaErr != other.muAlphaErr && (this.muAlphaErr == null || !this.muAlphaErr.equals(other.muAlphaErr))) {
            return false;
        }
        if (this.muDeltaErr != other.muDeltaErr && (this.muDeltaErr == null || !this.muDeltaErr.equals(other.muDeltaErr))) {
            return false;
        }
        return true;
    }            

    @Override
    public String toString() {
        return "ProperMotion{" + "muAlpha=" + muAlpha + ", muDelta=" + muDelta + 
                ", muAlphaErr=" + muAlphaErr + ", muDeltaErr=" + muDeltaErr + '}';
    }
   
}
