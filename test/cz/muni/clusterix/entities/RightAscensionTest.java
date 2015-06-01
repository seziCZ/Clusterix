/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.clusterix.entities;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of right ascension entity.
 * @author Tomas Sezima
 */
public class RightAscensionTest {
    
    public RightAscensionTest() {}

    /**
     * Test of constructors, of class RightAscension.
     */
    @Test
    public void testConstructors() {
        RightAscension firstRa = new RightAscension(2000, 9, 42, 6.0f, 0.0f);
        RightAscension secondRa = new RightAscension(2000, 145.5250f, 0.0f);                        
        assertEquals(firstRa.getDegrees(), secondRa.getDegrees(), 0.001);        
    }
    
    
}
