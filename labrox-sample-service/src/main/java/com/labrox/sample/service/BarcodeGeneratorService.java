package com.labrox.sample.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BarcodeGeneratorService {

    private final AtomicInteger sequence = new AtomicInteger(100000);

    /**
     * Generates a high-density, Code-128 compliant clinical laboratory accession barcode.
     * Format: S{YEAR_2DIGIT}{JULIAN_DAY_3DIGIT}{6_DIGIT_SEQUENCE}
     * Example: S26307100001
     */
    public String generateBarcode() {
        LocalDate now = LocalDate.now();
        int year2d = now.getYear() % 100;
        int dayOfYear = now.getDayOfYear();
        int seq = sequence.incrementAndGet();
        if (seq > 999999) {
            sequence.set(100000);
            seq = 100000;
        }
        return String.format("S%02d%03d%06d", year2d, dayOfYear, seq);
    }
}
