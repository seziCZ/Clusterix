package cz.muni.clusterix.businesstier;

import cz.muni.clusterix.entities.ExecutionContext;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Set of tests related to PmFunction entity.
 * @author Tomas Sezima
 */
public class TestFunction {
    
    public TestFunction() {}

    /**
     * Test of scale method, of class PmFunction.
     */
    @Test
    public void testScale() {
        System.out.println("Testing 'scale' method.");        
        // create two identical functions
        PmFunction firstFunction = new TestPmFunction(11, 2.6f);
        PmFunction secondFunction = new TestPmFunction(11, 2.6f);
        
        // scale first, retrieve relevant grids
        firstFunction.scale(0.6f);
        float[][] firstFunctionGrid = firstFunction.getGrid();
        float[][] secondFunctionGrid = secondFunction.getGrid();
        
        //compare                         
        for(int i = 0; i < 5; i++){
            for(int u = 0; u < 5; u++){
                assertEquals(firstFunctionGrid[i][u], 
                        secondFunctionGrid[i][u] * 0.6, 0.000001);
            }
        }
        
    }

    /**
     * Test of subtract method, of class PmFunction.
     */
    @Test
    public void testSubtract() {
        System.out.println("Testing 'subtract' method");
       // create two identical functions and one to use as a subtractor
        PmFunction firstFunction = new TestPmFunction(11, -42.23f);        
        PmFunction secondFunction = new TestPmFunction(11, -42.23f);
        PmFunction subtractor = new TestPmFunction(11, 235.4f);
        
        // scale first, retrieve relevant grids
        firstFunction.subtract(subtractor);
        float[][] firstFunctionGrid = firstFunction.getGrid();
        float[][] secondFunctionGrid = secondFunction.getGrid();
        float[][] subtractorGrid = subtractor.getGrid();
        
        //compare                         
        for(int i = 0; i < 5; i++){
            for(int u = 0; u < 5; u++){
                assertEquals(firstFunctionGrid[i][u], 
                        secondFunctionGrid[i][u] - subtractorGrid[i][u], 0.000001);
            }
        }
    }

    /**
     * Test of divideBy method, of class PmFunction.
     */
    @Test
    public void testDivideBy() {
        System.out.println("Testing 'divideBy' method.");
        // create two identical functions and one to use as a denominator
        PmFunction firstFunction = new TestPmFunction(11, 64.3f);        
        PmFunction secondFunction = new TestPmFunction(11, 64.3f);
        PmFunction demnominator = new TestPmFunction(11, -2.4f);
        
        // scale first, retrieve relevant grids
        firstFunction.divideBy(demnominator);
        float[][] firstFunctionGrid = firstFunction.getGrid();
        float[][] secondFunctionGrid = secondFunction.getGrid();
        float[][] denominatorGrid = demnominator.getGrid();
        
        //compare                         
        for(int i = 0; i < 5; i++){
            for(int u = 0; u < 5; u++){
                Float result = denominatorGrid[i][u] == 0 ? Float.NaN : 
                        secondFunctionGrid[i][u] / denominatorGrid[i][u];                        
                assertEquals(firstFunctionGrid[i][u], result, 0.000001);
            }
        }
    }
    

    /**
     * Test of applyBinaryOperation method, of class PmFunction.
     */
    @Test
    public void testApplyBinaryOperation() {
        System.out.println("Testing 'applyBinaryOperation' method.");
        // create two identical function and apply binary operation
        PmFunction firstFunction = new TestPmFunction(11, 234.65f);        
        PmFunction secondFunction = new TestPmFunction(11, 234.65f);
        PmFunction testFunction = new TestPmFunction(11, -23.4f);
        
        BinaryOperator testOperator = new BinaryOperator() {
            @Override
            public float apply(float firstFunctionValue, float secondFunctionValue, ExecutionContext context) {
                return firstFunctionValue * secondFunctionValue / 3;
            }
        };
        
        firstFunction.applyBinaryOperator(testFunction, testOperator, null);
        
        // assert equals
        float[][] firstFunctionGrid = firstFunction.getGrid();
        float[][] secondFunctionGrid = secondFunction.getGrid();
        float[][] testFunctionGrid = testFunction.getGrid();
        for(int i = 0; i < 11; i++){
            for(int u = 0; u < 11; u++){
                assertEquals(secondFunctionGrid[i][u] * testFunctionGrid[i][u] / 3, 
                        firstFunctionGrid[i][u], 0.000001);
            }
        }
        
    }

    /**
     * Test of applyUnaryOperation method, of class PmFunction.
     */
    @Test
    public void testApplyUnaryOperation() {
        System.out.println("Testing 'applyUnaryOperation' method.");
        // create test function and apply unary operation
        PmFunction firstFunction = new TestPmFunction(11, 234.65f);        
        PmFunction secondFunction = new TestPmFunction(11, 234.65f);        
        
        UnaryOperator testOperator = new UnaryOperator() {
            @Override
            public float apply(float functionValue, ExecutionContext context) {
                return functionValue * 23.5f;
            }
        };
        
        firstFunction.applyUnaryOperator(testOperator, null);        
        
        // assert equals
        float[][] firstFunctionGrid = firstFunction.getGrid();
        float[][] secondFunctionGrid = secondFunction.getGrid();        
        for(int i = 0; i < 11; i++){
            for(int u = 0; u < 11; u++){
                assertEquals(secondFunctionGrid[i][u] * 23.5f, 
                        firstFunctionGrid[i][u], 0.000001);
            }
        }
    }
    

    /**
     * Test of equals method, of class PmFunction.
     */
    @Test
    public void testEquals() {
        System.out.println("Testing 'equals' method");
        PmFunction firstFunction = new TestPmFunction(11, 64.3f);        
        PmFunction secondFunction = new TestPmFunction(11, 64.3f);
        assertTrue(firstFunction.equals(secondFunction));        
    }

    
    // private helpers
    
    
    private class TestPmFunction extends PmFunction {
        public TestPmFunction(int size, float seed) {
            super(getTestGrid(size, seed));
        }
    }
    
    /**
     * Generates test grid whose matrix size is defined by
     * 'size' parameter and individual values are determined
     * by 'seed' parameter.
     * 
     * @param size Size of grid to be generated
     * @param seed Seed that is used to generate individual matrix values
     * @return Generated function grid
     */
    private float[][] getTestGrid(int size, float seed){
        float[][] generatedGrid = new float[size][size];
        for(int i = 0; i < size; i++){
            for(int u = 0; u < size; u++){
                generatedGrid[i][u] = seed * i;
            }
        }
        return generatedGrid;
    }
    
}
