package com.pharmacy.management.controller;

import com.pharmacy.management.model.Sale;
import com.pharmacy.management.repository.SaleRepository;
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

@WebMvcTest(SaleController.class)
@ContextConfiguration(classes = { SaleController.class,
        com.pharmacy.management.exception.GlobalExceptionHandler.class })
@AutoConfigureMockMvc(addFilters = false)
class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SaleRepository saleRepository;

    @Test
    void getAllSales_ShouldReturnList() throws Exception {
        Sale sale = new Sale();
        sale.setId("sale1");

        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("admin");
        when(saleRepository.findByAdminId(anyString())).thenReturn(Arrays.asList(sale));

        mockMvc.perform(get("/api/sales").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("sale1"));
    }
}
