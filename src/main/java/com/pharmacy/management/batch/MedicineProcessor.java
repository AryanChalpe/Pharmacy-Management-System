package com.pharmacy.management.batch;

import com.pharmacy.management.model.Medicine;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class MedicineProcessor implements ItemProcessor<Medicine, Medicine> {

    @Override
    public Medicine process(Medicine medicine) {
        if (medicine.getExpiryDate() != null) {
            try {
                LocalDate expiryDate = LocalDate.parse(medicine.getExpiryDate());
                if (expiryDate.isBefore(LocalDate.now())) {
                    medicine.setExpired(true);
                }
            } catch (Exception e) {
                // Handle invalid date formats if necessary
            }
        }
        return medicine;
    }
}
