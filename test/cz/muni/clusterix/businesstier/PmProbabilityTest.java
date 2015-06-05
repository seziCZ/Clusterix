package cz.muni.clusterix.businesstier;

import cz.muni.clusterix.entities.Restrictions;
import cz.muni.clusterix.helpers.StarGenerator;
import static java.lang.Math.hypot;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Set of test used to assert PmProbab's entity funcionality.
 * @author Tomas Sezima
 */
public class PmProbabilityTest {
       
    private final StarGenerator starGenerator;
    
    public PmProbabilityTest() {
        this.starGenerator = new StarGenerator();
    }

    /**
     * Test of assignProbabsTo method, of class PmProbab.
     */
    @Test
    public void testAssignProbabsOperator() {
        System.out.println("Testing 'testAssignProbabsOperator' method.");
        // test init
        int gridSize = 11;
        double maxMu = 0.4f;
        float maMuErr = 10.0f;       
        float treshold = 6.22f;
        
        // create dummy function first        
        PmFrequency dummyPmFreq = getTestPmFunction(gridSize, 0.0f);        
        Restrictions restrictions = new Restrictions(null, maxMu, maMuErr, null, null);                        
        PmProbability dummyFunction = new PmProbability(dummyPmFreq, dummyPmFreq, restrictions);
        
        // init parameters and apply operator        
        PmFrequency first = getTestPmFunction(gridSize, treshold);        
        PmFunction firstUnmodified = first.clone();
        PmFrequency second = getTestPmFunction(gridSize, 2.0f);        
        BinaryOperator operator = dummyFunction.new ProbabilityEstimation(gridSize / 2, 
                treshold - 0.01, restrictions);
        
        first.applyBinaryOperator(second, operator, null);
        
        // assert binary operator's functionality
        float[][] modifiedGrid = first.getGrid();
        float[][] originalGrid = firstUnmodified.getGrid();
        for(int i = 0; i < 11; i++){
            for(int u = 0; u < 11; u++){
                if(hypot((gridSize / 2 - i) * first.getCellsize(), (gridSize / 2 - u) * first.getCellsize()) < restrictions.getMaxMu() &&
                        originalGrid[i][u] > treshold - 0.01f){
                    assertEquals(treshold / 2, modifiedGrid[i][u], 0.000001);                    
                }else{
                    assertEquals(0, modifiedGrid[i][u], 0.000001);
                }
            }
        }
    }    
    
    
    // private helpers        
    
    private PmFrequency getTestPmFunction(int size, float values){
        float[][] grid = new float[size][size];
        for(int i = 0; i < size; i++){
            for(int u = 0; u < size; u++){
                grid[i][u] = values;
            }
        }
        return new PmFrequency(grid, 6.0f);
    }
        
}
