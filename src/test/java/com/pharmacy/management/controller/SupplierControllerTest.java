package com.pharmacy.management.controller;

import com.pharmacy.management.model.Supplier;
import com.pharmacy.management.service.SupplierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplierController.class)
@ContextConfiguration(classes = { SupplierController.class,
        com.pharmacy.management.exception.GlobalExceptionHandler.class })
@AutoConfigureMockMvc(addFilters = false)
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupplierService supplierService;

    @Test
    void getAllSuppliers_ShouldReturnList() throws Exception {
        Supplier supplier = new Supplier();
        supplier.setId("sup1");

        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("admin");
        when(supplierService.getAllSuppliers(anyString())).thenReturn(Arrays.asList(supplier));

        mockMvc.perform(get("/api/suppliers").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("sup1"));
    }
}
