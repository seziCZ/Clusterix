package cz.muni.clusterix.dbtier;

import cz.muni.clusterix.businesstier.StellarField;
import cz.muni.clusterix.entities.OpenCluster;
import cz.muni.clusterix.exceptions.NoDataFoundException;
import cz.muni.clusterix.exceptions.SourceProblemException;
import java.util.List;

/**
 * Implementation of the DbManager class is the first step in new data source connection.
 * Corresponding implementation will offer all necessary data, however, in order to
 * connect new database properly, multiple changes have to be also done in the graphical 
 * user interface. For more information, please, inpect ClusterixWeb project or its 
 * documentation. 
 *
 * @author Tomas Sezima
 */
public interface DbManager {

    /**
     * GetOpenCluster method retrieves main cluster parameters needed for membership
     * determination in a form of OpenCluster object.
     *
     * @param clusterIdentificator Query that characterises cluster
     * @return Main parameters of cluster as OpenCluster object
     * @throws cz.muni.clusterix.exceptions.SourceProblemException
     * @throws cz.muni.clusterix.exceptions.NoDataFoundException
     */
    public OpenCluster getOpenCluster(String clusterIdentificator) throws SourceProblemException, NoDataFoundException;

    /**
     * GetStellarField method retrieves stars connected to queried cluster. Only stars with
     * known propper motions are being retrieved.
     *
     * @param clusterIdentificator Query which characterises cluster
     * @return Set of stars with propper motions
     * @throws cz.muni.clusterix.exceptions.SourceProblemException
     * @throws cz.muni.clusterix.exceptions.NoDataFoundException
     */
    public StellarField getStellarField(String clusterIdentificator) throws SourceProblemException, NoDataFoundException;

    /**
     * GetAllClusters method retrieves ordered list of all available open clusters that carry data needed
     * for membership estimation.
     *
     * @return List of available open clusters
     * @throws cz.muni.clusterix.exceptions.SourceProblemException
     */
    public List<OpenCluster> getAllClusters() throws SourceProblemException;
    
    
    /**
     * Retrieves url to source that has been used to evaluate probabilities for 
     * cluster specified by identificator.
     * 
     * @param clusterIdentificator Cluster description
     * @return URL of source
     * @throws cz.muni.clusterix.exceptions.SourceProblemException
     * @throws cz.muni.clusterix.exceptions.NoDataFoundException
     */
    public String getSourceUri(String clusterIdentificator) throws SourceProblemException, NoDataFoundException;
}
