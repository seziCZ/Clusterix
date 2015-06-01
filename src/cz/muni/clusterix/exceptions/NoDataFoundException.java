package cz.muni.clusterix.exceptions;

/**
 * An exception that is thrown when no data are available for requested object.
 *
 * @author Tomas Sezima
 */
public class NoDataFoundException extends Exception {

    public NoDataFoundException(String str) {
        super(str);
    }

    public NoDataFoundException(String str, Exception ex) {
        super(str, ex);
    }

}
