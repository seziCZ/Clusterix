package cz.muni.clusterix.businesstier;

import java.util.Map;

/**
 * Interface that describes methods required to describe empirical bivariate function.
 * 
 * @author Tomas Sezima
 */
public interface Function {
    
    /**
     * Empirical bivariate function may be described by matrix; this method is reponsible
     * for retrieval of such a representation.
     * 
     * @return Matrix that describes function
     */
    public float[][] getGrid();
    
    /**
     * Retrieves size of single matrix cell. I.e. each cell has
     * area getCellSize() * getCellSize().
     * 
     * @return cell size in arcsec
     */
    public double getCellsize();
    
    /**
     * Applies given unary operator on 'this' function.
     * 
     * @param operator Operator to be applied
     * @param context Evaluation context in form of map
     */
    public void applyUnaryOperation(UnaryOperator operator, Map<String, Object> context);
    
    /**
     * Applies binary operator on 'this' function and function given as parameter.
     * 
     * @param secondFunction Second argument of given binary operator
     * @param operator Operator to be applied
     * @param context Evaluation context in form of map
     */
    public void applyBinaryOperation(Function secondFunction, BinaryOperator operator, Map<String, Object> context);
    
}
