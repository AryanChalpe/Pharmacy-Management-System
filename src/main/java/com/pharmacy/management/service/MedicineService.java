package com.pharmacy.management.service;

import com.pharmacy.management.exception.InsufficientStockException;
import com.pharmacy.management.exception.MedicineNotFoundException;
import com.pharmacy.management.model.Medicine;
import com.pharmacy.management.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineService {

    private boolean isExpired(Medicine medicine) {
        if (medicine.isExpired())
            return true;
        if (medicine.getExpiryDate() == null || medicine.getExpiryDate().isEmpty())
            return false;
        try {
            java.time.LocalDate expiry = java.time.LocalDate.parse(medicine.getExpiryDate());
            return expiry.isBefore(java.time.LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private com.pharmacy.management.repository.SaleRepository saleRepository;

    public List<Medicine> getAllMedicines(String adminId) {
        return medicineRepository.findByAdminId(adminId);
    }

    public org.springframework.data.domain.Page<Medicine> getPaginatedMedicines(String adminId, int page, int size) {
        return medicineRepository.findByAdminId(adminId, org.springframework.data.domain.PageRequest.of(page, size));
    }

    public Medicine getMedicineById(String id, String adminId) {
        return medicineRepository.findByIdAndAdminId(id, adminId)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + id));
    }

    public Medicine addMedicine(Medicine medicine, String adminId) {
        medicine.setAdminId(adminId);
        return medicineRepository.save(medicine);
    }

    public Medicine sellMedicine(String id, int quantity, String adminId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Medicine medicine = medicineRepository.findByIdAndAdminId(id, adminId)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + id));

        if (medicine.getQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock. Available: " + medicine.getQuantity());
        }

        if (isExpired(medicine)) {
            throw new IllegalStateException("Cannot sell expired medicine");
        }

        medicine.setQuantity(medicine.getQuantity() - quantity);
        return medicineRepository.save(medicine);
    }

    public Medicine updateMedicine(String id, Medicine medicineDetails, String adminId) {
        Medicine medicine = medicineRepository.findByIdAndAdminId(id, adminId)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + id));

        medicine.setName(medicineDetails.getName());
        medicine.setDescription(medicineDetails.getDescription());
        medicine.setPrice(medicineDetails.getPrice());
        medicine.setQuantity(medicineDetails.getQuantity());

        return medicineRepository.save(medicine);
    }

    public void deleteMedicine(String id, String adminId) {
        Medicine medicine = medicineRepository.findByIdAndAdminId(id, adminId)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + id));
        medicineRepository.delete(medicine);
    }

    public void processBilling(com.pharmacy.management.dto.BillingRequest request, String adminId) {
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Medicine medicine = medicineRepository.findByNameAndAdminId(request.getMedicineName(), adminId)
                .orElseThrow(() -> new MedicineNotFoundException(
                        "Medicine not found with name: " + request.getMedicineName()));

        if (medicine.getQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock. Available: " + medicine.getQuantity());
        }

        if (isExpired(medicine)) {
            throw new IllegalStateException("Cannot sell expired medicine: " + medicine.getName());
        }

        int newQuantity = medicine.getQuantity() - request.getQuantity();
        double totalPrice = medicine.getPrice() * request.getQuantity();

        try {
            emailService.sendBillEmail(request.getUserEmail(), medicine, request.getQuantity(), totalPrice);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }

        if (newQuantity == 0) {
            medicineRepository.delete(medicine);
        } else {
            medicine.setQuantity(newQuantity);
            medicineRepository.save(medicine);
        }

        // Record Sale
        com.pharmacy.management.model.Sale sale = new com.pharmacy.management.model.Sale(
                adminId,
                medicine.getName(),
                request.getQuantity(),
                medicine.getPrice(),
                totalPrice,
                java.time.LocalDateTime.now());
        saleRepository.save(sale);
    }
}
