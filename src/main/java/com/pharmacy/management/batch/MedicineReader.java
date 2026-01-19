package com.pharmacy.management.batch;

import com.pharmacy.management.model.Medicine;
import com.pharmacy.management.repository.MedicineRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class MedicineReader implements ItemReader<Medicine> {

    @Autowired
    private MedicineRepository medicineRepository;

    private Iterator<Medicine> iterator;

    @Override
    public Medicine read() {
        if (iterator == null) {
            iterator = medicineRepository.findAll().iterator();
        }
        return iterator.hasNext() ? iterator.next() : null;
    }
}
