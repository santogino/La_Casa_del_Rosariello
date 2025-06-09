package com.la_casa_del_rosariello.service;

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

        return bookingRepository.save(newBooking);
    }

    public boolean verificaDisponibilita(LocalDate dataInizioRichiesta, LocalDate dataFineRichiesta) {

        List<Booking> prenotazioniInConflitto = bookingRepository.findByDataInizioBeforeAndDataFineAfterAndStatoPrenotazione(
                dataFineRichiesta,
                dataInizioRichiesta,
                StatoPrenotazione.CONFERMATA
        );

        return prenotazioniInConflitto.isEmpty();
    }

    public Booking aggiornaPrenotazione(Long id, Booking prenotazioneAggiornata) {
        Booking prenotazioneEsistente = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Prenotazione non trovata con ID: " + id));

        if (prenotazioneAggiornata.getNumeroOspiti() > 2) {
            throw new InvalidGuestNumberException("Il numero massimo di ospiti consentito è 2 !");
        }

        if (!prenotazioneEsistente.getDataInizio().equals(prenotazioneAggiornata.getDataInizio()) ||
                !prenotazioneEsistente.getDataFine().equals(prenotazioneAggiornata.getDataFine())) {

            List<Booking> prenotazioniInConflitto = bookingRepository.findByDataInizioBeforeAndDataFineAfterAndStatoPrenotazione(
                    prenotazioneAggiornata.getDataFine(),
                    prenotazioneAggiornata.getDataInizio(),
                    StatoPrenotazione.CONFERMATA
            );

            if (!prenotazioniInConflitto.isEmpty()) {
                throw new BookingConflictException("Le date aggiornate si sovrappongono a una prenotazione esistente.");
            }
        }
        prenotazioneEsistente.setDataInizio(prenotazioneAggiornata.getDataInizio());
        prenotazioneEsistente.setDataFine(prenotazioneAggiornata.getDataFine());
        prenotazioneEsistente.setOspiteNome(prenotazioneAggiornata.getOspiteNome());
        prenotazioneEsistente.setOspiteCognome(prenotazioneAggiornata.getOspiteCognome());
        prenotazioneEsistente.setOspiteEmail(prenotazioneAggiornata.getOspiteEmail());
        prenotazioneEsistente.setNumeroOspiti(prenotazioneAggiornata.getNumeroOspiti());
        prenotazioneEsistente.setNote(prenotazioneAggiornata.getNote());

        return bookingRepository.save(prenotazioneEsistente);
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
