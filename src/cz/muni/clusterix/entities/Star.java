package cz.muni.clusterix.entities;

/**
 * Entity "Star" represents single star and its basic properties, such as
 * magnitude, possition and proper motion.
 *
 * @author Tomas Sezima
 */
public class Star implements WithCoordinates, WithMotion{

    private final int no;
    private Float magnitude;
    private final Declination declination;
    private final RightAscension rightAscension;
    private final ProperMotion properMotion;    
    
    // Cluster membership probability
    private Float probability;        
    private Boolean clusterStar;

        
    /**
     * Constructor.
     *
     * @param no Star id
     * @param magnitude 2MASS magnitude
     * @param ra Right ascension of the star
     * @param dec Declination of the star     
     * @param motion Proper motion of the star    
     */
    public Star(int no, Float magnitude, RightAscension ra, Declination dec, ProperMotion motion) {
        if(ra == null || dec == null || motion == null){
            throw new IllegalArgumentException("Null values were proposed to Star constuctor.");
        }            
            
        this.no = no;
        this.magnitude = magnitude;
        this.declination = dec;
        this.rightAscension = ra;
        this.properMotion = motion;        
    }

    
    // getters

    @Override
    public Declination getDeclination() {
        return declination;
    }

    @Override
    public RightAscension getRightAscension() {
        return rightAscension;
    }

    @Override
    public ProperMotion getProperMotion() {
        return properMotion;
    }
    
    public int getNo() {
        return no;
    }

    public float getMagnitude() {
        return magnitude;
    }    

    public Float getProbability() {
        return probability;
    }

    public Boolean isClusterStar() {
        return clusterStar;
    }

    // setters
    
    public void setProbability(float probability) {
        this.probability = probability;
    }       

    public void setIsClusterStar(boolean isClusterStar) {
        this.clusterStar = isClusterStar;
    }
    
    
    // eq and hashcode

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.no;
        hash = 47 * hash + (this.magnitude != null ? this.magnitude.hashCode() : 0);
        hash = 47 * hash + (this.declination != null ? this.declination.hashCode() : 0);
        hash = 47 * hash + (this.rightAscension != null ? this.rightAscension.hashCode() : 0);
        hash = 47 * hash + (this.properMotion != null ? this.properMotion.hashCode() : 0);
        hash = 47 * hash + (this.probability != null ? this.probability.hashCode() : 0);
        hash = 47 * hash + (this.clusterStar != null ? this.clusterStar.hashCode() : 0);
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
        final Star other = (Star) obj;
        if (this.no != other.no) {
            return false;
        }
        if (Float.floatToIntBits(this.magnitude) != Float.floatToIntBits(other.magnitude)) {
            return false;
        }
        if (this.declination != other.declination && (this.declination == null || !this.declination.equals(other.declination))) {
            return false;
        }
        if (this.rightAscension != other.rightAscension && (this.rightAscension == null || !this.rightAscension.equals(other.rightAscension))) {
            return false;
        }
        if (this.properMotion != other.properMotion && (this.properMotion == null || !this.properMotion.equals(other.properMotion))) {
            return false;
        }
        if (this.probability != other.probability && (this.probability == null || !this.probability.equals(other.probability))) {
            return false;
        }
        if (this.clusterStar != other.clusterStar && (this.clusterStar == null || !this.clusterStar.equals(other.clusterStar))) {
            return false;
        }
        return true;
    }   
    
    @Override
    public String toString() {
        return "Star{" + "no=" + no + ", magnitude=" + magnitude + ", declination=" + 
                declination + ", rightAscension=" + rightAscension + ", properMotion=" + 
                properMotion + ", probability=" + probability + ", clusterStar=" + clusterStar + '}';
    }
    
}
