package cz.muni.clusterix.businesstier;

import cz.muni.clusterix.helpers.Calc;
import cz.muni.clusterix.businesstier.FieldMask.FieldType;
import cz.muni.clusterix.entities.Restrictions;
import cz.muni.clusterix.entities.OpenCluster;
import cz.muni.clusterix.entities.ProperMotion;
import cz.muni.clusterix.entities.Result;
import cz.muni.clusterix.entities.Star;
import cz.muni.clusterix.exceptions.NoDataFoundException;
import cz.muni.clusterix.entities.PmStat;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * Class "StellarField" is a description of evaluated stellar field. It holds
 * information about available stars together with their fundamental properties.
 * On the other hand, cluster itself is separately described through the
 * OpenCluster object which allowes us to break unnecessary dependencies.
 *
 * @author Tomas Sezima
 */
public class StellarField {

    static Logger log = Logger.getLogger(StellarField.class.getName());
    // all retrieved stars
    private final Set<Star> stars;
    
    /**
     * Constructor.
     *
     * @param stars Stars emerging in investigated stellar field
     */
    public StellarField(Set<Star> stars) {
        this.stars = stars;
    }

    /**
     * Core method of the application. For given open cluster and field mask,
     * method evaluates membership probabilies for stars which satisfy criteria
     * set in 'restrictions' object.
     *
     * @param cluster Open cluster for which the probabilities are calculated
     * @param mask Field separation definition
     * @param restrictions Restrictions proposed by the user
     * @return Result containing stars with evaluated probabilities     
     * @throws cz.muni.clusterix.exceptions.NoDataFoundException
     */
    public Result evaluateProbabilities(OpenCluster cluster, FieldMask mask, Restrictions restrictions) throws NoDataFoundException {

        //separate stars that will be used for cluster+field PDF from those that will
        //be used for creating field PDF
        Set<Star> toProcess = this.getFilteredStars(restrictions);
        Set<Star> clusterFieldStars = mask.getMarkedStars(toProcess, EnumSet.of(FieldType.CLUSTERFIELD));
        Set<Star> fieldStars = mask.getMarkedStars(toProcess, EnumSet.of(FieldType.FIELD));
           
        // Actual plane size could be smaller than the one proposed via Restriction
        restrictions.setMaxMu(getOptimalPlaneSize(toProcess, restrictions));        

        //now calculate PM frequency functions for these subsets          
        PmFrequency clusterFieldFreq = new PmFrequency(clusterFieldStars, restrictions);        
        restrictions.setSmooth(clusterFieldFreq.getSmoothParam()); 
        PmFrequency fieldFreq = new PmFrequency(fieldStars, restrictions);
        fieldFreq.scale(mask.getAreaFactor());

        // estimate probabilities        
        PmFrequency clusterFreq = clusterFieldFreq.clone();
        clusterFreq.subtract(fieldFreq);
        PmProbability result = new PmProbability(clusterFreq, clusterFieldFreq, restrictions);
        restrictions.setGammaCoef(result.getGammaCoef());                         
        Set<Star> allStars = new HashSet<Star>(clusterFieldStars);        
        allStars.addAll(fieldStars);
        List<Star> assigned = result.assignProbabsTo(allStars);
        
        // retrieve proper motion stats                
        ProperMotion fieldMotion = getMotionOf(assigned, false);
        ProperMotion clusterMotion = getMotionOf(assigned, true);
        cluster.setMotion(clusterMotion);       

        log.info("Results were retrieved for cluster " + cluster.getName());        
        return new Result(cluster, fieldMotion, assigned, 
                result.getExpectedNumOfMembers(), restrictions, mask);        
    }


    /**
     * GetFilteredStars method retrieves stars that satisfy given restrictions.
     *
     * @param rest Restrictions
     * @return stars that satisfy given restrictions
     */
    public Set<Star> getFilteredStars(Restrictions rest) {
        Set<Star> filtered = new HashSet<Star>();
        for (Star star : stars) {                        
            if ((rest.getMaxMag() == null || star.getMagnitude() < rest.getMaxMag()) && 
                    (rest.getMaxMuErr() == null || star.getProperMotion().getMeanMuErr() < rest.getMaxMuErr()) && 
                    (rest.getMaxMu() == null || star.getProperMotion().getMeanMu() < rest.getMaxMu())) {                
                filtered.add(star);
            }
        }
        return filtered;
    }

    
    // private helpers
    
    /**
     * This method returns implicit maximum PM that will be used to estimate
     * sizes of empirical frequency (and propability density) functions' grids.
     *
     * @param stars Stars to be checked for PMs
     * @param rest Restrictions to be checked for max PM definition
     * @return PM to be used as a max PM
     */
    private double getOptimalPlaneSize(Set<Star> stars, Restrictions rest) {
        double max = 0;
        for (Star star : stars) {
            if(star.getProperMotion().getMeanMu() > max){
                max = star.getProperMotion().getMeanMu();
            }            
        }
        return rest.getMaxMu() != null && rest.getMaxMu() < max ? rest.getMaxMu() : max;        
    }    
    
    
    /**
     * Retrieves proper motion of probable cluster members (for boolean parameter set 
     * to 'true') or field candidates (boolean parameter set to 'false').
     * TODO: stars are sorted, it is not necessary to iterate over whole set in order
     * to mark cluster/field members...
     * 
     * @param stars List of stars to be examined
     * @param clusterMembers 'True' if cluster members should be investigated, 'false' otherwise
     * @return Proper motion of relevant set of stars
     */
    private ProperMotion getMotionOf(List<Star> stars, boolean clusterMembers) {                
        ProperMotion result = null;        
        
        // retrieve stats          
        int numOfStars = 0;
        PmStat pmStat = new PmStat(0, 0);
        PmStat pmErrStat = new PmStat(0, 0);        
        for(Star star : stars){
            if(clusterMembers == star.isClusterStar()){
                // sum PMs of each star
                pmStat.addToAlpha(star.getProperMotion().getMuAlpha());
                pmStat.addToDelta(star.getProperMotion().getMuDelta());                
                if(star.getProperMotion().getMuAlphaErr() != null &&
                        star.getProperMotion().getMuDeltaErr() != null){
                    // sum PM ERR squares
                    pmErrStat.addToAlpha(Calc.square(star.getProperMotion().getMuAlphaErr()));
                    pmErrStat.addToDelta(Calc.square(star.getProperMotion().getMuDeltaErr()));                    
                }
                numOfStars++;
            }
        }
                
        if(numOfStars > 0){
            float muAlpha = (float) (pmStat.getAlphaStat() / Calc.square(numOfStars));            
            float muDelta = (float) (pmStat.getDeltaStat() / Calc.square(numOfStars));
            // assume that ALL or NONE stars were proposed with PM errors
            float muAlphaErr = (float) (pmErrStat.getAlphaStat() / Calc.square(numOfStars));
            float muDeltaErr = (float) (pmErrStat.getDeltaStat() / Calc.square(numOfStars));
            result = new ProperMotion(muAlpha, muAlphaErr, muDelta, muDeltaErr);
        }
        
        return result;
    }
    
    
    //getters
    
    public Set<Star> getStars() {
        return stars;
    }
        
    
    //equals and hashcode
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StellarField other = (StellarField) obj;
        if (this.stars != other.stars && (this.stars == null || !this.stars.equals(other.stars))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.stars != null ? this.stars.hashCode() : 0);
        return hash;
    }
    
}
