package com.pharmacy.management.batch;

import com.pharmacy.management.model.Medicine;
import com.pharmacy.management.repository.MedicineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchComponentsTest {

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private MedicineReader medicineReader;

    @InjectMocks
    private MedicineWriter medicineWriter;

    @Test
    void medicineReader_ShouldReadUntilNull() {
        Medicine m1 = new Medicine();
        when(medicineRepository.findAll()).thenReturn(Arrays.asList(m1));

        assertEquals(m1, medicineReader.read());
        assertNull(medicineReader.read());
    }

    @Test
    void medicineWriter_ShouldSaveAll() {
        Medicine m1 = new Medicine();
        List<Medicine> items = Arrays.asList(m1);
        Chunk<Medicine> chunk = new Chunk<>(items);

        medicineWriter.write(chunk);

        verify(medicineRepository, times(1)).saveAll(items);
    }
}
