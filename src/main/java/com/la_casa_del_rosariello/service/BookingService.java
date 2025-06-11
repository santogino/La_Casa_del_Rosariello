package com.la_casa_del_rosariello.service;

import com.la_casa_del_rosariello.dto.BookingRequestDTO;
import com.la_casa_del_rosariello.entity.Booking;
import com.la_casa_del_rosariello.entity.StatoPrenotazione;
import com.la_casa_del_rosariello.exception.BookingConflictException;
import com.la_casa_del_rosariello.exception.BookingNotFoundException;
import com.la_casa_del_rosariello.exception.InvalidGuestNumberException;
import com.la_casa_del_rosariello.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    private final double PREZZO_PER_NOTTE = 60;

    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> findBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking createdBooking(Booking newBooking) {
        if (newBooking.getNumeroOspiti() > 2) {
            throw new InvalidGuestNumberException("Il numero massimo di ospiti è 2!");
        }

        if (newBooking.getDataFine().isBefore(newBooking.getDataInizio()) || newBooking.getDataFine().isEqual(newBooking.getDataInizio())) {
            throw new IllegalArgumentException("La data di fine vacanza deve essere successiva alla data di inizio!");
        }

        if (!verificaDisponibilita(newBooking.getDataInizio(), newBooking.getDataFine())) {
            throw new BookingConflictException("Le date selezionate non sono disponibili !");
        }
        newBooking.setStatoPrenotazione(StatoPrenotazione.CONFERMATA);

        return bookingRepository.save(newBooking);
    }

    public boolean verificaDisponibilita(LocalDate dataInizioRichiesta, LocalDate dataFineRichiesta) {
        // Definisci gli stati che bloccano la disponibilità
        List<StatoPrenotazione> statiInConflitto = Arrays.asList(StatoPrenotazione.CONFERMATA, StatoPrenotazione.PENDENTE);

        List<Booking> prenotazioniInConflittoInterne = bookingRepository.findOverlappingBookings(
                dataInizioRichiesta,
                dataFineRichiesta,
                statiInConflitto // Passa la lista di stati
        );

        // La logica ora è corretta: se la lista non è vuota, non è disponibile
        return prenotazioniInConflittoInterne.isEmpty();
    }


    public Booking aggiornaPrenotazione(Long id, BookingRequestDTO newDatiPrenotazione) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Prenotazione con ID " + id + " non trovata."));

        // Preleva i vecchi dati per confrontare
        LocalDate oldDataInizio = existingBooking.getDataInizio();
        LocalDate oldDataFine = existingBooking.getDataFine();
        StatoPrenotazione oldStato = existingBooking.getStatoPrenotazione();

        // Controlla se le date o lo stato (se CONFERMATA/PENDENTE) sono cambiati in modo da richiedere una verifica
        boolean datesChanged = !oldDataInizio.isEqual(newDatiPrenotazione.getDataInizio()) ||
                !oldDataFine.isEqual(newDatiPrenotazione.getDataFine());

        boolean statusChangingToConfirmedOrPending =
                (newDatiPrenotazione.getStatoPrenotazione() == StatoPrenotazione.CONFERMATA && oldStato != StatoPrenotazione.CONFERMATA) ||
                        (newDatiPrenotazione.getStatoPrenotazione() == StatoPrenotazione.PENDENTE && oldStato != StatoPrenotazione.PENDENTE);

        // Se le date sono cambiate O lo stato sta passando a CONFERMATA/PENDENTE (necessita di bloccare date)
        if (datesChanged || statusChangingToConfirmedOrPending) {
            // Esegui la verifica della disponibilità escludendo la prenotazione attuale
            if (!verificaDisponibilitaEscludendo(newDatiPrenotazione.getDataInizio(), newDatiPrenotazione.getDataFine(), id)) {
                throw new BookingConflictException("Le date " + newDatiPrenotazione.getDataInizio() + " - " + newDatiPrenotazione.getDataFine() + " sono già occupate da un'altra prenotazione.");
            }
        }

        // Validazione del numero di ospiti (se cambiasse e fosse fuori range)
        if (newDatiPrenotazione.getNumeroOspiti() < 1 || newDatiPrenotazione.getNumeroOspiti() > 2) {
            throw new InvalidGuestNumberException("Il numero di ospiti deve essere compreso tra 1 e 2.");
        }

        // Aggiorna i campi della prenotazione esistente
        existingBooking.setDataInizio(newDatiPrenotazione.getDataInizio());
        existingBooking.setDataFine(newDatiPrenotazione.getDataFine());
        existingBooking.setOspiteNome(newDatiPrenotazione.getNomeOspite());
        existingBooking.setOspiteCognome(newDatiPrenotazione.getCognomeOspite());
        existingBooking.setOspiteEmail(newDatiPrenotazione.getEmailOspite());
        existingBooking.setNumeroOspiti(newDatiPrenotazione.getNumeroOspiti());
        existingBooking.setStatoPrenotazione(newDatiPrenotazione.getStatoPrenotazione());
        existingBooking.setNote(newDatiPrenotazione.getNote());

        // Non aggiornare dataCreazione, è gestito da @PrePersist

        return bookingRepository.save(existingBooking);
    }

    private boolean verificaDisponibilitaEscludendo(LocalDate dataInizioRichiesta, LocalDate dataFineRichiesta, Long excludedBookingId) {
        // Riutilizza la stessa logica degli stati
        List<StatoPrenotazione> statiInConflitto = Arrays.asList(StatoPrenotazione.CONFERMATA, StatoPrenotazione.PENDENTE);

        List<Booking> prenotazioniInConflittoInterne = bookingRepository.findOverlappingBookingsExcludingId(
                dataInizioRichiesta,
                dataFineRichiesta,
                statiInConflitto, // Passa la lista di stati
                excludedBookingId
        );
        return prenotazioniInConflittoInterne.isEmpty();
    }

    public Booking cancellaPrenotazione(Long id) {
        Booking prenotazioneDaCancellare = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Prenotazione non trovata con ID: " + id));

        prenotazioneDaCancellare.setStatoPrenotazione(StatoPrenotazione.CANCELLATA);
        return bookingRepository.save(prenotazioneDaCancellare);
    }

    public double getPrezzoPerNotte() {
        return PREZZO_PER_NOTTE;
    }

    public double calcolaPrezzoTotale(LocalDate dataInizio, LocalDate dataFine) {
        if (dataFine.isBefore(dataInizio) || dataFine.isEqual(dataInizio)) {
            throw new IllegalArgumentException("La data di fine deve essere successiva alla data di inizio per calcolare il prezzo.");
        }
        long numeroNotti = ChronoUnit.DAYS.between(dataInizio, dataFine);
        return numeroNotti * PREZZO_PER_NOTTE;
    }
}
