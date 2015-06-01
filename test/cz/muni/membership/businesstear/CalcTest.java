package cz.muni.membership.businesstear;

import cz.muni.clusterix.helpers.Calc;
import java.util.HashSet;
import static java.lang.Math.*;
import cz.muni.clusterix.entities.Declination;
import cz.muni.clusterix.entities.ProperMotion;
import cz.muni.clusterix.entities.Point;
import cz.muni.clusterix.entities.RightAscension;
import cz.muni.clusterix.entities.Star;
import cz.muni.clusterix.entities.PmStat;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Set of unit tests for FieldMask class
 *
 * @author Tomas Sezima
 */
public class CalcTest {

    public CalcTest() {}    

    /**
     * Test of getSmoothParam method, of class Calc.
     */
    @Test
    public void testGetSmoothParam() {
        System.out.println("Testing 'getSmoothParam' method...");
        Set<Star> stars = getTestStars();        
        double result = Calc.getSmoothParam(stars);
        assertEquals(5.907553143, result, 0.000001);
    }

    /**
     * Test of getPmDeviation method, of class Calc.
     */
    @Test
    public void testGetPmDeviation() {
        System.out.println("Testing 'getPmDeviation' method...");
        Set<Star> stars = getTestStars();
        PmStat mean = new PmStat(3, 3);
        PmStat result = Calc.getPmDeviation(stars, mean);        
        assertEquals(sqrt((float) 8 / 3), result.getAlphaStat(), 0.000001);
        assertEquals(sqrt((float) 342 / 3), result.getDeltaStat(), 0.000001);

    }

    /**
     * Test of getPmMean method, of class Calc.
     */
    @Test
    public void testGetPmMean() {
        System.out.println("Testing 'getPmMean' method...");
        Set<Star> stars = getTestStars();
        PmStat result = Calc.getPmMean(stars);        
        assertEquals(3.0f, result.getAlphaStat(), 0.000001);
        assertEquals(7.0f, result.getDeltaStat(), 0.000001);        
    }

    /**
     * Test of getRand method, of class Calc.
     */
    @Test
    public void testGetRand() {
        System.out.println("Testing 'getRand' method...");        
        for (int i = 0; i < 100; i++) {
            double result = Calc.getRand(0, i);
            assertTrue(result <= i);
        }
    }

    /**
     * Test of getRandGauss method, of class Calc.
     */
    @Test
    public void testGetRandGauss() {
        System.out.println("Testing 'getRandGauss' method...");        
        int insideDev = 0;
        for (int i = 0; i < 1000; i++) {
            double res = Calc.getRandGauss(2, 30);
            if (res < 32 && res > 28) {
                insideDev++;
            }
        }
        assertTrue(insideDev >= 650);
    }

    /**
     * Test of getDistance method, of class Calc.
     */
    @Test
    public void testGetDistance_4args() {
        System.out.println("Testing 'getDistance' method...");
        RightAscension ra1 = new RightAscension(10.0f, 0.0f);
        Declination de1 = new Declination(15.0f, 0.0f);
        RightAscension ra2 = new RightAscension(350.0f, 0.0f);
        Declination de2 = new Declination(40.0f, 0.0f);      
        ProperMotion motion = new ProperMotion(0.0f, 0.0f);
        Star star1 = new Star(1, Float.NaN, ra1, de1, motion);
        Star star2 = new Star(2, Float.NaN, ra2, de2, motion);
        
        // assert equals
        double result = Calc.getDistance(star1, star2);
        assertEquals(hypot(-340, -25), result, 0.0000001);
    }

    /**
     * Test of getDistance method, of class Calc.
     */
    @Test
    public void testGetDistance_Point_Point() {
        System.out.println("Testing 'getDistance (between points)' method...");
        Point first = new Point(0, 0);
        Point second = new Point(1, 1);        
        double result = Calc.getDistance(first, second);
        assertEquals(sqrt(2), result, 0);
        
        first = new Point(0, 0);
        second = new Point(3, 2);        
        result = Calc.getDistance(first, second);
        assertEquals(sqrt(13), result, 0);
        
        first = new Point(8, 7);
        second = new Point(-9, 3);                
        result = Calc.getDistance(first, second);
        assertEquals(hypot(17, 4), result, 0.000001);
    }

    /**
     * Test of square method, of class Calc.
     */
    @Test
    public void testSquare() {
        System.out.println("Testing 'square' method...");
        assertEquals(pow(243.3, 2), Calc.square(243.3), 0.000001);
        assertEquals(pow(-23.234, 2), Calc.square(-23.234), 0.000001);
        assertEquals(pow(0.352, 2), Calc.square(0.352), 0.000001);        
    }

    //private helpers
    
    private Set<Star> getTestStars() {
        Set<Star> stars = new HashSet<Star>();
        RightAscension ra = new RightAscension(0, 0, 0.0f, 0.0f);
        Declination dec = new Declination(0, 0, 0.0f, 0.0f);
        ProperMotion star1 = new ProperMotion(5.0f, 20.0f);
        stars.add(new Star(1, 0f, ra, dec, star1));
        ProperMotion star2 = new ProperMotion(1.0f, 5.0f);
        stars.add(new Star(2, 0f, ra, dec, star2));
        ProperMotion star3 = new ProperMotion(3.0f, -4.0f);
        stars.add(new Star(3, 0f, ra, dec, star3));
        return stars;
    }
}
