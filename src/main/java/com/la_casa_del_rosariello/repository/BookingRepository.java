package com.la_casa_del_rosariello.repository;

import com.la_casa_del_rosariello.entity.Booking;
import com.la_casa_del_rosariello.entity.StatoPrenotazione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //Quando un'entità Booking viene salvata per la prima volta nel database, spesso è utile registrarne il momento esatto della creazione. Questo è il ruolo del campo createdAt (LocalDateTime).
    List<Booking> findByDataInizioBeforeAndDataFineAfterAndStatoPrenotazione(
            LocalDate dataFineRichiesta,
            LocalDate dataInizioRichiesta,
            StatoPrenotazione statoConfermato
    );

    List<Booking> findByOspiteEmail(String ospiteEmail);
    List<Booking> findByStatoPrenotazione(StatoPrenotazione statoPrenotazione);
    // Per un ospite che vede la propria prenotazione
    Optional<Booking> findByIdAndOspiteEmail(Long id, String ospiteEmail);
}
