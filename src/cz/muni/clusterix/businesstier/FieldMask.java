package cz.muni.clusterix.businesstier;

import cz.muni.clusterix.entities.Declination;
import cz.muni.clusterix.entities.Point;
import cz.muni.clusterix.entities.RightAscension;
import cz.muni.clusterix.entities.Star;
import cz.muni.clusterix.entities.WithCoordinates;
import cz.muni.clusterix.helpers.ClusterixConstants;
import static java.lang.Math.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * An entity, that allows user to define "cluster + field" and "field" samples
 * without relying on standard circular division techniques. Detail description
 * of the methodology together with corresponding mathematical background may be
 * found at http://is.muni.cz/th/324922/fi_m/.
 *
 * @author Tomas Sezima
 */
public class FieldMask implements WithCoordinates{
        
    private static final Logger log = Logger.getLogger(FieldMask.class.getName());
    
    /**
     * Enumeration that describes allowed values of stellar field types.
     */
    public enum FieldType{ 
        CLUSTERFIELD, 
        FIELD, 
        NONE 
    }

    // mask density
    private int density;
    // size of individual cells in arcseconds
    private float cellSize;    
    // actual mask
    private FieldType[][] mask;
    //center coordinates of mask (so that we can fit it to a stellar field)
    RightAscension ra;
    Declination dec;
    

    /**
     * Constructor. Mask parameter is described by 2 dimensional array, that
     * holds information about performed selection. Central coordinates are
     * related to the mask itself, not the underlaying cluster.
     *
     * @param density Mask density
     * @param cellsize Size of single cell in arcseconds
     * @param mask FieldType mask
     * @param ra Right ascension of center of the mask
     * @param dec Declination of center of the mask     
     */
    public FieldMask(int density, float cellsize, FieldType[][] mask,
            RightAscension ra, Declination dec){
        if (density % 2 == 0) {
            //mask has to be symetric in order to ease calculations   
            log.error("An attempt was made to create mask with even size.");
            throw new IllegalArgumentException("Matrix size has to be odd.");
        }
        this.density = density;
        this.cellSize = cellsize;
        this.mask = mask;
        this.ra = ra;
        this.dec = dec;
    }    

    /**
     * Retrieves all stars whose coordinates are under the mask cells that are
     * marked by proposed marker.
     * 
     * @param allStars Set of stars
     * @param markers Set of mask's markers
     * @return Stars laying under relevant cells
     */
    public Set<Star> getMarkedStars(Set<Star> allStars, Set<FieldType> markers) {
        int matrixMean = density / 2;
        Set<Star> selectedStars = new HashSet<Star>();
        if(allStars != null){
            for (Star star : allStars) {
                // retrieve star possition in the mask
                Point pos = getMaskCoords(star);
                // check what mask value corresponds to retrieved point                                                
                if (abs(pos.getX()) <= matrixMean && abs(pos.getY()) <= matrixMean
                        && markers.contains(mask[matrixMean + pos.getX()][matrixMean + pos.getY()])) {
                    selectedStars.add(star);
                }
            }
        }        
        return selectedStars;
    
    }
    

    /**
     * Retrieves ratio between areas taken by proposed field types.
     * 
     * @param first First field type
     * @param second Second field type
     * @return Ration between volumes of mask cells marked as "first" and "second"
     */
    public float getRatio(FieldType first, FieldType second){
        int firstMarkers = 0;
        int secondMarkers = 0;
        for (int i = 0; i < density; i++) {
            for (int u = 0; u < density; u++) {
                if (mask[i][u] == first) {
                    firstMarkers++;
                }else if (mask[i][u] == second) {
                    secondMarkers++;
                }
            }
        }        
        
        return secondMarkers != 0 ? (float) firstMarkers / secondMarkers : Float.NaN;    
    }        

    
    // private helpers
    
    /**
     * Retrieve coordinates of mask cell that hosts object defined by
     * parameter. Note, that X axes of 'this' mask is represented by second array 
     * parameter and vice versa, i.e. mask[0][1] will return second element in 
     * first mask's row, which coresponds to x = 1, y = 0 coordinates.
     *
     * @param WithCoordinates Object whose coordinates are to be found
     * @return Coordinates of mask cell star belongs to
     */
    private Point getMaskCoords(WithCoordinates object) {
        float xDiff = ra.getDegrees() - object.getRightAscension().getDegrees();
        float yDiff = dec.getDegrees() - object.getDeclination().getDegrees();        
        int x = (int) round(xDiff * (float) ClusterixConstants.ARCSECS_IN_DEGREE / cellSize);
        int y = (int) round(yDiff * (float) ClusterixConstants.ARCSECS_IN_DEGREE/ cellSize);                
        // invert axes so that they reflect mask's axes
        return new Point(y, x);
    }

    
    //getters and setters
    
    @Override
    public RightAscension getRightAscension() {
        return ra;
    }
    
    @Override
    public Declination getDeclination() {
        return dec;
    }
    
    /**
     * Returns size of a single mask cell in arcseconds.
     *
     * @return cellsize in arcseconds
     */
    public float getCellSize() {
        return cellSize;
    }    

    public void setDeclination(Declination dec) {
        this.dec = dec;
    }

    public FieldType[][] getMask() {
        return mask;
    }

    public void setMask(FieldType[][] mask) {
        this.mask = mask;
    }

    public int getDensity() {
        return density;
    }

    public void setDensity(int matrixSize) {
        this.density = matrixSize;
    }    

    public void setRightAscension(RightAscension ra) {
        this.ra = ra;
    }

    
    //equals and hashcode
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FieldMask other = (FieldMask) obj;
        if (this.density != other.density) {
            return false;
        }
        if (Double.doubleToLongBits(this.cellSize) != Double.doubleToLongBits(other.cellSize)) {
            return false;
        }
        if (!Arrays.deepEquals(this.mask, other.mask)) {
            return false;
        }
        if (this.ra != other.ra && (this.ra == null || !this.ra.equals(other.ra))) {
            return false;
        }
        if (this.dec != other.dec && (this.dec == null || !this.dec.equals(other.dec))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + this.density;
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.cellSize) ^ (Double.doubleToLongBits(this.cellSize) >>> 32));
        hash = 47 * hash + Arrays.deepHashCode(this.mask);
        hash = 47 * hash + (this.ra != null ? this.ra.hashCode() : 0);
        hash = 47 * hash + (this.dec != null ? this.dec.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "FieldMask{" + "matrixSize=" + density + ", cellSize=" + cellSize + 
                ", mask=" + mask + ", ra=" + ra + ", dec=" + dec + '}';
    }    

}
