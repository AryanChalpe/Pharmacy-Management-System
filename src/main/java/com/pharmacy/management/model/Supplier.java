package com.pharmacy.management.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Document(collection = "suppliers")
public class Supplier {

    @Id
    private String id;
    private String adminId;

    @NotBlank(message = "Supplier name is required")
    private String name;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @Email(message = "Invalid email format")
    private String email;

    private String address;

    // Constructors
    public Supplier() {
    }

    public Supplier(String adminId, String name, String contactNumber, String email, String address) {
        this.adminId = adminId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
