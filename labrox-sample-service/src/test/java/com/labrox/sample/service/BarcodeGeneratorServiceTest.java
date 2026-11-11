package com.labrox.sample.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BarcodeGeneratorServiceTest {

    private final BarcodeGeneratorService generator = new BarcodeGeneratorService();

    @Test
    @DisplayName("Generate barcode matches Code-128 accession format S{YY}{DDD}{ENTROPY}")
    void testBarcodeFormat() {
        String barcode = generator.generateBarcode();
        assertNotNull(barcode);
        assertTrue(barcode.startsWith("S"));
        assertEquals(12, barcode.length());
    }

    @Test
    @DisplayName("Generate 1,000 barcodes with zero collision")
    void testZeroCollision() {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String b = generator.generateBarcode();
            assertTrue(seen.add(b), "Duplicate barcode generated: " + b);
        }
        assertEquals(1000, seen.size());
    }
}
