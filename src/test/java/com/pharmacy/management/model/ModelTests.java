package com.pharmacy.management.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModelTests {

    @Test
    void medicineModelTest() {
        Medicine medicine = new Medicine();
        medicine.setId("1");
        medicine.setName("Paracetamol");
        medicine.setPrice(10.0);
        medicine.setQuantity(100);
        medicine.setExpiryDate("2025-12-31");
        medicine.setAdminId("admin1");
        medicine.setExpired(false);

        assertEquals("1", medicine.getId());
        assertEquals("Paracetamol", medicine.getName());
        assertEquals(10.0, medicine.getPrice());
        assertEquals(100, medicine.getQuantity());
        assertEquals("2025-12-31", medicine.getExpiryDate());
        assertEquals("admin1", medicine.getAdminId());
        assertFalse(medicine.isExpired());
    }

    @Test
    void userModelTest() {
        User user = new User();
        user.setId("u1");
        user.setUsername("john");
        user.setPassword("pass");
        user.setRole(Role.ADMIN);

        assertEquals("u1", user.getId());
        assertEquals("john", user.getUsername());
        assertEquals("pass", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());

        User user2 = new User("jane", "pass2", Role.USER);
        assertEquals("jane", user2.getUsername());
    }
}
