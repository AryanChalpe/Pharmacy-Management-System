package com.pharmacy.management.controller;

import com.pharmacy.management.exception.MedicineNotFoundException;
import com.pharmacy.management.model.Medicine;
import com.pharmacy.management.service.MedicineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicineController.class)
@ContextConfiguration(classes = { MedicineController.class,
        com.pharmacy.management.exception.GlobalExceptionHandler.class })
@AutoConfigureMockMvc(addFilters = false)
public class MedicineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicineService medicineService;

    @Test
    void getMedicineById_WhenExists_ShouldReturnMedicine() throws Exception {
        Medicine medicine = new Medicine();
        medicine.setId("med1");
        medicine.setName("Paracetamol");

        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("admin");

        when(medicineService.getMedicineById(eq("med1"), anyString())).thenReturn(medicine);

        mockMvc.perform(get("/api/medicines/med1").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Paracetamol"));
    }

    @Test
    void getMedicineById_WhenNotExists_ShouldReturn404() throws Exception {
        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("admin");

        when(medicineService.getMedicineById(eq("med-absent"), anyString()))
                .thenThrow(new MedicineNotFoundException("Medicine not found"));

        mockMvc.perform(get("/api/medicines/med-absent").principal(mockPrincipal))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
