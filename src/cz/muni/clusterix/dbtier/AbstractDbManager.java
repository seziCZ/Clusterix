package cz.muni.clusterix.dbtier;

import cz.muni.clusterix.exceptions.NoDataFoundException;
import cz.muni.clusterix.exceptions.SourceProblemException;

/**
 * This class represents abstract DB manager.
 * 
 * @author Tomas Sezima
 */
abstract class AbstractDbManager implements DbManager{    
        
    @Override
    public String getSourceUri(String clusterIdentificator) throws SourceProblemException, NoDataFoundException {
        return "#";
    }       
    
}
