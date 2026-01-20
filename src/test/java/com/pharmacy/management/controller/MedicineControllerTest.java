package com.pharmacy.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.management.dto.BillingRequest;
import com.pharmacy.management.model.Medicine;
import com.pharmacy.management.service.MedicineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicineController.class)
@ContextConfiguration(classes = { MedicineController.class,
                com.pharmacy.management.exception.GlobalExceptionHandler.class })
@AutoConfigureMockMvc(addFilters = false)
class MedicineControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private MedicineService medicineService;

        @Autowired
        private ObjectMapper objectMapper;

        private Medicine medicine;
        private Principal principal;

        @BeforeEach
        void setUp() {
                medicine = new Medicine();
                medicine.setId("med123");
                medicine.setName("Paracetamol");
                medicine.setPrice(10.0);
                medicine.setQuantity(100);

                principal = mock(Principal.class);
                when(principal.getName()).thenReturn("adminUser");
        }

        @Test
        void getAllMedicines_ShouldReturnList() throws Exception {
                when(medicineService.getAllMedicines(anyString())).thenReturn(Arrays.asList(medicine));

                mockMvc.perform(get("/api/medicines").principal(principal))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].name").value("Paracetamol"));
        }

        @Test
        void getMedicineById_ShouldReturnMedicine() throws Exception {
                when(medicineService.getMedicineById(anyString(), anyString())).thenReturn(medicine);

                mockMvc.perform(get("/api/medicines/med123").principal(principal))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Paracetamol"));
        }

        @Test
        void addMedicine_ShouldReturnSavedMedicine() throws Exception {
                when(medicineService.addMedicine(any(), anyString())).thenReturn(medicine);

                mockMvc.perform(post("/api/medicines")
                                .principal(principal)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(medicine)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Paracetamol"));
        }

        @Test
        void sellMedicine_ShouldReturnUpdatedMedicine() throws Exception {
                when(medicineService.sellMedicine(anyString(), anyInt(), anyString())).thenReturn(medicine);

                mockMvc.perform(post("/api/medicines/med123/sell")
                                .param("quantity", "5")
                                .principal(principal))
                                .andExpect(status().isOk());
        }

        @Test
        void updateMedicine_ShouldReturnUpdatedMedicine() throws Exception {
                when(medicineService.updateMedicine(anyString(), any(), anyString())).thenReturn(medicine);

                mockMvc.perform(put("/api/medicines/med123")
                                .principal(principal)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(medicine)))
                                .andExpect(status().isOk());
        }

        @Test
        void deleteMedicine_ShouldReturnSuccessMessage() throws Exception {
                mockMvc.perform(delete("/api/medicines/med123").principal(principal))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Medicine deleted successfully"));
        }

        @Test
        void billMedicine_ShouldReturnSuccessMessage() throws Exception {
                BillingRequest request = new BillingRequest();
                request.setMedicineName("Paracetamol");
                request.setQuantity(5);
                request.setUserEmail("test@example.com");

                mockMvc.perform(post("/api/medicines/bill")
                                .principal(principal)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Billing successful. Email sent."));
        }
}
