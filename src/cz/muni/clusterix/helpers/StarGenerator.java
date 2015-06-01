package cz.muni.clusterix.helpers;

import cz.muni.clusterix.businesstier.StellarField;
import cz.muni.clusterix.entities.Declination;
import cz.muni.clusterix.entities.RightAscension;
import cz.muni.clusterix.entities.Star;
import static cz.muni.clusterix.helpers.Calc.*;
import cz.muni.clusterix.entities.ProperMotion;
import java.util.HashSet;
import java.util.Set;

/**
 * For given parameters, class StarGenerator generates artificial stellar fields that
 * may be used for testing purposes. Deeper understanding of underlaying approaches
 * may be retrieved from http://is.muni.cz/th/324922/fi_m/.
 *
 * @author Tomas Sezima
 */
public class StarGenerator {

    /**
     * GetTestField method creates artificial stellar field that contains two 
     * kinematic groups of star. First one describes a cluster (center is 
     * represented by parameters 'ra' and 'dec', radius by homonymous atribute) 
     * and the second one a field. Total number of stars in stellar field (cluster + field)
     * is defined by 'num' parameter.
     *
     *
     * @param ra Right ascension of the plane center
     * @param dec Declination of the plane center
     * @param radius Radius of the cluster in arcmins
     * @param num Total number of stars in the stellar plane
     * @return artificial stellar field
     */
    public StellarField getTestField(RightAscension ra, Declination dec,
            float radius, int num) {        
        int clusterStars = (int) (num * 0.2);
        
        Scatter fieldScatter = new Scatter(3.0f, 2.0f, 2.0f);
        ProperMotion fieldMotion = new ProperMotion(1.0f, 1.0f);
        Set<Star> stars = generateStars(ra, dec, 14.0f, fieldMotion, 
                radius, num - clusterStars, fieldScatter);
        
        Scatter clusterScatter = new Scatter(2.0f, 1.5f, 1.5f);        
        ProperMotion clusterMotion = new ProperMotion(3.0f, -3.0f);
        stars.addAll(generateStars(ra, dec, 15.0f, clusterMotion, 
                radius, clusterStars, clusterScatter));
        
        return new StellarField(stars);
    }

    /**
     * Generates test stars whose coordinates are within proposed restrictions.
     * This method is used to test variety of functions across the Clusterix
     * application.
     * 
     * @param ra Right ascension
     * @param dec Declination
     * @param radius Radius in arcmins within which stars will be generated
     * @param scatter Scatter
     * @param motion Mean sample proper motion
     * @param num Number of stars to be generated
     * @return Set of generated stars
     */    
    public Set<Star> getTestStars(RightAscension ra, Declination dec, 
            float radius, Scatter scatter, ProperMotion motion, int num){
        if(scatter == null) scatter = new Scatter(0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        if(motion == null) motion = new ProperMotion(0.0f, 0.0f);
        return generateStars(ra, dec, 0.0f, motion, radius, num, scatter);
    }
    
    // private helpers
    
    
    /**
     * Generates stars with declination dec +- radius, and right ascension ra +-
     * radius. A number of generated stars is set by 'num' parameter, average
     * magnitude may be set by 'magnitude' parameter.
     *     
     * @param centralDec Declination of the origin
     * @param centralRa Rectascense of the origin
     * @param meanMag Average magnitude
     * @param motion Mean sample motion
     * @param clusterRadius Radius in arcmins in which stars will be generated
     * @param num Number of stars to be generated
     * @param scatter Scatter
     * @return generated stars
     */
    private Set<Star> generateStars(RightAscension centralRa, Declination centralDec, float meanMag, 
            ProperMotion motion, float clusterRadius, int num, Scatter scatter){
        Set<Star> stars = new HashSet<Star>();
        for(int i = 0; i < num; i++){
            // transfrom declination
            float randDec;
            do{
                randDec = (float) (centralDec.getDegrees()  + 
                    getRand(-1, 1) * clusterRadius / ClusterixConstants.ARCMINS_IN_DEGREE);
            }while(randDec < Declination.MIN_DEGREES || randDec > Declination.MAX_DEGREES);            
            Declination newDec = new Declination(randDec, 0.0f);

            // transform right ascension
            float randRa;
            do{
                randRa = (float) (centralRa.getDegrees() + 
                    getRand(-1, 1) * clusterRadius / ClusterixConstants.ARCMINS_IN_DEGREE);
            }while(randRa < RightAscension.MIN_DEGREES || randRa > RightAscension.MAX_DEGREES);            
            RightAscension newRa = new RightAscension(randRa, 0.0f);

            //fill test values
            float motionInAlpha = getRandGauss(scatter.getMuAlphaScatter(), motion.getMuAlpha());            
            float motionInDelta = getRandGauss(scatter.getMuDeltaScatter(), motion.getMuDelta());
            
            Float motionInAlphaErr = null;
            Float motionInDeltaErr = null;
            if(motion.getMuAlphaErr() != null && scatter.getMuAlphaErrScatter() != null &&
                    motion.getMuDeltaErr() != null && scatter.getMuDeltaErrScatter() != null){
                motionInAlphaErr = getRandGauss(scatter.getMuAlphaErrScatter(), motion.getMuAlphaErr());
                motionInDeltaErr = getRandGauss(scatter.getMuDeltaErrScatter(), motion.getMuDeltaErr());
            }
            
            float magnitude = getRandGauss(scatter.magScatter, meanMag);
            
            ProperMotion starMotion = new ProperMotion(motionInAlpha, motionInAlphaErr, 
                    motionInDelta, motionInDeltaErr);
            Star created = new Star(i, magnitude, newRa, newDec, starMotion);
            stars.add(created);
        }
        return stars;
    }
    
    
    // private classes
            
    public class Scatter{
        
        private final float magScatter;
        private final float muAlphaScatter;
        private final float muDeltaScatter;
        private final Float muAlphaErrScatter;
        private final Float muDeltaErrScatter;
        
        /**
         * Constructor.
         * 
         * @param mag Scatter in magnitude (magnitudes)
         * @param alpha Scatter in alpha (degrees)
         * @param delta Scatter in delta (degrees)
         * @param alphaErr Scatter in alpha err (degrees)
         * @param deltaErr Scatter in delta err (degrees)
         */
        public Scatter(float mag, float alpha, float delta, Float alphaErr, Float deltaErr){
            this.magScatter = mag;
            this.muAlphaScatter = alpha;
            this.muDeltaScatter = delta;
            this.muAlphaErrScatter = alphaErr;
            this.muDeltaErrScatter = deltaErr;
        }
        
        public Scatter(float mag, float alpha, float delta){
            this(mag, alpha, delta, null, null);
        }
        
        // getter and setter

        public float getMagScatter() {
            return magScatter;
        }

        public float getMuAlphaScatter() {
            return muAlphaScatter;
        }

        public float getMuDeltaScatter() {
            return muDeltaScatter;
        }

        public Float getMuAlphaErrScatter() {
            return muAlphaErrScatter;
        }

        public Float getMuDeltaErrScatter() {
            return muDeltaErrScatter;
        }

        @Override
        public String toString() {
            return "Scatter{" + "magScatter=" + magScatter + ", muAlphaScatter=" 
                    + muAlphaScatter + ", muDeltaScatter=" + muDeltaScatter + 
                    ", muAlphaErrScatter=" + muAlphaErrScatter + 
                    ", muDeltaErrScatter=" + muDeltaErrScatter + '}';
        }
                                
    }        
    
}
