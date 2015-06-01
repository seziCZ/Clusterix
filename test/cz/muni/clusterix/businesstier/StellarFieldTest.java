package cz.muni.clusterix.businesstier;

import cz.muni.clusterix.entities.Declination;
import cz.muni.clusterix.entities.ProperMotion;
import cz.muni.clusterix.entities.Restrictions;
import cz.muni.clusterix.entities.RightAscension;
import cz.muni.clusterix.entities.Star;
import cz.muni.clusterix.helpers.StarGenerator;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Set of test related to StellarField entity.
 * 
 * @author Tomas Sezima
 */
public class StellarFieldTest {
    
    private final StarGenerator starGenerator;
    
    public StellarFieldTest() {
        this.starGenerator = new StarGenerator();
    }

    

    /**
     * Test of getFilteredStars method, of class StellarField.
     */
    @Test
    public void testGetFilteredStars() {
        System.out.println("Testing 'getFilteredStars' method.");
        
        // create set of stars with certain properties
        RightAscension firstRa = new RightAscension(180.0f, 0.0f);
        Declination firstDec = new Declination(45.0f, 0.0f);
        ProperMotion firstMotion = new ProperMotion(10.0f, 10.0f);                        
        
        RightAscension secondRa = new RightAscension(75.0f, 0.0f);
        Declination secondDec = new Declination(0.0f, 0.0f);
        ProperMotion secondMotion = new ProperMotion(-5.0f, 1.0f, -5.0f, 1.0f);
        
        Set<Star> stars = starGenerator.getTestStars(firstRa, firstDec, 5.0f, null, firstMotion, 50);        
        stars.addAll(starGenerator.getTestStars(secondRa, secondDec, 20.0f, null, secondMotion, 25));
                
        // assert 
        Restrictions restrictions = new Restrictions(null, 8.0d, 1.0f, null, null);
        StellarField field = new StellarField(stars);
        Set<Star> filteredStars = field.getFilteredStars(restrictions);        
        assertEquals(0, filteredStars.size());
        
        restrictions = new Restrictions(null, 10.0d, 1.5f, null, null);
        filteredStars = field.getFilteredStars(restrictions);
        assertEquals(25, filteredStars.size());
                
    }

    /**
     * Test of getStars method, of class StellarField.
     */
    @Test
    public void testGetStars() {
        System.out.println("Testing 'getStars' method.");
        
        RightAscension ra = new RightAscension(0.0f, 0.0f);
        Declination dec = new Declination(0.0f, 0.0f);
        StellarField testField = starGenerator.getTestField(ra, dec, 1.0f, 345);

        assertEquals(345, testField.getStars().size());

    }

    /**
     * Test of equals method, of class StellarField.
     */
    @Test
    public void testEquals() {
        System.out.println("Testing 'equals' method.");
        
        RightAscension ra = new RightAscension(0.0f, 0.0f);
        Declination dec = new Declination(0.0f, 0.0f);
        StellarField first = starGenerator.getTestField(ra, dec, 1.0f, 345);
        StellarField second = new StellarField(first.getStars());
        assertTrue(first.equals(second));
    }
    
    
}
