package cz.muni.clusterix.businesstier;

import cz.muni.clusterix.helpers.Calc;
import cz.muni.clusterix.entities.Restrictions;
import cz.muni.clusterix.entities.Star;
import static cz.muni.clusterix.helpers.Calc.*;
import static cz.muni.clusterix.businesstier.PmFunction.CELLSIZE;
import cz.muni.clusterix.entities.ExecutionContext;
import cz.muni.clusterix.entities.Point;
import static java.lang.Math.*;
import java.util.Set;
import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Logger;

/**
 * This class represents bivariate empirical frequency function. In this study,
 * underlaying grid is used to describe cluster and cluster field populations
 * using their proper motion. For more information, see
 * http://is.muni.cz/th/324922/fi_m/.
 *
 * @author Tomas Sezima
 */
public class PmFrequency extends PmFunction {

    private static final Logger log = Logger.getLogger(FieldMask.class.getName());

    // gaussian dispersion
    private final double smoothParam;

    /**
     * Constructor.
     *
     * @param stars Stars that will be used to create frequency function
     * @param restrictions User's restrictions, not NULL
     */
    public PmFrequency(Set<Star> stars, Restrictions restrictions){
        super(restrictions);
        
        // check user's restrictions
        smoothParam = restrictions.getSmoothParam() == null ? 
                Calc.getSmoothParam(stars) : restrictions.getSmoothParam();
        
        //evaluate empirical FF using normal circular kernel        
        UnaryOperator kernelEst = new KernelEstimation(function.length / 2, restrictions, stars);
        super.applyUnaryOperator(kernelEst, null);
    }

    /**
     * Constructor. To create frequency function in a native way, please, use
     * second constructor.
     *
     * @param alreadyCreated Frequency function representation
     * @param smoothParam Gaussian dispersion value
     */
    public PmFrequency(float[][] alreadyCreated, double smoothParam) {
        super(alreadyCreated);
        this.smoothParam = smoothParam;
    }
    

    /**
     * Mean error of distribution is estimated as a mean of it's negative
     * values. Straightforward operations like this does not have to be 
     * parallelized (thread handling may be more time consuming than actual
     * computation logic).
     *
     * @return Mean err (gamma) of 'this' frequency distribution
     */
    public double getGamma() {
        double err = 0;
        int num = 0;
        for (float[] row : this.function) {
            for (int u = 0; u < row.length; u++) {
                if (row[u] < 0) {
                    err += square(row[u]);
                    num++;
                }
            }
        }
        return num != 0 ? sqrt(err / num) : num;
    }     

    
    // getters            
    
    public double getSmoothParam() {
        return smoothParam;
    }

    
    // private classes 
    
    
    /**
     * Kernel functions can be arbitrary in shape, but for most applications
     * radially symmetrical functions are preferred. In this thesis normal circular
     * kernels are used of the following form:
     */
    private class KernelEstimation implements UnaryOperator {
        
        private final int centralCoordinate;
        private final Restrictions restrictions;
        private final Set<Star> stars;
        
        public KernelEstimation(int functionCenter, Restrictions restrictions, Set<Star> stars){
            this.centralCoordinate = functionCenter;
            this.restrictions = restrictions;
            this.stars = stars;
        }

        @Override
        public float apply(float functionValue, ExecutionContext context) {            
            
            float result = 0;
            Point pos = new Point(centralCoordinate - context.getCurrentXcoord(), 
                    centralCoordinate - context.getCurrentYcoord());
            
            if (hypot(pos.getX() * CELLSIZE, pos.getY() * CELLSIZE) < restrictions.getMaxMu()){                    
                for (Star star : stars) {
                    double volume = 1.0 / (2 * PI * square(smoothParam));
                    double dist = square(star.getProperMotion().getMuAlpha() - pos.getX() * CELLSIZE)
                            + square(star.getProperMotion().getMuDelta() - pos.getY() * CELLSIZE);
                    result += volume * FastMath.exp(-0.5 * (dist / square(smoothParam)));
                }
            }
            return result;
        }

    }        
    

    //hashcode, equals and clone
    
    /**
     * Clone 'this' object.     
     * @return Cloned PmFrequency function
     */    
    @Override
    public PmFrequency clone(){
        // copy array
        float[][] gridClone = new float[this.function.length][this.function.length];        
        PmFrequency clone = new PmFrequency(gridClone, smoothParam);   
        
        BinaryOperator copyOperator = new BinaryOperator() {                        
            @Override
            public float apply(float firstFunctionValue, float secondFunctionValue, 
                    ExecutionContext context) {
                return secondFunctionValue;
            }                                    
        };
                                
        clone.applyBinaryOperator(this, copyOperator, null);                                            
        return clone;
    }
        
    // ancestor's equals and hascode are satisfactory...
        
}
