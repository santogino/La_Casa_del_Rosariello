package com.la_casa_del_rosariello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.la_casa_del_rosariello.dto.BookingRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateBookingSuccess() throws Exception {
        BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
        bookingRequestDTO.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequestDTO.setCheckOutDate(LocalDate.now().plusDays(3));
        bookingRequestDTO.setNumberOfGuests(2);
        bookingRequestDTO.setNome("Mario");
        bookingRequestDTO.setCognome("Rossi");
        bookingRequestDTO.setEmail("mario.rossi@example.com");

        mockMvc.perform(post("/api/bookings")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(bookingRequestDTO)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").exists()); // Assumendo che la risposta di successo contenga un campo 'id'
    }

    @Test
    void testCreateBookingValidationFailure() throws Exception {
        BookingRequestDTO bookingRequestDTO = new BookingRequestDTO();
        // Non settare i campi obbligatori per causare un errore di validazione

        mockMvc.perform(post("/api/bookings")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(bookingRequestDTO)))
               .andExpect(status().isBadRequest());
               // Potresti aggiungere verifiche sul corpo della risposta per i messaggi di errore specifici
    }

    // Aggiungi altri test di integrazione per altri scenari e endpoint del controller
}