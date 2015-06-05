package cz.muni.clusterix.entities;

/**
 * Helper that allows programmer to transfer statistical information about
 * proper motions in both axis (alpha, delta) in a single entity.
 *
 * @author Tomas Sezima
 */
public class PmStat {

    private double alphaStat;
    private double deltaStat;
    
    /**
     * Constructor.
     *
     * @param alphaStat Statistical information in alpha
     * @param deltaStat Statistical information in delta
     */
    public PmStat(double alphaStat, double deltaStat) {
        this.alphaStat = alphaStat;
        this.deltaStat = deltaStat;
    }

        
    public void addToAlpha(double value){
        this.alphaStat += value;
    }
    
    public void addToDelta(double value){
        this.deltaStat += value;
    }
    
    
    // getters and setters
    
    public double getAlphaStat() {
        return alphaStat;
    }

    public double getDeltaStat() {
        return deltaStat;
    }        

    public void setAlphaStat(double alphaStat) {
        this.alphaStat = alphaStat;
    }

    public void setDeltaStat(double deltaStat) {
        this.deltaStat = deltaStat;
    }
        
    
    
    // hashcode and equals
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PmStat other = (PmStat) obj;
        if (Double.doubleToLongBits(this.alphaStat) != Double.doubleToLongBits(other.alphaStat)) {
            return false;
        }
        if (Double.doubleToLongBits(this.deltaStat) != Double.doubleToLongBits(other.deltaStat)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.alphaStat) ^ (Double.doubleToLongBits(this.alphaStat) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.deltaStat) ^ (Double.doubleToLongBits(this.deltaStat) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "PmStat{" + "alphaStat=" + alphaStat + ", deltaStat=" + deltaStat + '}';
    }        
    
}
