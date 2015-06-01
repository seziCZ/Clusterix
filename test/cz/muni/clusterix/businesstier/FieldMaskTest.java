package cz.muni.clusterix.businesstier;

import cz.muni.clusterix.businesstier.FieldMask.FieldType;
import cz.muni.clusterix.entities.Declination;
import cz.muni.clusterix.entities.RightAscension;
import cz.muni.clusterix.entities.Star;
import cz.muni.clusterix.helpers.ClusterixConstants;
import cz.muni.clusterix.helpers.StarGenerator;
import java.util.EnumSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A set of tests related to FieldMask entity.
 * @author Tomas Sezima
 */
public class FieldMaskTest {
    
    private static final float DEFAULT_CELLSIZE = 150; // arcsec
    
    private StarGenerator starGenerator;    
    
    public FieldMaskTest() {
        this.starGenerator = new StarGenerator();
    }

    /**
     * Test of getMarkedStars method, of class FieldMask.
     */
    @Test
    public void testGetMarkedStars() {
        System.out.println("Testing 'getMarkedStars' method.");
        RightAscension ra = new RightAscension(10.0f, 0.0f);
        Declination dec = new Declination(-5.0f, 0.0f);
        FieldMask testMask = getTestFieldMask(ra, dec);
        
        RightAscension testRa = null;
        Declination testDec = null;
        
        //  C N F
        // (F)C F   i.e. field-stars
        //  F N C
        float raDeg = ra.getDegrees() + DEFAULT_CELLSIZE / ClusterixConstants.ARCSECS_IN_DEGREE;        
        testRa = new RightAscension(raDeg, 0.0f);        
        Set<Star> stars = starGenerator.getTestStars(testRa, dec, 75.0f / 
                ClusterixConstants.SEC_IN_MINUTE, null, null, 79);
        //  C N(F)
        //  F C F   i.e. field-stars
        //  F N C
        float decDeg = dec.getDegrees() + DEFAULT_CELLSIZE / ClusterixConstants.ARCSECS_IN_DEGREE;
        raDeg = ra.getDegrees() - DEFAULT_CELLSIZE / ClusterixConstants.ARCSECS_IN_DEGREE;
        testRa = new RightAscension(raDeg, 0.0f);
        testDec = new Declination(decDeg, 0.0f);        
        stars.addAll(starGenerator.getTestStars(testRa, testDec, 75.0f / 
                ClusterixConstants.SEC_IN_MINUTE, null, null, 83));
        //  C N F
        //  F C F   i.e. cluster-field stars
        //  F N(C)
        raDeg = ra.getDegrees() - DEFAULT_CELLSIZE / ClusterixConstants.ARCSECS_IN_DEGREE;
        decDeg = dec.getDegrees() - DEFAULT_CELLSIZE / ClusterixConstants.ARCSECS_IN_DEGREE;
        testRa = new RightAscension(raDeg, 0.0f);
        testDec = new Declination(decDeg, 0.0f);
        stars.addAll(starGenerator.getTestStars(testRa, testDec, 75.0f / 
                ClusterixConstants.SEC_IN_MINUTE, null, null, 89));
        //  C(N)F
        //  F C F   i.e. non-marked stars
        //  F N C
        decDeg = dec.getDegrees() + DEFAULT_CELLSIZE / ClusterixConstants.ARCSECS_IN_DEGREE;
        testDec = new Declination(decDeg, 0.0f);
        stars.addAll(starGenerator.getTestStars(ra, testDec, 75.0f / 
                ClusterixConstants.SEC_IN_MINUTE, null, null, 97));
        //  C N F
        //  F C F   i.e. non-marked stars
        //  F(N)C
        decDeg = dec.getDegrees() - DEFAULT_CELLSIZE / ClusterixConstants.ARCSECS_IN_DEGREE;
        testDec = new Declination(decDeg, 0.0f);
        stars.addAll(starGenerator.getTestStars(ra, testDec, 75.0f / 
                ClusterixConstants.SEC_IN_MINUTE, null, null, 101));
        
        // assertions
        Set<Star> fieldStars = testMask.getMarkedStars(stars, 
                EnumSet.of(FieldType.FIELD));
        assertEquals(162, fieldStars.size());        
        Set<Star> clusterFieldStars = testMask.getMarkedStars(stars, 
                EnumSet.of(FieldType.CLUSTERFIELD));
        assertEquals(89, clusterFieldStars.size());        
        Set<Star> noMarkStars = testMask.getMarkedStars(stars, 
                EnumSet.of(FieldType.NONE));
        assertEquals(198, noMarkStars.size());        
        Set<Star> mixedStars = testMask.getMarkedStars(stars, 
                EnumSet.of(FieldType.CLUSTERFIELD, FieldType.FIELD));
        assertEquals(251, mixedStars.size()); 
        Set<Star> allStars = testMask.getMarkedStars(stars, 
                EnumSet.of(FieldType.CLUSTERFIELD, FieldType.FIELD, FieldType.NONE));
        assertEquals(449, allStars.size());
    }

    /**
     * Test of getAreaFactor method, of class FieldMask.
     */
    @Test
    public void testGetAreaFactor() throws Exception {
        System.out.println("Testing 'getAreaFactor' method.");
        RightAscension ra = new RightAscension(10.0f, 0.0f);
        Declination dec = new Declination(-5.0f, 0.0f);
        FieldMask testMask = getTestFieldMask(ra, dec);
        assertEquals(0.75f, testMask.getAreaFactor(), 0.000001);
    }

    /**
     * Test of getCellSize method, of class FieldMask.
     */
    @Test
    public void testGetCellSize() {
        System.out.println("Testing 'getCellSize' method");        
        RightAscension ra = new RightAscension(10.0f, 0.0f);
        Declination dec = new Declination(-5.0f, 0.0f);
        FieldMask testMask = getTestFieldMask(ra, dec);
        assertEquals(DEFAULT_CELLSIZE, testMask.getCellSize(), 0.000001);
    }
    

    /**
     * Test of getDensity method, of class FieldMask.
     */
    @Test
    public void testGetDensity() {
        System.out.println("Testing 'getDensity' method.");
        RightAscension ra = new RightAscension(10.0f, 0.0f);
        Declination dec = new Declination(-5.0f, 0.0f);
        FieldMask testMask = getTestFieldMask(ra, dec);        
        assertEquals(3, testMask.getDensity());
    }    
    
    
    //private helpers
    
    private FieldMask getTestFieldMask(RightAscension ra, Declination dec) {               
        FieldType[][] mask = new FieldType[3][3];
        
        //create mask defined by scheme
        //  C N F
        //  F C F
        //  F N C
        for (int i = 0; i < 3; i++) {
            for (int u = 0; u < 3; u++) {
                if(i == u){
                    mask[i][u] = FieldType.CLUSTERFIELD;
                }else if(u == 1){
                    mask[i][u] = FieldType.NONE;
                }else{
                    mask[i][u] = FieldType.FIELD;
                }
            }
        }
        return new FieldMask(mask.length, DEFAULT_CELLSIZE, mask, ra, dec);
    }
    
    
    
}
