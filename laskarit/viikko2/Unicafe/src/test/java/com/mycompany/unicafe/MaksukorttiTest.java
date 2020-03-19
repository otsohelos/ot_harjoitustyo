package com.mycompany.unicafe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MaksukorttiTest {

    Maksukortti kortti;

    @Before
    public void setUp() {
        kortti = new Maksukortti(1000);
    }

    @Test
    public void luotuKorttiOlemassa() {
        assertTrue(kortti != null);
    }

    @Test
    public void saldoAlussaOikein() {
        assertEquals("saldo: 10.0", kortti.toString());
    }

    @Test
    public void rahanLisaaminenToimii() {
        kortti.lataaRahaa(500);
        assertEquals("saldo: 15.0", kortti.toString());
    }

    @Test
    public void rahanOttaminenToimii() {
        kortti.otaRahaa(500);
        assertEquals("saldo: 5.0", kortti.toString());
    }

    @Test
    public void eiVoiOttaaLiikaa() {
        kortti.otaRahaa(700);
        assertEquals("saldo: 3.0", kortti.toString());
        kortti.otaRahaa(700);
        assertEquals("saldo: 3.0", kortti.toString());
    }

    @Test
    public void palautuksetOikein() {
        assertEquals(kortti.otaRahaa(700), true);
        assertEquals(kortti.otaRahaa(700), false);
    }

    @Test
    public void saldoToimii() {
        assertEquals(1000, kortti.saldo());
    }
}
