package cz.muni.clusterix.helpers;

import cz.muni.clusterix.businesstier.StellarField;
import cz.muni.clusterix.entities.Declination;
import cz.muni.clusterix.entities.ProperMotion;
import cz.muni.clusterix.entities.RightAscension;
import cz.muni.clusterix.entities.Star;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Set of tests that is used to assert StarGenerator functionality.
 * @author Tomas Sezima
 */
public class StarGeneratorTest {
    
    public StarGeneratorTest() {}

    /**
     * Test of getTestField method, of class StarGenerator.
     */
    @Test
    public void testGetTestField() {
        System.out.println("getTestField");
        RightAscension ra = new RightAscension(0.0f, 0.0f);
        Declination dec = new Declination(0.0f, 0.0f);        
        StarGenerator instance = new StarGenerator();        
        StellarField result = instance.getTestField(ra, dec, 5.0f, 100);
        
        // test stellar field was generated within proposed restrictions
        if(result == null || result.getStars() == null) fail("Generation process failed.");
        for(Star star : result.getStars()){
            if(star.getRightAscension().getDegrees() > 5.0f / ClusterixConstants.ARCMINS_IN_DEGREE ||
                    star.getDeclination().getDegrees() > 5.0f / ClusterixConstants.ARCMINS_IN_DEGREE){
                fail("Stars were not generated within given restriction.");
            }                                
        }                
        assertEquals(100, result.getStars().size());        
    }

    /**
     * Test of getTestStars method, of class StarGenerator.
     */
    @Test
    public void testGetTestStars() {
        System.out.println("Testing 'getTestStars' mehod.");
        RightAscension ra = new RightAscension(0.0f, 0.0f);
        Declination dec = new Declination(0.0f, 0.0f);     
        ProperMotion motion = new ProperMotion(5.0f, 5.0f);
        StarGenerator instance = new StarGenerator();        
        Set<Star> result = instance.getTestStars(ra, dec, 5.0f, null, motion, 100);
        
        // assert all stars were generated within given restrictions
        if(result == null) fail("Generation process failed.");
        for(Star star : result){
            if(star.getRightAscension().getDegrees() > 5.0f / ClusterixConstants.ARCMINS_IN_DEGREE ||
                    star.getDeclination().getDegrees() > 5.0f / ClusterixConstants.ARCMINS_IN_DEGREE ||
                    star.getProperMotion().getMuAlpha() > 5.0f || star.getProperMotion().getMuDelta() > 5.0f){
                fail("Stars were not generated within given restriction.");
            }                                
        }                
        assertEquals(100, result.size());        
    }
    
}
