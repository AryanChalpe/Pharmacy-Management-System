package com.pharmacy.management.repository;

import com.pharmacy.management.model.User;
import com.pharmacy.management.model.Role; // Added import for Role
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    long countByRole(Role role);
}
