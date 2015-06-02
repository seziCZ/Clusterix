package cz.muni.clusterix.helpers;

/**
 * Basic constants used for data conversion, etc.
 * 
 * @author Tomas Sezima
 */
public class ClusterixConstants {
    
    private ClusterixConstants(){}
    
    public static final int NUM_OF_AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();        
    
    // default values
    public static final float DEFAULT_GAMMA_COEF = 3.0f;
    public static final float DEFAULT_OUTER_RADIUS = 2.0f; // times inner radius
    public static final double DEFAULT_MAX_MU = 40.0d; // arcmins
    public static final float DEFAULT_MAX_MU_ERR = 40.0f; //arcmins
    public static final int CURRENT_EPOCH = 2000;    
    public static final int DEFAULT_MASK_DENSITY = 101;
    
    // conversion constants        
    public static final int SEC_TO_ARCSEC = 15;
    public static final int SEC_IN_MINUTE = 60;
    public static final int ARCMINS_IN_DEGREE = 60;
    public static final int ARCSECS_IN_DEGREE = 3600;        
    
    // function evaluation params
    public static final String SCALE_PARAM = "scale_param";
    public static final String FUNCTION_CENTER_PARAM = "function_center";
    public static final String STARS_PARAM = "stars_param";
    public static final String RESTRICTIONS_PARAM = "restrictions_param";    
    public static final String TRESHOLD_PARAM = "treshold_param";        
    
}
