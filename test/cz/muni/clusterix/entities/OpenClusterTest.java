package cz.muni.clusterix.entities;

import cz.muni.clusterix.businesstier.FieldMask;
import cz.muni.clusterix.businesstier.FieldMask.FieldType;
import cz.muni.clusterix.helpers.Calc;
import cz.muni.clusterix.helpers.ClusterixConstants;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Set of tests related to Open cluster entity.
 * @author tomas
 */
public class OpenClusterTest {        
    
    public OpenClusterTest() {}

    /**
     * Test of getDefaultMask method, of class OpenCluster.
     */
    @Test
    public void testGetDefaultMask() {
        System.out.println("Testing 'getDefaultMask' method.");
        RightAscension ra = new RightAscension(180.0f, 0.0f);
        Declination dec = new Declination(45.0f, 0.0f);
        OpenCluster cluster = new OpenCluster("Test cluster", ra, dec, 60.0f);
        
        // retrieve mask and assert
        FieldMask defaultMask = cluster.getDefaultMask(11);
        assertTrue(defaultMask.getCellSize() * 11 >= 
                cluster.getOutterRadius() * 2 * ClusterixConstants.SEC_IN_MINUTE);
        assertEquals(cluster.getDeclination(), defaultMask.getDeclination());
        assertEquals(cluster.getRightAscension(), defaultMask.getRightAscension());
             
        // assert field coherence
        FieldType[][] mask = defaultMask.getMask();
        Point center = new Point(5, 5);
        for(int i = 0; i < 11; i++){
            for(int u = 0; u < 11; u++){
                double distance = Calc.getDistance(center, new Point(i, u));
                if(mask[i][u].equals(FieldType.CLUSTERFIELD)){                    
                    assertTrue(distance <= cluster.getRadius() * 
                            ClusterixConstants.SEC_IN_MINUTE / defaultMask.getCellSize());
                }else if(mask[i][u].equals(FieldType.FIELD)){
                    assertTrue(distance > cluster.getRadius() * 
                            ClusterixConstants.SEC_IN_MINUTE / defaultMask.getCellSize());
                }
            }
        }
    }    

    /**
     * Test of getOutterRadius method, of class OpenCluster.
     */
    @Test
    public void testGetOutterRadius() {
        System.out.println("Testing 'getOutterRadius' method.");
        RightAscension ra = new RightAscension(180.0f, 0.0f);
        Declination dec = new Declination(45.0f, 0.0f);
        OpenCluster cluster = new OpenCluster("Test cluster", ra, dec, 60.0f);
        assertEquals(cluster.getOutterRadius(), 60.0f * ClusterixConstants.DEFAULT_OUTER_RADIUS, 0.000001);
        
    }    
    
}
