package cz.muni.clusterix.helpers;

import cz.muni.clusterix.entities.PmStat;
import java.util.Set;
import cz.muni.clusterix.entities.Star;
import java.util.Random;
import cz.muni.clusterix.entities.Point;
import cz.muni.clusterix.entities.Declination;
import cz.muni.clusterix.entities.RightAscension;
import cz.muni.clusterix.entities.WithCoordinates;
import static java.lang.Math.*;

/**
 * The class Calc contains basic numeric operations needed for cluster
 * membership estimation.
 *
 * @author Tomas Sezima
 */
public class Calc {

    private static final int SILVERMANS_RULE_DIMENSION = 2;

    // static helper, could not be inicialized...
    private Calc() {}    
        
        
    /**
     * GetSmoothParam returns sample's implicit smoothing parameter (gaussian dispersion) estimated 
     * by Silverman's rule of thumb.     
     *      
     * @param stars Stars whose proper motions will be used to determine smoothing parameter
     * @return smoothing parameter
     */
    public static double getSmoothParam(Set<Star> stars) {
        PmStat means = getPmMean(stars);
        PmStat deviations = getPmDeviation(stars, means);
        double variance = square(deviations.getAlphaStat()) + square(deviations.getDeltaStat());
        return pow(4.0 / (SILVERMANS_RULE_DIMENSION + 2.0), 1.0 / (SILVERMANS_RULE_DIMENSION + 4.0))
                * sqrt(variance / SILVERMANS_RULE_DIMENSION) * pow(stars.size(), -1.0 / (SILVERMANS_RULE_DIMENSION + 4.0));
    }

    /**
     * This method estimates standard deviation of propper motions associated
     * with stars defined by parameter.
     *
     * @param stars Stars whose proper motions will be used to determine
     * standard deviation
     * @param mean Aritmetic means (alpha, delta) of investigated sample
     * @return standard deviations of PMs in PmStat object
     */
    public static PmStat getPmDeviation(Set<Star> stars, PmStat mean) {
        PmStat deviation = null;

        if (stars != null && !stars.isEmpty()) {
            deviation = new PmStat(0, 0);
            for (Star star : stars) {
                deviation.addToAlpha(square(star.getProperMotion().getMuAlpha() - mean.getAlphaStat()));
                deviation.addToDelta(square(star.getProperMotion().getMuDelta() - mean.getDeltaStat()));                
            }
            deviation.setAlphaStat(sqrt(deviation.getAlphaStat() / stars.size()));
            deviation.setDeltaStat(sqrt(deviation.getDeltaStat() / stars.size()));
        }

        return deviation;
    }

    /**
     * This method estimates aritmetic mean of propper motions associated with
     * stars defined by parameter.
     *
     * @param stars Stars to be scaned for PMs
     * @return aritmetic mean of alpha and delta PMs in PmStat object
     */
    public static PmStat getPmMean(Set<Star> stars) {
        PmStat mean = null;

        if (stars != null && !stars.isEmpty()) {
            mean = new PmStat(0, 0);
            for (Star star : stars) {
                mean.addToAlpha(star.getProperMotion().getMuAlpha());
                mean.addToDelta(star.getProperMotion().getMuDelta());
            }
            mean.setAlphaStat(mean.getAlphaStat() / stars.size());
            mean.setDeltaStat(mean.getDeltaStat() / stars.size());
        }
        
        return mean;
    }

    /**
     * Retrieves distance from point [firstX, firstY] to point [secondX,
     * secondY] in arcseconds.
     *     
     * @param first First object that contains stellar coordinates
     * @param second Second object that contains stellar coordinates
     * @return distance between objects in arcseconds
     */
    public static double getDistance(WithCoordinates first, WithCoordinates second) {
        RightAscension firstX = first.getRightAscension();
        Declination firstY = first.getDeclination();
        RightAscension secondX = second.getRightAscension();
        Declination secondY = second.getDeclination();
        
        double x = abs(firstX.getDegrees() - secondX.getDegrees());
        double y = abs(firstY.getDegrees() - secondY.getDegrees());
        return hypot(x, y);
    }

    /**
     * Retrieves dimensionless distance from "first" point to "second" point.
     *
     * @param first Point
     * @param second Point
     * @return distance retrieved via standard mathematical metric
     */
    public static double getDistance(Point first, Point second) {
        double x = first.getX() - second.getX();
        double y = first.getY() - second.getY();
        return hypot(x, y);
    }

    /**
     * Returns pow(base, 2) faster than native Math function does.
     *
     * @param base Base
     * @return base square
     */
    public static double square(double base) {
        return base * base;
    }   

    
    // methods used in test
    
    /**
     * Returns the next pseudorandom, uniformly distributed double value within
     * the range specified by 'from' and 'to' parameters.
     *
     * @param from Lower barrier of the interval
     * @param to Upper barrier of the interval
     * @return pseudo random double number from interval [from, to]
     */
    public static double getRand(double from, double to) {
        double random = new Random().nextDouble();
        double result = from + (random * (to - from));
        return result;
    }

    /**
     * Returns the next pseudorandom, Gaussian ('normally') distributed double
     * value with mean 'mean' and standard deviation 'deviation' from this
     * random number generator's sequence.
     *
     * @param deviation Standard deviation of distibution
     * @param mean Mean value of distribution
     * @return random value
     */
    public static float getRandGauss(double deviation, double mean) {
        double random = new Random().nextGaussian();
        return (float) (random * deviation + mean);
    }
    
}
