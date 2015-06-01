package cz.muni.clusterix.entities;

/**
 * This class represents restrictions placed upon the probability search.
 *
 * @author Tomas Sezima
 */
public class Restrictions {

    // brightness restrictions    
    private Float maxMag;
    // propper motion restrictions
    private Double maxMu;    
    // propper motion error restrictions
    private Float maxMuErr;
    // gaussian dispersion 
    private Double smooth;
    // gamma factor
    private Float gammaCoef;

    
    //constructor
    
    /**
     * Constructor.
     *
     * @param maxMag 2MASS magnitude
     * @param maxMu Maximal propper motion in masyear^-1
     * @param maxMuErr Maximal propper motion error in masyear^-1
     * @param smooth Requested value of a gaussian dispersion
     * @param gammaCoef Gamma (err) factor
     */
    public Restrictions(Float maxMag, Double maxMu, Float maxMuErr, Double smooth, Float gammaCoef) {
        
        if((maxMu != null && maxMu < 0) || 
           (maxMuErr != null && maxMuErr < 0) ||
           (smooth != null && smooth < 0) || 
           (gammaCoef != null && gammaCoef < 0))
            throw new IllegalArgumentException("Proposed restrictions are not valid.");                                
        
        this.maxMag = maxMag;
        this.smooth = smooth;
        this.gammaCoef = gammaCoef;
        this.maxMu = maxMu;
        this.maxMuErr = maxMuErr;        
    }        

    
    //getters and setters           

    public Float getMaxMag() {
        return maxMag;
    }

    public Double getMaxMu() {
        return maxMu;
    }

    public Float getMaxMuErr() {
        return maxMuErr;
    }

    public Double getSmoothParam() {
        return smooth;
    }

    public Float getGammaCoef() {
        return gammaCoef;
    }    

    public void setMaxMu(Double maxMu) {
        this.maxMu = maxMu;
    }

    public void setSmooth(Double smooth) {
        this.smooth = smooth;
    }

    public void setGammaCoef(Float gammaCoef) {
        this.gammaCoef = gammaCoef;
    }

    public void setMaxMag(Float maxMag) {
        this.maxMag = maxMag;
    }

    public void setMaxMuErr(Float maxMuErr) {
        this.maxMuErr = maxMuErr;
    }
    
    
    
    // equals and hashcode

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.maxMag != null ? this.maxMag.hashCode() : 0);
        hash = 47 * hash + (this.maxMu != null ? this.maxMu.hashCode() : 0);
        hash = 47 * hash + (this.maxMuErr != null ? this.maxMuErr.hashCode() : 0);
        hash = 47 * hash + (this.smooth != null ? this.smooth.hashCode() : 0);
        hash = 47 * hash + (this.gammaCoef != null ? this.gammaCoef.hashCode() : 0);
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
        final Restrictions other = (Restrictions) obj;
        if (this.maxMag != other.maxMag && (this.maxMag == null || !this.maxMag.equals(other.maxMag))) {
            return false;
        }
        if (this.maxMu != other.maxMu && (this.maxMu == null || !this.maxMu.equals(other.maxMu))) {
            return false;
        }
        if (this.maxMuErr != other.maxMuErr && (this.maxMuErr == null || !this.maxMuErr.equals(other.maxMuErr))) {
            return false;
        }
        if (this.smooth != other.smooth && (this.smooth == null || !this.smooth.equals(other.smooth))) {
            return false;
        }
        if (this.gammaCoef != other.gammaCoef && (this.gammaCoef == null || !this.gammaCoef.equals(other.gammaCoef))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Restrictions{" + "maxMag=" + maxMag + ", maxMu=" + maxMu + ", maxMuErr=" 
                + maxMuErr + ", smooth=" + smooth + ", gammaCoef=" + gammaCoef + '}';
    }        
    
}
