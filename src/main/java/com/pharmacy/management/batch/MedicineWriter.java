package com.pharmacy.management.batch;

import com.pharmacy.management.model.Medicine;
import com.pharmacy.management.repository.MedicineRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MedicineWriter implements ItemWriter<Medicine> {

    @Autowired
    private MedicineRepository medicineRepository;

    @Override
    public void write(Chunk<? extends Medicine> chunk) {
        medicineRepository.saveAll(chunk.getItems());
    }
}
