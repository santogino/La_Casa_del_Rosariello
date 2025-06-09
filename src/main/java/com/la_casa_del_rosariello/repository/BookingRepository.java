package com.la_casa_del_rosariello.repository;

import com.la_casa_del_rosariello.entity.Booking;
import com.la_casa_del_rosariello.entity.StatoPrenotazione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //Quando un'entità Booking viene salvata per la prima volta nel database, spesso è utile registrarne il momento esatto della creazione. Questo è il ruolo del campo createdAt (LocalDateTime).
    List<Booking> findByStartDateBeforeAndEndDateAfterAndStatus(
            LocalDate newEndDate,
            LocalDate newStartDate,
            StatoPrenotazione confirmedStatus
    );

    List<Booking> findByGuestEmail(String guestEmail);
    List<Booking> findByStatus(StatoPrenotazione status);
    // Per un ospite che vede la propria prenotazione
    Optional<Booking> findByIdAndGuestEmail(Long id, String guestEmail);
}
