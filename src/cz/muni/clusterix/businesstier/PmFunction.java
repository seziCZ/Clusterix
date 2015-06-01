package cz.muni.clusterix.businesstier;

import cz.muni.clusterix.entities.ExecutionContext;
import cz.muni.clusterix.entities.Restrictions;
import cz.muni.clusterix.helpers.ClusterixConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * This class represents general bivariate empirical probability function. In
 * other words, both frequency functions and probability density functions may
 * extend this class in order to be represeted satisfactorily.
 *
 * @author Tomas Sezima
 */
public class PmFunction implements Function{

    private static final Logger log = Logger.getLogger(PmFunction.class.getName());

    // grid size 0.08 masyear^-1 is well below the propper motion errors, this
    // value should be ideal for algoritm purposses
    protected static final double CELLSIZE = 0.08; //masyear^-1        
    protected float[][] function;

    /**
     * Constructor.
     * 
     * @param function already created matrix
     */
    public PmFunction(float[][] function) {
        this.function = function;
    }
    
    /**
     * Constructor.
     * 
     * @param restrictions  restrictions, proper motion limitation has to be set
     */
    public PmFunction(Restrictions restrictions){
        if(restrictions != null && restrictions.getMaxMu() != null){
            this.function = new float
                    [2 * (int) Math.ceil(restrictions.getMaxMu() / CELLSIZE)]
                    [2 * (int) Math.ceil(restrictions.getMaxMu() / CELLSIZE)];
        }else throw new IllegalArgumentException("Restrictions entity proposed to "
              + "PmFunction constructor has to have 'maximal motion' property set.");
        
    }

    
    //public helpers
    
    /**
     * Scales function's grid values by the factor which is given as parameter.
     *
     * @param scaleFactor Scale factor
     */
    public void scale(float scaleFactor) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(ClusterixConstants.SCALE_PARAM, scaleFactor);
                
        UnaryOperator scale = new UnaryOperator() {
            @Override
            public float apply(float functionValue, ExecutionContext context) {
                float scaleParam = (Float) context.getParameters().get(ClusterixConstants.SCALE_PARAM);
                return functionValue * scaleParam;
            }
        };        
        
        applyUnaryOperation(scale, params);
    }

    /**
     * Subtracts given grid from 'this' object. Subtracted function has to have
     * the same density and size as 'this' object does.
     *
     * @param toSubtract Function to be subtracted from 'this' function    
     */
    public void subtract(Function toSubtract) {
        
        if(this.getGrid().length != toSubtract.getGrid().length){
            throw new IllegalArgumentException("An attempt was made to subtract "
                    + "two functions with different matrix sizes.");
        }
        
        BinaryOperator subtraction = new BinaryOperator() {
            @Override
            public float apply(float firstFunctionValue, float secondFunctionValue, ExecutionContext context) {                
                return firstFunctionValue - secondFunctionValue;
            }
        };
        
        applyBinaryOperation(toSubtract, subtraction, null);
    }

    /**
     * Divide 'this' function's grid by function specified by parameter.
     *
     * @param denominator Function to be used as denominator
     */
    public void divideBy(Function denominator){
        
        if(this.getGrid().length != denominator.getGrid().length){
            throw new IllegalArgumentException("An attempt was made to subtract "
                    + "two functions with different matrix sizes.");
        }
        
        BinaryOperator division = new BinaryOperator() {
            @Override
            public float apply(float firstFunctionValue, float secondFunctionValue, ExecutionContext context) {
                return secondFunctionValue != 0 ? firstFunctionValue / secondFunctionValue : Float.NaN;
            }
        };
        
        applyBinaryOperation(denominator, division, null);
    }
    

    /**
     * Hanles parallel application of given operation. First function will be
     * modified by applyng given binary operation on each cell of both
     * functions.
     *
     * @param secondFunction Function to be used as second argument of opperation
     * @param operation Operation to be applyed
     * @param context Operation context         
     */
    @Override
    public void applyBinaryOperation(Function secondFunction, 
            BinaryOperator operation, Map<String, Object> context){
        // assert function grids have the same dimension.
        if (this.function.length != secondFunction.getGrid().length) {
            log.error("An attempt was made to process two functions with different matrix sizes.");
            throw new IllegalArgumentException("Can not process functions with different matrixes.");
        }
            
        int cores = ClusterixConstants.NUM_OF_AVAILABLE_PROCESSORS;
        BinaryOperationExecutor[] executors = new BinaryOperationExecutor[cores];

        // Split the kernel estimation between all available processors
        for (int i = 0; i < cores; i++) {
            executors[i] = new BinaryOperationExecutor(i, cores, 
                    this.function, secondFunction.getGrid(), operation, context);
            executors[i].start();
        }

        // Wait until the work is done and then continue in the processing
        for (int i = 0; i < cores; i++) {
            try {
                executors[i].join();
            } catch (InterruptedException ex) {
                log.error("Program failed to handle join of used threads.");
            }
        }        
    }
        
    
    /**
     * Hanles parallel application of given operation. Function will be
     * modified by applyng given unary operation on each cell of both
     * functions.
     *     
     * @param operator Operation to be applyed
     * @param context Operation context     
     */
    @Override
    public void applyUnaryOperation(UnaryOperator operator, 
        Map<String, Object> context){                    
        
        int cores = ClusterixConstants.NUM_OF_AVAILABLE_PROCESSORS;
        UnaryOperationExecutor[] executors = new UnaryOperationExecutor[cores];

        // Split the kernel estimation between all available processors
        for (int i = 0; i < cores; i++) {
            executors[i] = new UnaryOperationExecutor(i, cores, 
                    this.function, operator, context);
            executors[i].start();
        }

        // Wait until the work is done and then continue in the processing
        for (int i = 0; i < cores; i++) {
            try {
                executors[i].join();
            } catch (InterruptedException ex) {
                log.error("Program failed to handle join of used threads.");
            }
        }
   
    }

    
    //getters    
    
    @Override
    public float[][] getGrid() {
        return function;
    }        
    
    @Override
    public double getCellsize() {
        return CELLSIZE;
    }

    
    // private classes
    
    /**
     * This class allows paralell computation of operations over proper motion
     * functions.
     *
     * @author Tomas Sezima
     */
    protected class BinaryOperationExecutor extends Thread {

        private final int threadNo;
        private final int mod;
        private final float[][] firstFunction;
        private final float[][] secondFunction;        
        private final BinaryOperator operation;
        private final Map<String, Object> contextParams;

        public BinaryOperationExecutor(int threadNo, int mod, float[][] firstFunction,
                float[][] secondFunction, BinaryOperator operation, Map<String, Object> context) {
            super("Thread " + threadNo);
            this.threadNo = threadNo;
            this.mod = mod;
            this.firstFunction = firstFunction;
            this.secondFunction = secondFunction;            
            this.operation = operation;
            this.contextParams = context;
        }

        @Override
        public void run() {
            List<float[][]> functions = new ArrayList<float[][]>();
            functions.add(firstFunction);
            functions.add(secondFunction);
            ExecutionContext context = new ExecutionContext(0, 0, functions, contextParams);
            for (int i = 0; i < firstFunction.length; i++) {
                for (int u = threadNo; u < firstFunction.length; u += mod) {
                    context.setCurrentXcoord(i);
                    context.setCurrentYcoord(u);
                    firstFunction[i][u] = operation.apply(firstFunction[i][u], 
                            secondFunction[i][u], context);                                                            
                }
            }
        }
    }

    /**
     * This class allows paralell computation of operations over proper motion
     * functions. 
     *
     * @author Tomas Sezima
     */
    protected class UnaryOperationExecutor extends Thread{

        private final int threadNo;
        private final int mod;
        private final float[][] function;                
        private final UnaryOperator operation;
        private final Map<String, Object> contextParams;

        public UnaryOperationExecutor(int threadNo, int mod, float[][] function, 
                UnaryOperator operation, Map<String, Object> context) {
            super("Thread " + threadNo);
            this.threadNo = threadNo;
            this.mod = mod;
            this.function = function;                        
            this.operation = operation;
            this.contextParams = context;
        }

        @Override
        public void run() {
            List<float[][]> functions = new ArrayList<float[][]>();
            functions.add(function);            
            ExecutionContext context = new ExecutionContext(0, 0, functions, contextParams);
            for (int i = 0; i < function.length; i++) {
                for (int u = threadNo; u < function.length; u += mod) {                    
                    context.setCurrentXcoord(i);
                    context.setCurrentYcoord(u);
                    function[i][u] = operation.apply(function[i][u], context);                    
                }
            }
        }
    }        
    
    
    // equals and hashcode

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Arrays.deepHashCode(this.function);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PmFunction other = (PmFunction) obj;
        if (!Arrays.deepEquals(this.function, other.function)) {
            return false;
        }
        return true;
    }                
        
}
