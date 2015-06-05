package cz.muni.clusterix.entities;

import java.util.List;
import java.util.Map;

/**
 * Describes context of evaluated Operation (both binary and unary).
 *
 * @author Tomas Sezima
 */
public class ExecutionContext {

    // currently processed x coordinate
    private int x;
    // currently processed y coordinate
    private int y;
    // list of processed functions
    private final List<float[][]> functions;
    // arbitraty parameters
    private final Map<String, Object> parameters;

    public ExecutionContext(int x, int y, List<float[][]> functions, Map<String, Object> parameters) {
        this.x = x;
        this.y = y;
        this.functions = functions;
        this.parameters = parameters;
    }

    
    // getters and setters
    
    public int getCurrentXcoord() {
        return x;
    }

    public void setCurrentXcoord(int x) {
        this.x = x;
    }

    public int getCurrentYcoord() {
        return y;
    }

    public void setCurrentYcoord(int y) {
        this.y = y;
    }

    public List<float[][]> getFunctions() {
        return functions;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    
    // eq and hashcode

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExecutionContext other = (ExecutionContext) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.functions != other.functions && (this.functions == null || 
                !this.functions.equals(other.functions))) {
            return false;
        }
        if (this.parameters != other.parameters && (this.parameters == null || 
                !this.parameters.equals(other.parameters))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ExecutionContext{" + "x=" + x + ", y=" + y + ", functions=" + 
                functions + ", parameters=" + parameters + '}';
    }    
    
}
