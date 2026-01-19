package com.pharmacy.management.repository;

import com.pharmacy.management.model.Medicine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicineRepository extends MongoRepository<Medicine, String> {
    Optional<Medicine> findByName(String name);

    java.util.List<Medicine> findByAdminId(String adminId);

    org.springframework.data.domain.Page<Medicine> findByAdminId(String adminId,
            org.springframework.data.domain.Pageable pageable);

    Optional<Medicine> findByNameAndAdminId(String name, String adminId);

    Optional<Medicine> findByIdAndAdminId(String id, String adminId);
}
