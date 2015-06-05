package cz.muni.clusterix.businesstier;

import cz.muni.clusterix.entities.Restrictions;
import java.util.ArrayList;
import java.util.List;
import cz.muni.clusterix.entities.Star;
import java.util.Set;
import cz.muni.clusterix.entities.Point;
import java.util.Collections;
import static cz.muni.clusterix.helpers.Calc.*;
import static cz.muni.clusterix.businesstier.PmFunction.CELLSIZE;
import cz.muni.clusterix.entities.ExecutionContext;
import cz.muni.clusterix.helpers.ClusterixConstants;
import static java.lang.Math.*;
import java.util.Comparator;
import org.apache.log4j.Logger;

/**
 * This class represents bivariate empirical probability density function.
 *
 * @author Tomas Sezima
 */
public class PmProbability extends PmFunction {
    
    private static final Logger log = Logger.getLogger(PmProbability.class.getName());

    // inplicite err treshold is defined by GAMMACOEF * mean err    
    private final int expectedNumOfMembers;
    private final float gammaCoef;                    
    
    /**
     * Constructor.
     *
     * @param clusterFreq Frequency function describing the cluster population, not NULL
     * @param clusterFieldFreq Frequency function describing the cluster-field, not NULL
     * population.
     * @param rest Restrictions proposed by the user, not NULL
     */
    public PmProbability(PmFrequency clusterFreq, PmFrequency clusterFieldFreq, Restrictions rest) {        
        // share function in order to minimize memory requirements
        // i.e.: given clusterFreq function is being modified and therefore COULD NOT be used any further!
        super(clusterFreq.getGrid());                
        
        // check input params
        if(clusterFreq.getGrid().length != clusterFieldFreq.getGrid().length){           
            throw new IllegalArgumentException("An attempt was made to create "
                    + "proper motion probability function using frequency "
                    + "functions of different sizes.");
        }                
        
        this.expectedNumOfMembers = getExpNumOfClusterStars(clusterFreq);
        this.gammaCoef = rest.getGammaCoef() != null ? 
                rest.getGammaCoef() : ClusterixConstants.DEFAULT_GAMMA_COEF;                
        
        // init        
        BinaryOperator probabEst = new ProbabilityEstimation(clusterFreq.getGrid().length / 2, 
                clusterFreq.getGamma(), rest);
        applyBinaryOperator(clusterFieldFreq, probabEst, null);
    }


    /**
     * Assigns membership probability to each star from givem set. Estimation
     * is being done by maping PM possition to retrieved probability grid.
     *
     * @param stars Stars
     * @return Stars with assigned probabilities
     */
    public List<Star> assignProbabsTo(Set<Star> stars) {
        int center = (int) (super.function.length / 2);                        
        for (Star star : stars) {
            //retrieve PM possition in grid
            int xCoord = center - (int) Math.round(star.getProperMotion().getMuAlpha() / CELLSIZE);
            int yCoord = center - (int) Math.round(star.getProperMotion().getMuDelta() / CELLSIZE);

            //assign probability
            if (xCoord >= 0 && xCoord < super.function.length && 
                    xCoord >= 0 && yCoord < super.function.length) {                
                star.setProbability(super.function[xCoord][yCoord]);
            } else star.setProbability(0.0f);            
        }

        // transform set into list and order it by probabilities
        List<Star> list = new ArrayList<Star>(stars);
        Collections.sort(list, new ProbabilityComparator());

        // mark most probable cluster members        
        // TODO: optimize multiple iteration over star set
        for (int i = 0; i < stars.size(); i++) {
            list.get(i).setIsClusterStar(i < expectedNumOfMembers);            
        }

        return list;
    }                
    
    
    // getters
    
    public float getGammaCoef() {
        return gammaCoef;
    }               

    public int getExpectedNumOfMembers() {
        return expectedNumOfMembers;
    }        
    
    
    // private helpers
    
    /**
     * The non-parametric aproach gives an expected number of cluster members
     * from the integrated volume of the cluster frequency function in the areas
     * of high cluster density where F(x) > GAMMACOEF * gamma. Method
     * getExpNumOfClusterStars() is responsible for retrieveing such a value
     * from 'cluster frequency function' object. Straightforward operations like
     * this does not have to be parallelized (thread handling may be more time 
     * consuming than actual computation logic).
     *
     * @return expected number of cluster stars.
     */
    private int getExpNumOfClusterStars(PmFrequency function) {
        double result = 0;
        double treshold = gammaCoef * function.getGamma();        
        
        for(float[] row : function.getGrid()){
            for(int i = 0; i < function.getGrid().length; i++){
                if(row[i] > treshold){
                    result += row[i];
                }
            }
        }
        
        return (int) Math.ceil(result * square(this.getCellsize()));
    }
    
    /**
     * According to Bayesian theory, for an individual found in the 2-D space at
     * position (a, b), the probability of belonging to cluster population is:
     *
     * P(a,b) = (Chi_cf(a,b) - Chi_f(a,b)) / Chi_cf(a,b)
     *
     * where Chis are the (empirical) frequency functions of cluster+field (cf)
     * and field (c) star's propper motions.
     */
    protected class ProbabilityEstimation implements BinaryOperator{
        
        private final int centralCoordinate;
        private final double treshold;
        private final Restrictions restrictions;
        
        public ProbabilityEstimation(int functionCenter, double treshold, Restrictions restrictions){
            this.centralCoordinate = functionCenter;
            this.treshold = treshold;
            this.restrictions = restrictions;        
        }                

        @Override
        public float apply(float firstFunctionValue, float secondFunctionValue, ExecutionContext context) {                                                            
            
            float result = 0;
            Point pos = new Point(centralCoordinate - context.getCurrentXcoord(),
                    centralCoordinate - context.getCurrentYcoord());
            
            if (hypot(pos.getX() * CELLSIZE, pos.getY() * CELLSIZE) < restrictions.getMaxMu()){
                result = firstFunctionValue > treshold ? firstFunctionValue / secondFunctionValue : 0;
            }
            return result;
        }
    
    }
    
    /**
     * Compares stars according to their membership probability. All stars in sorted 
     * set has to have probabilities assigned.
     */
    private class ProbabilityComparator implements Comparator<Star>{
        @Override
        public int compare(Star o1, Star o2) {            
            int result = 0;
            if(o1.getProbability() != null && o2.getProbability() != null){
                result =  o2.getProbability().compareTo(o1.getProbability());
            }
            return result;
        }                
    }
        
    // ancestor's equals and hascode are satisfactory...
    
}
