package cz.muni.clusterix.entities;

import cz.muni.clusterix.businesstier.FieldMask;
import java.util.List;

/**
 *
 * Result of the probability search. Class contains stars, their's probabilities
 * and values used during the calculatins encapsulated in a Restriction object.
 * Also, FieldMask proposed by user is part of the result.
 *
 * @author Tomas Sezima
 */
public class Result {

    // cluster and field specification
    private final OpenCluster cluster;
    private final ProperMotion field;        
    
    // actual result 
    private final List<Star> stars;
    private final int numOfMemebers;
    
    // user given options
    private final Restrictions rests;
    private final FieldMask mask;            

    
    //constructor
    
    /**
     * Constructor.
     *
     * @param cluster Open cluster that has been evaluated
     * @param field Proper motion of field stars
     * @param stars Stars with assigned probabilities
     * @param numOfMembers Expected number of cluster memebers
     * @param rests Restrictions containing values used during memberhip
     * probability computation
     * @param mask FieldMask proposed by user
     */
    public Result(OpenCluster cluster, ProperMotion field, List<Star> stars, 
            int numOfMembers, Restrictions rests, FieldMask mask) {
        this.cluster = cluster;
        this.field = field;
        this.stars = stars;
        this.numOfMemebers = numOfMembers;
        this.rests = rests;
        this.mask = mask;
    }

    
    //getters
    
    public FieldMask getMask() {
        return mask;
    }

    public Restrictions getRests() {
        return rests;
    }

    public List<Star> getStars() {
        return stars;
    }

    public OpenCluster getCluster() {
        return cluster;
    }

    public ProperMotion getFieldProperMotion() {
        return field;
    }

    public int getNumOfMembers() {
        return numOfMemebers;
    }        

    
    //equals and hashcode

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.cluster != null ? this.cluster.hashCode() : 0);
        hash = 31 * hash + (this.field != null ? this.field.hashCode() : 0);
        hash = 31 * hash + (this.stars != null ? this.stars.hashCode() : 0);
        hash = 31 * hash + this.numOfMemebers;
        hash = 31 * hash + (this.rests != null ? this.rests.hashCode() : 0);
        hash = 31 * hash + (this.mask != null ? this.mask.hashCode() : 0);
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
        final Result other = (Result) obj;
        if (this.cluster != other.cluster && (this.cluster == null || !this.cluster.equals(other.cluster))) {
            return false;
        }
        if (this.field != other.field && (this.field == null || !this.field.equals(other.field))) {
            return false;
        }
        if (this.stars != other.stars && (this.stars == null || !this.stars.equals(other.stars))) {
            return false;
        }
        if (this.numOfMemebers != other.numOfMemebers) {
            return false;
        }
        if (this.rests != other.rests && (this.rests == null || !this.rests.equals(other.rests))) {
            return false;
        }
        if (this.mask != other.mask && (this.mask == null || !this.mask.equals(other.mask))) {
            return false;
        }
        return true;
    }
        

    @Override
    public String toString() {
        return "Result{" + "cluster=" + cluster + ", field=" + field + ", stars=" 
                + stars + ", rests=" + rests + ", mask=" + mask + '}';
    }    
    
}
