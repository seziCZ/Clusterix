package cz.muni.clusterix.commandline;

import cz.muni.clusterix.entities.Restrictions;
import cz.muni.clusterix.businesstier.StellarField;
import cz.muni.clusterix.entities.OpenCluster;
import cz.muni.clusterix.entities.Result;
import cz.muni.clusterix.entities.Star;
import cz.muni.clusterix.helpers.ClusterixConstants;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main classed that is used to run Clusterix in a command line fashion.
 *
 * @author Tomas Sezima
 */
public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {        
        try {
            // verify that used proposed expected number of params
            if (args == null || args.length != CommandlineConstants.EXPECTED_NUM_OF_ARGS) {
                throw new IllegalArgumentException("Data, configuration and output "
                        + "file paths have to be proposed as application parameters.");
            }

            DataAccessor manipulator = new DataAccessor();
            
            // retrieve configuration file
            Properties config = new Properties();
            Path configPath = Paths.get(args[CommandlineConstants.CONFIG_PATH_POSITION]);
            if(!Files.isReadable(configPath)){
                throw new FileSystemException("Proposed configuration file ( " + configPath + 
                        ") does not exists or could not be read.");
            }            
            config.load(Files.newInputStream(configPath));
            manipulator.validateConfig(config);
            
            // retrieve output file
            Path outputPath = Paths.get(args[CommandlineConstants.OUTPUT_PATH_POSITION]);            
            
            // retrieve data file
            Path dataPath = Paths.get(args[CommandlineConstants.DATA_PATH_POSITION]);
            if(!Files.isReadable(dataPath)){
                throw new FileSystemException("Proposed data file (" + dataPath + 
                        ")does not exists or could not be read.");
            }
            
            // execute            
            Set<Star> stars = manipulator.getStars(dataPath);
            OpenCluster cluster = manipulator.getCluster(config);
            Restrictions restrictions = manipulator.getRestriction(config);
            StellarField field = new StellarField(stars);             
            Result probabilities = field.evaluateProbabilities(cluster, 
                    cluster.getDefaultMask(ClusterixConstants.DEFAULT_MASK_DENSITY), restrictions);
            
            // store data
            manipulator.writeResults(outputPath, config, restrictions, cluster, probabilities);
        } catch (Exception ex) {            
            // print to standard output
            String message = ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage();            
            log.log(Level.SEVERE, "EXECUTION ABORTED: {0}", message);            
        }
    }
}
