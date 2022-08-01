package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AssertTest {

    @Test
    public void test() {
        assertTrue(true);
        assertFalse(false);

        assertEquals(1, 1);
        assertEquals(0.51234, 0.512, 0.001);
        assertEquals(Math.PI, 3.14, 0.01);

        int i = 5; // primitivo
        Integer i2 = 5; // objeto
        assertEquals(Integer.valueOf(i), i2);
        assertEquals(i, i2.intValue());

        assertEquals("bola", "bola");
        assertTrue("bola".equalsIgnoreCase("Bola"));
        assertTrue("bola".startsWith("bo"));

    }

}