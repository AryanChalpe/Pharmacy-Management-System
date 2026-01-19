package com.pharmacy.management.controller;

import com.pharmacy.management.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "http://localhost:5173")
public class SaleController {

    @Autowired
    private SaleRepository saleRepository;

    @GetMapping
    public Object getAllSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean paginate,
            java.security.Principal principal) {
        if (paginate) {
            return saleRepository.findByAdminId(principal.getName(),
                    org.springframework.data.domain.PageRequest.of(page, size));
        }
        return saleRepository.findByAdminId(principal.getName());
    }
}
