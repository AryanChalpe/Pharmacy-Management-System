package com.pharmacy.management.repository;

import com.pharmacy.management.model.Supplier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplierRepository extends MongoRepository<Supplier, String> {
    List<Supplier> findByAdminId(String adminId);
}
