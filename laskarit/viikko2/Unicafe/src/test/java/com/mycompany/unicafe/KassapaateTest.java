/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.unicafe;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author otsohelos
 */
public class KassapaateTest {

    Kassapaate kassa;
    Maksukortti kortti;

    @Before
    public void setUp() {
        this.kassa = new Kassapaate();
        this.kortti = new Maksukortti(1000);
    }

    @Test
    public void luotuPaateOikein() {
        assertEquals(100000, kassa.kassassaRahaa());
        assertEquals(0, kassa.edullisiaLounaitaMyyty());
        assertEquals(0, kassa.maukkaitaLounaitaMyyty());
    }

    @Test
    public void kateisostoToimii() {
        assertEquals(260, kassa.syoEdullisesti(500));
        assertEquals(100, kassa.syoMaukkaasti(500));
        assertEquals(100640, kassa.kassassaRahaa());
    }

    @Test
    public void kateisostoEiOnnistuJosRahaaLiianVahan() {
        assertEquals(200, kassa.syoEdullisesti(200));
        assertEquals(150, kassa.syoMaukkaasti(150));
        assertEquals(100000, kassa.kassassaRahaa());
    }

    @Test
    public void korttimaksuToimii() {
        assertTrue(kassa.syoEdullisesti(kortti));
        assertTrue(kassa.syoMaukkaasti(kortti));
        assertEquals(1, kassa.maukkaitaLounaitaMyyty());
        assertEquals(1, kassa.edullisiaLounaitaMyyty());
        assertEquals(360, kortti.saldo());
    }

    @Test
    public void korttimaksuEiToimiJosKortillaEiRahaa() {
        kassa.syoMaukkaasti(kortti);
        kassa.syoMaukkaasti(kortti);
        // nyt saldo on 200
        assertEquals(200, kortti.saldo());

        assertFalse(kassa.syoEdullisesti(kortti));
        assertFalse(kassa.syoMaukkaasti(kortti));
        assertEquals(2, kassa.maukkaitaLounaitaMyyty());
        assertEquals(200, kortti.saldo());
    }

    @Test
    public void rahanLatausToimii() {
        kassa.lataaRahaaKortille(kortti, 1000);
        assertEquals(2000, kortti.saldo());
        assertEquals(101000, kassa.kassassaRahaa());
    }

    @Test
    public void negatiivisenLatausEiTeeMitaan() {
        kassa.lataaRahaaKortille(kortti, -100);
        assertEquals(1000, kortti.saldo());
        assertEquals(100000, kassa.kassassaRahaa());
    }
}
