package com.pharmacy.management.service;

import com.pharmacy.management.dto.BillingRequest;
import com.pharmacy.management.exception.InsufficientStockException;
import com.pharmacy.management.exception.MedicineNotFoundException;
import com.pharmacy.management.model.Medicine;
import com.pharmacy.management.model.Sale;
import com.pharmacy.management.repository.MedicineRepository;
import com.pharmacy.management.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicineServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private MedicineService medicineService;

    private Medicine medicine;
    private final String adminId = "admin123";

    @BeforeEach
    void setUp() {
        medicine = new Medicine();
        medicine.setId("med1");
        medicine.setName("Paracetamol");
        medicine.setPrice(10.0);
        medicine.setQuantity(100);
        medicine.setAdminId(adminId);
    }

    @Test
    void getAllMedicines_ShouldReturnList() {
        when(medicineRepository.findByAdminId(adminId)).thenReturn(Arrays.asList(medicine));

        List<Medicine> result = medicineService.getAllMedicines(adminId);

        assertEquals(1, result.size());
        assertEquals("Paracetamol", result.get(0).getName());
        verify(medicineRepository, times(1)).findByAdminId(adminId);
    }

    @Test
    void getMedicineById_WhenExists_ShouldReturnMedicine() {
        when(medicineRepository.findByIdAndAdminId("med1", adminId)).thenReturn(Optional.of(medicine));

        Medicine result = medicineService.getMedicineById("med1", adminId);

        assertNotNull(result);
        assertEquals("Paracetamol", result.getName());
    }

    @Test
    void getMedicineById_WhenNotExists_ShouldThrowMedicineNotFoundException() {
        when(medicineRepository.findByIdAndAdminId("med1", adminId)).thenReturn(Optional.empty());

        assertThrows(MedicineNotFoundException.class, () -> medicineService.getMedicineById("med1", adminId));
    }

    @Test
    void addMedicine_ShouldSaveAndReturn() {
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        Medicine result = medicineService.addMedicine(new Medicine(), adminId);

        assertNotNull(result);
        assertEquals(adminId, result.getAdminId());
        verify(medicineRepository, times(1)).save(any(Medicine.class));
    }

    @Test
    void sellMedicine_WhenStockAvailable_ShouldReduceQuantity() {
        when(medicineRepository.findByIdAndAdminId("med1", adminId)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        int sellQty = 10;
        int expectedQty = medicine.getQuantity() - sellQty;

        Medicine result = medicineService.sellMedicine("med1", sellQty, adminId);

        assertEquals(expectedQty, result.getQuantity());
        verify(medicineRepository, times(1)).save(medicine);
    }

    @Test
    void sellMedicine_WhenInsufficientStock_ShouldThrowInsufficientStockException() {
        when(medicineRepository.findByIdAndAdminId("med1", adminId)).thenReturn(Optional.of(medicine));

        assertThrows(InsufficientStockException.class, () -> medicineService.sellMedicine("med1", 150, adminId));
        verify(medicineRepository, never()).save(any());
    }

    @Test
    void sellMedicine_WhenQuantityIsZero_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> medicineService.sellMedicine("med1", 0, adminId));
        verify(medicineRepository, never()).findByIdAndAdminId(anyString(), anyString());
    }

    @Test
    void processBilling_ShouldSendEmailAndRecordSale() throws Exception {
        BillingRequest request = new BillingRequest();
        request.setMedicineName("Paracetamol");
        int sellQty = 5;
        request.setQuantity(sellQty);
        request.setUserEmail("customer@example.com");

        double expectedTotal = medicine.getPrice() * sellQty;
        int expectedNewQty = medicine.getQuantity() - sellQty;

        when(medicineRepository.findByNameAndAdminId("Paracetamol", adminId)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        medicineService.processBilling(request, adminId);

        assertEquals(expectedNewQty, medicine.getQuantity());
        verify(emailService, times(1)).sendBillEmail(eq("customer@example.com"), eq(medicine), eq(sellQty),
                eq(expectedTotal));
        verify(saleRepository, times(1)).save(any(Sale.class));
        verify(medicineRepository, times(1)).save(medicine);
    }

    @Test
    void getPaginatedMedicines_ShouldReturnPage() {
        org.springframework.data.domain.Page<Medicine> page = new org.springframework.data.domain.PageImpl<>(
                Arrays.asList(medicine));
        when(medicineRepository.findByAdminId(eq(adminId), any(org.springframework.data.domain.PageRequest.class)))
                .thenReturn(page);

        org.springframework.data.domain.Page<Medicine> result = medicineService.getPaginatedMedicines(adminId, 0, 10);

        assertEquals(1, result.getContent().size());
        verify(medicineRepository).findByAdminId(eq(adminId), any());
    }

    @Test
    void sellMedicine_WhenExpired_ShouldThrowIllegalStateException() {
        medicine.setExpired(true);
        when(medicineRepository.findByIdAndAdminId("med1", adminId)).thenReturn(Optional.of(medicine));

        assertThrows(IllegalStateException.class, () -> medicineService.sellMedicine("med1", 10, adminId));
    }

    @Test
    void sellMedicine_WhenDateBeforeToday_ShouldThrowIllegalStateException() {
        medicine.setExpiryDate(java.time.LocalDate.now().minusDays(1).toString());
        when(medicineRepository.findByIdAndAdminId("med1", adminId)).thenReturn(Optional.of(medicine));

        assertThrows(IllegalStateException.class, () -> medicineService.sellMedicine("med1", 10, adminId));
    }

    @Test
    void updateMedicine_WhenExists_ShouldUpdateAndSave() {
        Medicine details = new Medicine();
        details.setName("New Name");
        details.setQuantity(50);

        when(medicineRepository.findByIdAndAdminId("med1", adminId)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        Medicine result = medicineService.updateMedicine("med1", details, adminId);

        assertEquals("New Name", medicine.getName());
        assertEquals(50, medicine.getQuantity());
        verify(medicineRepository).save(medicine);
    }

    @Test
    void deleteMedicine_WhenExists_ShouldDelete() {
        when(medicineRepository.findByIdAndAdminId("med1", adminId)).thenReturn(Optional.of(medicine));

        medicineService.deleteMedicine("med1", adminId);

        verify(medicineRepository).delete(medicine);
    }

    @Test
    void processBilling_WhenEmailFails_ShouldThrowRuntimeException() throws Exception {
        BillingRequest request = new BillingRequest();
        request.setMedicineName("Paracetamol");
        request.setQuantity(5);
        request.setUserEmail("error@example.com");

        when(medicineRepository.findByNameAndAdminId("Paracetamol", adminId)).thenReturn(Optional.of(medicine));
        doThrow(new RuntimeException("Email error")).when(emailService).sendBillEmail(anyString(), any(), anyInt(),
                anyDouble());

        assertThrows(RuntimeException.class, () -> medicineService.processBilling(request, adminId));
    }

    @Test
    void processBilling_WhenExpired_ShouldThrowIllegalStateException() {
        medicine.setExpired(true);
        BillingRequest request = new BillingRequest();
        request.setMedicineName("Paracetamol");
        request.setQuantity(5);

        when(medicineRepository.findByNameAndAdminId("Paracetamol", adminId)).thenReturn(Optional.of(medicine));

        assertThrows(IllegalStateException.class, () -> medicineService.processBilling(request, adminId));
    }
}
