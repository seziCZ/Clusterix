package cz.muni.clusterix.entities;

/** 
 * Describes object that have Proper motion.
 * 
 * @author Tomas Sezima
 */
public interface WithMotion {
    
    /**
     * Retrieves object's proper motion.
     * 
     * @return object's proper motion
     */
    public ProperMotion getProperMotion();
    
}
