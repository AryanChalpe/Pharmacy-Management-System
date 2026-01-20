package com.pharmacy.management.controller;

import com.pharmacy.management.model.Supplier;
import com.pharmacy.management.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public List<Supplier> getAllSuppliers(Principal principal) {
        return supplierService.getAllSuppliers(principal.getName());
    }

    @PostMapping
    public Supplier addSupplier(@Valid @RequestBody Supplier supplier, Principal principal) {
        return supplierService.addSupplier(supplier, principal.getName());
    }

    @DeleteMapping("/{id}")
    public String deleteSupplier(@PathVariable String id, Principal principal) {
        supplierService.deleteSupplier(id, principal.getName());
        return "Supplier deleted successfully";
    }
}
