package cz.muni.clusterix.entities;

/**
 * Describes entities that could be located on stellar field using
 * Right Acension and Declination.
 * 
 * @author Tomas Sezima
 */
public interface WithCoordinates {
    
    /**
     * Retrieves object's right ascension.
     * 
     * @return Object right ascension 
     */
    public RightAscension getRightAscension();
    
    /**
     * Retrieves object's declination.
     * 
     * @return object's declination
     */
    public Declination getDeclination();
    
}
