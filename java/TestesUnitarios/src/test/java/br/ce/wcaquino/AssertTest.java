package br.ce.wcaquino;

import static org.junit.Assert.*;

import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTest {

    @Test
    public void test() {
        assertTrue(true);
        assertFalse(false);

        assertEquals(1, 1);
        // assertEquals("Erro comparação", 1, 2);
        assertEquals(0.51234, 0.512, 0.001);
        assertEquals(Math.PI, 3.14, 0.01);

        int i = 5; // primitivo
        Integer i2 = 5; // objeto
        assertEquals(Integer.valueOf(i), i2);
        assertEquals(i, i2.intValue());

        assertEquals("bola", "bola");
        assertNotEquals("bola", "casa");
        assertTrue("bola".equalsIgnoreCase("Bola"));
        assertTrue("bola".startsWith("bo"));

        Usuario u1 = new Usuario("Usuario 1");
        Usuario u2 = new Usuario("Usuario 1");
        // Usuario u3 = u2;
        Usuario u3 = null;

        assertEquals(u1, u2);

        // assertSame(u1, u2); //mesma instancia
        assertSame(u2, u2); //mesma instancia
        assertNotSame(u1, u2); //mesma instancia
        // assertSame(u3, u2); //mesma instancia
        assertTrue(u3 == null);
        assertNull(u3);
        assertNotNull(u2);
        
    
    
    }

}