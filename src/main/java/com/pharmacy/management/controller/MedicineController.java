package com.pharmacy.management.controller;

import com.pharmacy.management.model.Medicine;
import com.pharmacy.management.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @GetMapping
    public Object getAllMedicines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean paginate,
            java.security.Principal principal) {
        if (paginate) {
            return medicineService.getPaginatedMedicines(principal.getName(), page, size);
        }
        return medicineService.getAllMedicines(principal.getName());
    }

    @GetMapping("/{id}")
    public Medicine getMedicineById(@PathVariable String id, java.security.Principal principal) {
        return medicineService.getMedicineById(id, principal.getName());
    }

    @PostMapping
    public Medicine addMedicine(@Valid @RequestBody Medicine medicine, java.security.Principal principal) {
        return medicineService.addMedicine(medicine, principal.getName());
    }

    @PostMapping("/{id}/sell")
    public Medicine sellMedicine(@PathVariable String id, @RequestParam int quantity,
            java.security.Principal principal) {
        return medicineService.sellMedicine(id, quantity, principal.getName());
    }

    @PutMapping("/{id}")
    public Medicine updateMedicine(@PathVariable String id, @Valid @RequestBody Medicine medicineDetails,
            java.security.Principal principal) {
        return medicineService.updateMedicine(id, medicineDetails, principal.getName());
    }

    @DeleteMapping("/{id}")
    public String deleteMedicine(@PathVariable String id, java.security.Principal principal) {
        medicineService.deleteMedicine(id, principal.getName());
        return "Medicine deleted successfully";
    }

    @PostMapping("/bill")
    public String billMedicine(@Valid @RequestBody com.pharmacy.management.dto.BillingRequest request,
            java.security.Principal principal) {
        medicineService.processBilling(request, principal.getName());
        return "Billing successful. Email sent.";
    }

}
