package cz.muni.clusterix.businesstier;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests related to PmFrequency entity.
 * @author Tomas Sezima
 */
public class PmFrequencyTest {
    
    private static final float DEFAULT_SMOOTH_PARAM = 1.5f;
    
    public PmFrequencyTest() {}        

    /**
     * Test of subtract method, of class PmFrequency.
     */
    @Test
    public void testSubtract() {
        System.out.println("Testing 'substract' method.");
        PmFrequency first = getFunctionOf(5, -2.5f);
        PmFrequency second = getFunctionOf(5, 1.2f);
        first.subtract(second);
        // assert subtraction was successfull
        for(int i = 0; i < 5; i++){
            for(int u = 0; u < 5; u++){
                assertEquals(-3.7f, first.getGrid()[i][u], 0.000001);
            }
        }
    }

    /**
     * Test of getGamma method, of class PmFrequency.
     */
    @Test
    public void testGetGamma() {
        System.out.println("Testing 'getGamma' method.");
        PmFrequency first = getFunctionOf(5, -0.5f);
        // first row should consist of negative values...
        assertEquals(0.5f, first.getGamma(), 0.000001);
    }
   

    /**
     * Test of clone method, of class PmFrequency.
     */
    @Test
    public void testClone() {
        System.out.println("Testing 'clone' method.");
        PmFrequency first = getFunctionOf(5, 1f);
        PmFrequency second = first.clone();        
        PmFrequency third = second.clone();
        assertEquals(first, second);
        assertEquals(first, third);
    }    
    
    
    // private helpers
    
    /**
     * Returns test frequency function. For input size = 3 and number = 2, 
     * grid structure would be as follows
     * 
     *      2 2 2 
     *      3 3 3 
     *      4 4 4      
     * 
     * @param size Size of underlaying matrix, has to be odd
     * @param number Number to be used as a seed
     * @return New test PmFunction
     */
    private PmFrequency getFunctionOf(int size, float number){
        float[][] function = new float[size][size];
        for(int i = 0; i < size; i++){
            for(int u = 0; u < size; u++){
                function[i][u] = i + number;
            }
        }
        return new PmFrequency(function, DEFAULT_SMOOTH_PARAM);
    }
    
}
