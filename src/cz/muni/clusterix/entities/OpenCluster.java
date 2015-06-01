package cz.muni.clusterix.entities;

import cz.muni.clusterix.businesstier.FieldMask;
import cz.muni.clusterix.businesstier.FieldMask.FieldType;
import cz.muni.clusterix.helpers.Calc;
import cz.muni.clusterix.helpers.ClusterixConstants;
import org.apache.log4j.Logger;

/**
 * Entity "OpenCluster" holds information about open cluster which is
 * in center of our interest. No stars are assigned to the entity, as it's
 * the application's primary objective (no pre-separation of sample is usually known).
 * 
 * @author Tomas Sezima
 */
public class OpenCluster implements WithCoordinates, WithMotion{    
    
    private static final Logger log = Logger.getLogger(OpenCluster.class.getName());
    
    // name
    private String name;
    private ProperMotion motion;
    // center description
    private final Declination centerDec;
    private final RightAscension centerRa;    
    // radiuses describing open cluster in arcmins
    private float radius;
    private float outterRadius;

    
    // costructor
    
    /**
     * Constructor.
     * 
     * @param name Cluster name
     * @param centerRa Cluster central coordinates in alpha
     * @param centerDec Cluster central coordinates in delta
     * @param radius Cluster radius (arcmins)
     * @param outerRadius Cluster outer radius (arcmins)
     */
    public OpenCluster(String name, RightAscension centerRa, Declination centerDec, 
            float radius, float outerRadius){
        this.name = name;
        this.centerRa = centerRa;
        this.centerDec = centerDec;
        this.radius = radius;
        this.outterRadius = outerRadius;
    }
    
    
    public OpenCluster(String name, RightAscension centerRa, Declination centerDec, float radius) {
        this(name, centerRa, centerDec, radius, radius * ClusterixConstants.DEFAULT_OUTER_RADIUS);        
    }                
    

    // public helpers   
    
    /**
     * This method creates mask, that describes default field separation. Stars within
     * cluster radius are marked as "cluster + field", stars at distance up to 
     * 'radius' * 'outerRadius' are used as "field" stars.
     * 
     * @param matrixSize Density of the mask to be created
     * @return implicit FieldMask     
     */
    public FieldMask getDefaultMask(int matrixSize){
        
        if (matrixSize % 2 == 0) {
            //mask has to be symetric in order to ease calculations   
            log.error("An attempt was made to create mask with even size.");
            throw new IllegalArgumentException("Matrix size has to be odd.");
        }
                
        float maskDiameter = outterRadius * 2 * ClusterixConstants.SEC_IN_MINUTE; // in arcsecs
        float cellSize = maskDiameter / matrixSize;

        // create mask        
        Point center = new Point(matrixSize / 2, matrixSize / 2);
        FieldType[][] mask = new FieldType[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int u = 0; u < matrixSize; u++) {
                Point pos = new Point(i, u);       
                // mask is symetrical, there is no need to invert X and Y coordinates
                if (Calc.getDistance(pos, center) * cellSize <= radius * ClusterixConstants.SEC_IN_MINUTE) {
                    mask[i][u] = FieldType.CLUSTERFIELD;
                }else if (Calc.getDistance(pos, center) * cellSize > radius * ClusterixConstants.SEC_IN_MINUTE
                        && Calc.getDistance(pos, center) * cellSize <= outterRadius * ClusterixConstants.SEC_IN_MINUTE) {
                    mask[i][u] = FieldType.FIELD;
                }else mask[i][u] = FieldType.NONE;                                
            }
        }
        return new FieldMask(matrixSize, cellSize, mask, centerRa, centerDec);
    }
       
    
    // setters and getters

    @Override
    public Declination getDeclination() {
        return centerDec;
    }

    @Override
    public RightAscension getRightAscension() {
        return centerRa;
    }

    @Override
    public ProperMotion getProperMotion() {
        return motion;
    }
    
    public String getName() {
        return name;
    }    

    // radius in arcmins
    public float getRadius() {
        return radius;
    }

    // outer radius in arcmins
    public float getOutterRadius() {
        return outterRadius;
    }

    public void setMotion(ProperMotion motion) {
        this.motion = motion;
    }

    public void setOutterRadius(float outterRadius) {
        this.outterRadius = outterRadius;
    }
    
    
    // equals and hashcode

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 19 * hash + (this.centerDec != null ? this.centerDec.hashCode() : 0);
        hash = 19 * hash + (this.centerRa != null ? this.centerRa.hashCode() : 0);
        hash = 19 * hash + (this.motion != null ? this.motion.hashCode() : 0);
        hash = 19 * hash + Float.floatToIntBits(this.radius);
        hash = 19 * hash + Float.floatToIntBits(this.outterRadius);
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
        final OpenCluster other = (OpenCluster) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.centerDec != other.centerDec && (this.centerDec == null || !this.centerDec.equals(other.centerDec))) {
            return false;
        }
        if (this.centerRa != other.centerRa && (this.centerRa == null || !this.centerRa.equals(other.centerRa))) {
            return false;
        }
        if (this.motion != other.motion && (this.motion == null || !this.motion.equals(other.motion))) {
            return false;
        }
        if (Float.floatToIntBits(this.radius) != Float.floatToIntBits(other.radius)) {
            return false;
        }
        if (Float.floatToIntBits(this.outterRadius) != Float.floatToIntBits(other.outterRadius)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OpenCluster{" + "name=" + name + ", motion=" + motion + ", centerDec=" + 
                centerDec + ", centerRa=" + centerRa + ", radius=" + radius + ", outterRadius=" + 
                outterRadius + '}';
    }

    
        
}
