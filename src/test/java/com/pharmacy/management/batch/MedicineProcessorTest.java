package com.pharmacy.management.batch;

import com.pharmacy.management.model.Medicine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MedicineProcessorTest {

    private MedicineProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new MedicineProcessor();
    }

    @Test
    void process_WhenDateIsExpired_ShouldSetExpiredTrue() {
        Medicine medicine = new Medicine();
        medicine.setExpiryDate(LocalDate.now().minusDays(1).toString());
        medicine.setExpired(false);

        Medicine result = processor.process(medicine);

        assertTrue(result.isExpired());
    }

    @Test
    void process_WhenDateIsFuture_ShouldKeepExpiredFalse() {
        Medicine medicine = new Medicine();
        medicine.setExpiryDate(LocalDate.now().plusDays(10).toString());
        medicine.setExpired(false);

        Medicine result = processor.process(medicine);

        assertFalse(result.isExpired());
    }

    @Test
    void process_WhenDateIsNull_ShouldDoNothing() {
        Medicine medicine = new Medicine();
        medicine.setExpiryDate(null);
        medicine.setExpired(false);

        Medicine result = processor.process(medicine);

        assertFalse(result.isExpired());
    }

    @Test
    void process_WhenDateIsInvalid_ShouldHandleExceptionGracefully() {
        Medicine medicine = new Medicine();
        medicine.setExpiryDate("invalid-date");
        medicine.setExpired(false);

        Medicine result = processor.process(medicine);

        assertFalse(result.isExpired()); // Should not crash and not set it to true
    }
}
