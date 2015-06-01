package cz.muni.clusterix.exceptions;

/**
 * An exception that is thrown when problems with data source emerge.
 *
 * @author Tomas Sezima
 */
public class SourceProblemException extends Exception {

    public SourceProblemException() {
        super();
    }

    public SourceProblemException(String s) {
        super(s);
    }

    public SourceProblemException(String s, Exception ex) {
        super(s);
    }

}
