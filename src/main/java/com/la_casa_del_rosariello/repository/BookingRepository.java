package com.la_casa_del_rosariello.repository;

import com.la_casa_del_rosariello.entity.Booking;
import com.la_casa_del_rosariello.entity.StatoPrenotazione;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.dataInizio < :dataFine AND b.dataFine > :dataInizio AND b.statoPrenotazione IN :stati")
    List<Booking> findOverlappingBookings(LocalDate dataInizio, LocalDate dataFine, List<StatoPrenotazione> stati);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.dataInizio < :dataFine AND b.dataFine > :dataInizio " +
            "AND b.statoPrenotazione IN :stati " + // Controlla una lista di stati
            "AND b.id <> :excludedBookingId")      // Esclude la prenotazione corrente
    List<Booking> findOverlappingBookingsExcludingId(
            LocalDate dataFine,
            LocalDate dataInizio,
            List<StatoPrenotazione> stati,        // Accetta una lista di stati
            Long excludedBookingId
    );

    List<Booking> findByOspiteEmail(String ospiteEmail);
    List<Booking> findByStatoPrenotazione(StatoPrenotazione statoPrenotazione);
    // Per un ospite che vede la propria prenotazione

    Optional<Booking> findByIdAndOspiteEmail(Long id, String ospiteEmail);

    Page<Booking> findAll(Pageable pageable);
}
