package cz.muni.clusterix.dbtier;

import cz.muni.clusterix.businesstier.StellarField;
import cz.muni.clusterix.entities.Declination;
import cz.muni.clusterix.entities.OpenCluster;
import cz.muni.clusterix.entities.ProperMotion;
import cz.muni.clusterix.entities.RightAscension;
import cz.muni.clusterix.entities.Star;
import cz.muni.clusterix.exceptions.NoDataFoundException;
import cz.muni.clusterix.exceptions.SourceProblemException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * This class is responsible for data retrieval from WEBDA database. Unfortunatelly,
 * structure of the database is not well-defined and therefore no persitence frameworks
 * may be used.
 * 
 * TODO: implement JDBC connector or at least use Java 7 'try with resources' approach
 * to make code more readable...
 *
 * @author Tomas Sezima
 */
public class WebdaManagerImpl implements DbManager, Serializable {

    private static final String SOURCEURL = "http://webda.physics.muni.cz";
    private static final String CLUSTERPATH = "/contents/ocl.cat";
    private static final String STARPATH = "/ocl/";
    private static final Logger log = Logger.getLogger(WebdaManagerImpl.class.getName());

    @Override
    public List<OpenCluster> getAllClusters() throws SourceProblemException {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            URL url = new URL(SOURCEURL + CLUSTERPATH);
            URLConnection urlc = url.openConnection();
            urlc.setConnectTimeout(10000);
            is = urlc.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            List<OpenCluster> clusters = new ArrayList<OpenCluster>();

            //drop first two lines with database specific information
            for (int i = 0; i < 2; i++) {
                br.readLine();
            }

            // retrieve data
            String line = br.readLine();
            while (line != null) {
                // store cluster
                String[] values = line.split("\t");
                String identificator = values[1];
                RightAscension ra = new RightAscension(Float.valueOf(values[2].trim()), 0.0f);
                Declination de = new Declination(Float.valueOf(values[3].trim()), 0.0f);
                float radius = Float.valueOf(values[12]) / 2.0f;
                
                // restrict too big clusters
                if(radius < 30.0){
                    clusters.add(new OpenCluster(identificator, ra, de, radius));
                }                

                line = br.readLine();
            }
            return clusters;

        } catch (IOException ex) {
            throw new SourceProblemException("Clusters could not be retrived due to connection problems.", ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                log.error("Problem occured while trying to close connections.", ex);
            }
        }
    }

    @Override
    public OpenCluster getOpenCluster(String clusterIdentificator) throws SourceProblemException, NoDataFoundException {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            URL url = new URL(SOURCEURL + CLUSTERPATH);
            URLConnection urlc = url.openConnection();
            urlc.setConnectTimeout(50000);
            is = urlc.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            //find coresponding cluster
            String clusterDir = this.getDirName(clusterIdentificator);
            // retrieve data
            String line = br.readLine();
            while (line != null) {
                String[] values = line.split("\t");
                if (clusterDir.equals(values[20])) {
                    String identificator = values[1];
                    RightAscension ra = new RightAscension(Float.valueOf(values[2].trim()), 0.0f);
                Declination de = new Declination(Float.valueOf(values[3].trim()), 0.0f);
                    float radius = Float.valueOf(values[12]) / 2.0f;
                    return new OpenCluster(identificator, ra, de, radius);
                }
                line = br.readLine();
            }

            // if program reaches this point, there has to be problem with data
            // file structure
            log.error("Program failed to answer query '" + clusterIdentificator + "' even though"
                    + " a candidate was found in file '" + CLUSTERPATH + "'.");
            throw new IOException("Datafile used to retrive open cluster information is corrupted.");

        } catch (IOException ex) {
            throw new SourceProblemException("Program failed to retrieve data for queried cluster.", ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                log.error("Problem occured while trying to close connections.", ex);
            }
        }

    }

    @Override
    public StellarField getStellarField(String clusterIdentificator) throws SourceProblemException, NoDataFoundException {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        Set<Star> stars = new HashSet<Star>();

        try {            
            //establish connection
            URL url = new URL(this.getSourceUri(clusterIdentificator));
            URLConnection urlc = url.openConnection();
            urlc.setConnectTimeout(50000);
            is = urlc.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            //drop first two lines with database specific information
            for (int i = 0; i < 2; i++) {
                br.readLine();
            }
            String line = br.readLine();
            while (line != null) {
                String[] values = line.split("\t");

                // not used YET
                double raErr = Double.valueOf(values[4].trim());
                double decErr = Double.valueOf(values[5].trim());

                // data needed for membership determination
                int no = Integer.valueOf(values[0]);
                float magnitude = Float.valueOf(values[1].trim());
                RightAscension ra = new RightAscension(Float.valueOf(values[2].trim()), 0.0f);
                Declination dec = new Declination(Float.valueOf(values[3].trim()), 0.0f);
                float muAlpha = Float.valueOf(values[6].trim());
                float muDelta = Float.valueOf(values[7].trim());
                float muAlphaErr = Float.valueOf(values[8].trim());
                float muDeltaErr = Float.valueOf(values[9].trim());
                ProperMotion motion = new ProperMotion(muAlpha, muAlphaErr, muDelta, muDeltaErr);
                stars.add(new Star(no, magnitude, ra, dec, motion));

                line = br.readLine();
            }

            return new StellarField(stars);

        } catch (IOException ex) {
            throw new SourceProblemException(clusterIdentificator + " does not "
                    + "contains data needed for membership estimation.", ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                log.error("Problem occured while trying to close connections.", ex);
            }

        }
    }

    @Override
    public String getSourceUri(String clusterIdentificator) throws SourceProblemException, NoDataFoundException {        
        String dir = this.getDirName(clusterIdentificator);
        return SOURCEURL + STARPATH + dir + "/clusterix.dat";
    }

    
    //private helpers
    
    private String getDirName(String identificator) throws NoDataFoundException, SourceProblemException {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            URL url = new URL(SOURCEURL + CLUSTERPATH);
            URLConnection urlc = url.openConnection();
            urlc.setConnectTimeout(10000);
            is = urlc.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            //retrieve dir name for given phrase            
            String[] values;
            String line = br.readLine();
            while (line != null) {
                values = line.split("\t");
                if (identificator.toLowerCase().replaceAll("\\s+", "").equals(values[1].toLowerCase().replaceAll("\\s+", ""))
                        || identificator.toLowerCase().replaceAll("\\s+", "").equals(values[20].toLowerCase().replaceAll("\\s+", ""))) {
                    return values[20];
                }
                line = br.readLine();
            }

            log.info("Database was queried for '" + identificator + "', but no results were found.");
            throw new NoDataFoundException("No data found for query '" + identificator + "'.");

        } catch (IOException ex) {
            log.error("Problems occured while retrieving info from file '" + CLUSTERPATH + "'.", ex);
            throw new SourceProblemException("Connection was established, but program failed to read queried files.", ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                log.error("Problem occured while trying to close connections.", ex);
            }
        }
    }
}
