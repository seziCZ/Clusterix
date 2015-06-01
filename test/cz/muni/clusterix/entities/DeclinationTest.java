/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.clusterix.entities;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test of declination entity.
 * 
 * @author Tomas Sezima
 */
public class DeclinationTest {
    
    public DeclinationTest() {}

    /**
     * Test of constructors, of class Declination.
     */
    @Test
    public void testConstructors() {
        Declination firstDec = new Declination(2000, -3, 32, 1, 0.0f);
        Declination secondDec = new Declination(2000, -3.5336f, 0.0f);
        assertEquals(firstDec.getDegrees(), secondDec.getDegrees(), 0.001);        
    }
    
}
