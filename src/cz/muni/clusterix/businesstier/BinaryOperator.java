package cz.muni.clusterix.businesstier;

import cz.muni.clusterix.entities.ExecutionContext;

/**
 * Functional interface that represents binary operator. In Java 8, this
 * could be used to create Lambda expressions - current Clusterix implementation,
 * however, does not require Java 8 version, so anonymous classes are used 
 * instead.
 * 
 * @author Tomas Sezima
 */
public interface BinaryOperator {        
    
    /**
     * Applies operator to given values.
     * 
     * @param firstFunctionValue First value
     * @param secondFunctionValue Second value     
     * @param context Execution context     
     * @return The outcome of operator application
     */
    public float apply(float firstFunctionValue, float secondFunctionValue, ExecutionContext context);
    
}
