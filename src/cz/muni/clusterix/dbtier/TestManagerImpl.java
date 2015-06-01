
package cz.muni.clusterix.dbtier;

import cz.muni.clusterix.businesstier.StellarField;
import cz.muni.clusterix.entities.Declination;
import cz.muni.clusterix.entities.OpenCluster;
import cz.muni.clusterix.entities.RightAscension;
import cz.muni.clusterix.exceptions.NoDataFoundException;
import cz.muni.clusterix.exceptions.SourceProblemException;
import cz.muni.clusterix.helpers.StarGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 * DbManager implementation used for testing purposses.
 * 
 * @author Tomas Sezima
 */
public class TestManagerImpl extends AbstractDbManager{
    
    private final StarGenerator starGenerator;
    
    public TestManagerImpl(){
        this.starGenerator = new StarGenerator();
    }

    @Override
    public OpenCluster getOpenCluster(String clusterIdentificator) throws SourceProblemException, NoDataFoundException {
        RightAscension ra = new RightAscension(77.5625f, 0.0f);
        Declination dec = new Declination(-12.0683f, 0.0f);
        return new OpenCluster("Test cluster", ra, dec, 20);
    }

    @Override
    public StellarField getStellarField(String clusterIdentificator) throws SourceProblemException, NoDataFoundException {        
        RightAscension ra = new RightAscension(77.5625f, 0.0f);
        Declination dec = new Declination(-12.0683f, 0.0f);
        return starGenerator.getTestField(ra, dec, 40, 2000);
    }

    @Override
    public List<OpenCluster> getAllClusters() throws SourceProblemException {        
        List<OpenCluster> clusters = new ArrayList<OpenCluster>();
        try {
            clusters.add(this.getOpenCluster("test"));            
        } catch (NoDataFoundException ex) {
            // this should never happen
            ex.printStackTrace();
        }
        return clusters;
    }
}
