package cz.muni.clusterix.commandline;

import com.floreysoft.jmte.Engine;
import cz.muni.clusterix.entities.Restrictions;
import cz.muni.clusterix.entities.Declination;
import cz.muni.clusterix.entities.OpenCluster;
import cz.muni.clusterix.entities.ProperMotion;
import cz.muni.clusterix.entities.Result;
import cz.muni.clusterix.entities.RightAscension;
import cz.muni.clusterix.entities.Star;
import cz.muni.clusterix.helpers.ClusterixConstants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 * This class serves as an alternative to the dbtier managers used in web
 * version of Clusterix.
 *
 * @author Tomas Sezima
 */
public class DataAccessor {

    private static final Logger log = Logger.getLogger(DataAccessor.class.getName());

    private static final String COMMENT_CHAR = "#";
    private static final String DATA_SEPARATOR = "\t";
    private static final String OUTPUT_HEADER_PATH = "resultheader.vm";

    private final Engine engine;

    public DataAccessor() {
        this.engine = new Engine();
    }

    /**
     * Retrieves all Stars from given file. File have to have structure defined
     * in sample files (https://github.com/seziCZ/Clusterix), exception is thrown otherwise.
     *
     * @param path Path to file that contains relevant data
     * @return Set of retrieved Stars
     * @throws java.nio.file.FileSystemException
     */
    public Set<Star> getStars(Path path) throws FileSystemException, IOException {
        Set<Star> stars = new HashSet<Star>();
        BufferedReader reader = Files.newBufferedReader(path, Charset.defaultCharset());
        String line = reader.readLine();
        int lineNumberCounter = 1;
        // for each line...
        while (line != null) {
            // drop commented and empty lines
            if (!line.startsWith(COMMENT_CHAR) && !line.isEmpty()) {
                Scanner s = new Scanner(line).useDelimiter("\\t");
                s.useLocale(Locale.ENGLISH);

                try {
                    // retrieve parameters
                    int no = s.nextInt();
                    float ra = s.nextFloat();
                    float dec = s.nextFloat();
                    float ra_pm = s.nextFloat();
                    float ra_pm_err = s.nextFloat();
                    float dec_pm = s.nextFloat();
                    float dec_pm_err = s.nextFloat();
                    //store as star
                    RightAscension ascension = new RightAscension(ra, 0.0f);
                    Declination declination = new Declination(dec, 0.0f);
                    ProperMotion motion = new ProperMotion(ra_pm, ra_pm_err, dec_pm, dec_pm_err);

                    stars.add(new Star(no, 0.0f, ascension, declination, motion));
                } catch (Exception ex) {
                    throw new FileSystemException("Datafile " + path.getFileName()
                            + " has syntax error at line " + lineNumberCounter
                            + ". Underlying error is " + ex.getClass());
                }
            }
            lineNumberCounter++;
            line = reader.readLine();
        }
        return stars;
    }

    /**
     * Get cluster from proposed property file. Property has to have structure
     * defined by sample file (https://github.com/seziCZ/Clusterix). exception
     * is thrown otherwise.
     *
     * @param properties Properties containing Cluster relevant data
     * @return Retrieved OpenCluster
     */
    public OpenCluster getCluster(Properties properties) throws IllegalArgumentException {
        RightAscension ascension = new RightAscension(Float.valueOf(properties.getProperty(CommandlineConstants.CLUSTER_RA_PARAM)), 0.0f);
        Declination declination = new Declination(Float.valueOf(properties.getProperty(CommandlineConstants.CLUSTER_DEC_PARAM)), 0.0f);
        float radius = Float.valueOf(properties.getProperty(CommandlineConstants.CLUSTER_DIAMETER_PARAM));
        OpenCluster cluster = new OpenCluster(ascension.toString() + declination.toString(),
                ascension, declination, radius);

        // check for optional params
        if (properties.containsKey(CommandlineConstants.CLUSTER_OUTER_DIAMETER)) {            
            float outer_radius = Float.valueOf(properties.getProperty(CommandlineConstants.CLUSTER_OUTER_DIAMETER));
            cluster.setOutterRadius(outer_radius);
        }

        return cluster;
    }

    /**
     * Retrieves restrictions from proposed property file.
     *
     * @param properties Properties to be checked for restrictions
     * @return Retrieved restrictions
     */
    public Restrictions getRestriction(Properties properties) {
        Double maxMu = properties.containsKey(CommandlineConstants.DATA_MAX_MU_PARAM) ?
                Double.valueOf(properties.getProperty(CommandlineConstants.DATA_MAX_MU_PARAM)) : 
                    ClusterixConstants.DEFAULT_MAX_MU;
        Float maxMuErr = properties.containsKey(CommandlineConstants.DATA_MAX_MU_ERR_PARAM) ?
                Float.valueOf(properties.getProperty(CommandlineConstants.DATA_MAX_MU_ERR_PARAM)) : 
                    ClusterixConstants.DEFAULT_MAX_MU_ERR;
        Double smoothParam = properties.containsKey(CommandlineConstants.DATA_SMOOTH_PARAM) ?
                Double.valueOf(properties.getProperty(CommandlineConstants.DATA_SMOOTH_PARAM)) : null;
        Float gammaParam = properties.containsKey(CommandlineConstants.DATA_GAMMA_PARAM) ?
                Float.valueOf(properties.getProperty(CommandlineConstants.DATA_GAMMA_PARAM)) : null;
        return new Restrictions(null, maxMu, maxMuErr, smoothParam, gammaParam);
    }

    /**
     * Writes retrieved probabilities to proposed output file. File does not have
     * to exists, is rewriten if it does.
     *
     * @param outputPath Path to output file
     * @param properties Application properties
     * @param restrictions Execution relevant restrictions
     * @param cluster Processed open cluster
     * @param probabilities Retrieved probabilities
     * @throws java.nio.file.FileSystemException
     */
    public void writeResults(Path outputPath, Properties properties,
            Restrictions restrictions, OpenCluster cluster, Result probabilities) throws FileSystemException, IOException {
        InputStream headerInputStream = ClassLoader.getSystemResourceAsStream(OUTPUT_HEADER_PATH);
        String header = IOUtils.toString(headerInputStream, Charset.defaultCharset());

        //header could be processed with templating engine...
        Map<String, Object> params = new HashMap<String, Object>();
        for(String key : CommandlineConstants.MANDATORY_CONFIG_KEYS){
            params.put(key, properties.getProperty(key));
        }                
        
        // reconstruct optional params from restrictions        
        params.put(CommandlineConstants.CLUSTER_OUTER_DIAMETER, String.format(Locale.ENGLISH, "%.2f",cluster.getOutterRadius()));
        params.put(CommandlineConstants.DATA_GAMMA_PARAM, String.format(Locale.ENGLISH, "%.2f", restrictions.getGammaCoef()));        
        params.put(CommandlineConstants.DATA_MAX_MU_ERR_PARAM, String.format(Locale.ENGLISH, "%.2f", restrictions.getMaxMuErr()));
        params.put(CommandlineConstants.DATA_SMOOTH_PARAM, String.format(Locale.ENGLISH, "%.2f", restrictions.getSmoothParam()));
        
        // proper motion restrictions could have changed due to optimization, display original value
        double actualPlaneSize = properties.containsKey(CommandlineConstants.DATA_MAX_MU_PARAM) ?
                Double.valueOf(properties.getProperty(CommandlineConstants.DATA_MAX_MU_PARAM)) :
                    restrictions.getMaxMu();
        params.put(CommandlineConstants.DATA_MAX_MU_PARAM, String.format(Locale.ENGLISH, "%.2f", actualPlaneSize));
                
        // add info about expected number of cluster members
        params.put(CommandlineConstants.NUM_OF_MEMBERS, probabilities.getNumOfMembers());

        // Transform header template and append data
        String headerTransformed = engine.transform(header, params);
        StringBuilder builder = new StringBuilder(headerTransformed);
        List<Star> stars = probabilities.getStars();
        for (Star star : stars) {
            builder.append("\n")
                    .append(star.getNo()).append(DATA_SEPARATOR).append(DATA_SEPARATOR)
                    .append(String.format(Locale.ENGLISH, "%.6f", star.getRightAscension().getDegrees())).append(DATA_SEPARATOR)
                    .append(String.format(Locale.ENGLISH, "%.6f", star.getDeclination().getDegrees())).append(DATA_SEPARATOR)
                    .append(String.format(Locale.ENGLISH, "%.2f", star.getProperMotion().getMuAlpha())).append(DATA_SEPARATOR)
                    .append(String.format(Locale.ENGLISH, "%.2f", star.getProperMotion().getMuDelta())).append(DATA_SEPARATOR)
                    .append(String.format(Locale.ENGLISH, "%.2f", star.getProbability()));
        }

        // write to file
        Files.write(outputPath, builder.toString().getBytes());
    }

    /**
     * Asserts, that given property file contains mandatory and voluntary
     * properties specified in ClusterixConstants file.
     *
     * @param properties Properties to be validated
     */
    public void validateConfig(Properties properties) throws IllegalArgumentException {
        // validate voluntary properties first
        this.validateValuesAreFloats(properties, CommandlineConstants.VOLUNTARY_CONFIG_KEYS);
        Set<String> missingVoluntary = getMissingKeys(properties,
                CommandlineConstants.VOLUNTARY_CONFIG_KEYS);
        if (!missingVoluntary.isEmpty()) {
            log.log(Level.INFO, "Following optional parameters were not proposed: {0}",
                    this.toString(missingVoluntary));
        }

        // validate mandatory properties
        this.validateValuesAreFloats(properties, CommandlineConstants.MANDATORY_CONFIG_KEYS);
        Set<String> missingMandatory = getMissingKeys(properties,
                CommandlineConstants.MANDATORY_CONFIG_KEYS);
        if (!missingMandatory.isEmpty()) {
            throw new IllegalArgumentException("Following mandatory parameters "
                    + "were not proposed: " + this.toString(missingMandatory));
        }
    }

    
    // private helpers          

    private void validateValuesAreFloats(Properties properties, String[] keys) {
        for (String key : keys) {
            if (properties.containsKey(key)) {
                try {
                    Float.valueOf(properties.getProperty(key));
                } catch (NumberFormatException ex) {
                    throw new NumberFormatException("Property value for key '"
                            + key + "' has to be decimal number.");
                }
            }
        }
    }

    private Set<String> getMissingKeys(Properties properties, String[] keys) {
        Set<String> missing = new HashSet<String>();
        for (String key : keys) {
            if (!properties.containsKey(key)) {
                missing.add(key);
            }
        }
        return missing;
    }

    private String toString(Collection<String> elements) {
        StringBuilder builder = new StringBuilder();
        if (elements != null) {
            Iterator<String> iterator = elements.iterator();
            while (iterator.hasNext()) {
                builder.append(iterator.next());
                if (iterator.hasNext()) {
                    builder.append(", ");
                }
            }
        }
        return builder.toString();
    }
}
