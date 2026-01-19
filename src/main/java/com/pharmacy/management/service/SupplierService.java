package com.pharmacy.management.service;

import com.pharmacy.management.model.Supplier;
import com.pharmacy.management.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliers(String adminId) {
        return supplierRepository.findByAdminId(adminId);
    }

    public Supplier addSupplier(Supplier supplier, String adminId) {
        supplier.setAdminId(adminId);
        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(String id, String adminId) {
        // Ensure the supplier belongs to the admin before deleting
        supplierRepository.findById(id).ifPresent(supplier -> {
            if (supplier.getAdminId().equals(adminId)) {
                supplierRepository.delete(supplier);
            } else {
                throw new RuntimeException("Unauthorized delete attempt");
            }
        });
    }
}
