package com.la_casa_del_rosariello.repository;

import com.la_casa_del_rosariello.entity.Booking;
import com.la_casa_del_rosariello.entity.StatoPrenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // * Trova tutte le prenotazioni confermate che si sovrappongono a un dato intervallo di date.
    //     * La logica di sovrapposizione è:
    //     * (dataInizioRichiesta <= dataFinePrenotazioneEsistente) AND (dataFineRichiesta >= dataInizioPrenotazioneEsistente)
    @Query("SELECT b FROM Booking b WHERE " +
            "b.statoPrenotazione = :statoPrenotazioneConfermata AND " +
            "b.dataInizio <= :dataFineRicerca AND " +
            "b.dataFine >= :dataInizioRicerca")
    List<Booking> findOverlappingBookings(
            @Param("dataFineRicerca") LocalDate dataFineRicerca,
            @Param("dataInizioRicerca") LocalDate dataInizioRicerca,
            @Param("statoPrenotazioneConfermata") StatoPrenotazione statoPrenotazioneConfermata
    );

    @Query("SELECT b FROM Booking b WHERE " +
            "b.id <> :excludedBookingId AND " + // Condizione per escludere la prenotazione
            "b.statoPrenotazione = :statoPrenotazioneConfermata AND " +
            "b.dataInizio <= :dataFineRicerca AND " +
            "b.dataFine >= :dataInizioRicerca")
    List<Booking> findOverlappingBookingsExcludingId(
            @Param("dataFineRicerca") LocalDate dataFineRicerca,
            @Param("dataInizioRicerca") LocalDate dataInizioRicerca,
            @Param("statoPrenotazioneConfermata") StatoPrenotazione statoPrenotazioneConfermata,
            @Param("excludedBookingId") Long excludedBookingId
    );

    List<Booking> findByOspiteEmail(String ospiteEmail);
    List<Booking> findByStatoPrenotazione(StatoPrenotazione statoPrenotazione);
    // Per un ospite che vede la propria prenotazione
    Optional<Booking> findByIdAndOspiteEmail(Long id, String ospiteEmail);
}
