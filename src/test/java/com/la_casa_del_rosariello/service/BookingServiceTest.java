package com.la_casa_del_rosariello.service;

import com.la_casa_del_rosariello.dto.BookingRequestDTO;
import com.la_casa_del_rosariello.dto.BookingResponseDTO;
import com.la_casa_del_rosariello.entity.Booking;
import com.la_casa_del_rosariello.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequestDTO bookingRequestDTO;
    private Booking bookingEntity;

    @BeforeEach
    void setUp() {
        bookingRequestDTO = new BookingRequestDTO();
        bookingRequestDTO.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequestDTO.setCheckOutDate(LocalDate.now().plusDays(3));
        bookingRequestDTO.setNumberOfGuests(2);
        bookingRequestDTO.setNome("Mario");
        bookingRequestDTO.setCognome("Rossi");
        bookingRequestDTO.setEmail("mario.rossi@example.com");

        bookingEntity = new Booking();
        bookingEntity.setId(1L);
        // Set other fields of the Booking entity if needed for assertions
        bookingEntity.setCheckInDate(bookingRequestDTO.getCheckInDate());
        bookingEntity.setCheckOutDate(bookingRequestDTO.getCheckOutDate());
        bookingEntity.setNumberOfGuests(bookingRequestDTO.getNumberOfGuests());
        bookingEntity.setNome(bookingRequestDTO.getNome());
        bookingEntity.setCognome(bookingRequestDTO.getCognome());
        bookingEntity.setEmail(bookingRequestDTO.getEmail());
    }

    @Test
    void testCreateBookingSuccess() {
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingEntity);

        BookingResponseDTO result = bookingService.createBooking(bookingRequestDTO);

        assertEquals(bookingEntity.getId(), result.getId());
        assertEquals(bookingEntity.getCheckInDate(), result.getCheckInDate());
        assertEquals(bookingEntity.getCheckOutDate(), result.getCheckOutDate());
        assertEquals(bookingEntity.getNumberOfGuests(), result.getNumberOfGuests());
        assertEquals(bookingEntity.getNome(), result.getNome());
        assertEquals(bookingEntity.getCognome(), result.getCognome());
        assertEquals(bookingEntity.getEmail(), result.getEmail());
    }

    // You can add more test methods here for other scenarios and service methods
}