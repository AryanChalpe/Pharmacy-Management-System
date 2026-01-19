package com.pharmacy.management.repository;

import com.pharmacy.management.model.Sale;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends MongoRepository<Sale, String> {
    java.util.List<Sale> findByAdminId(String adminId);

    org.springframework.data.domain.Page<Sale> findByAdminId(String adminId,
            org.springframework.data.domain.Pageable pageable);
}
