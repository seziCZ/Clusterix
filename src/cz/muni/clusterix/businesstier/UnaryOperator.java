package cz.muni.clusterix.businesstier;

import cz.muni.clusterix.entities.ExecutionContext;

/**
 * Functional interface that represents unary operator. In Java 8, this
 * could be used to create Lambda expressions - current Clusterix implementation,
 * however, does not require Java 8 version, so anonymous classes are used 
 * instead.
 * 
 * @author Tomas Sezima
 */
public interface UnaryOperator {

    /**
     * Applies operation to given values.
     * 
     * @param functionValue First value
     * @param context Evaluation context
     * @return Resulting number
     */
    public float apply(float functionValue, ExecutionContext context);

}
