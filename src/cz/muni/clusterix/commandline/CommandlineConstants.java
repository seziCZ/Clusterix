package cz.muni.clusterix.commandline;

/**
 * This class holds Clusterix constants related to command line execution.
 * 
 * @author Tomas Sezima
 */
public class CommandlineConstants {                   
    
    private CommandlineConstants(){}
    
    // parameter relevant constants
    public static final int EXPECTED_NUM_OF_ARGS = 3;            
    public static final int DATA_PATH_POSITION = 0;
    public static final int CONFIG_PATH_POSITION = 1;
    public static final int OUTPUT_PATH_POSITION = 2;
    
    // config constants        
    public static final String CLUSTER_RA_PARAM = "cluster_ra";
    public static final String CLUSTER_DEC_PARAM = "cluster_dec";
    public static final String CLUSTER_DIAMETER_PARAM = "cluster_radius";
    public static final String CLUSTER_OUTER_DIAMETER = "cluster_outer_radius";
    
    public static final String DATA_MAX_MU_PARAM = "data_max_mu";
    public static final String DATA_MAX_MU_ERR_PARAM = "data_max_mu_err";    
    // public static final String DATA_MAX_MAGNITUDE_PARAM= "data_max_magnitude";    
    public static final String DATA_SMOOTH_PARAM= "data_smooth_param";    
    public static final String DATA_GAMMA_PARAM= "data_gamma_treshold";    
    
    public static final String NUM_OF_MEMBERS = "expected_num_of_members";
    
    // configuration file properties        
    public static final String[] MANDATORY_CONFIG_KEYS = new String[]{
        CLUSTER_RA_PARAM, CLUSTER_DEC_PARAM, CLUSTER_DIAMETER_PARAM,         
    };
    
    public static final String[] VOLUNTARY_CONFIG_KEYS = new String[]{
        DATA_MAX_MU_PARAM, DATA_MAX_MU_ERR_PARAM, // DATA_MAX_MAGNITUDE_PARAM, 
                DATA_SMOOTH_PARAM, DATA_GAMMA_PARAM, CLUSTER_OUTER_DIAMETER
    };    
    
}
