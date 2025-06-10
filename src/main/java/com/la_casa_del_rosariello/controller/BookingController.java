package com.la_casa_del_rosariello.controller;

import com.la_casa_del_rosariello.dto.BookingRequestDTO;
import com.la_casa_del_rosariello.dto.BookingResponseDTO;
import com.la_casa_del_rosariello.dto.DisponibilitaResponseDTO;
import com.la_casa_del_rosariello.dto.PrezzoResponseDTO;
import com.la_casa_del_rosariello.entity.Booking;
import com.la_casa_del_rosariello.exception.BookingConflictException;
import com.la_casa_del_rosariello.exception.BookingNotFoundException;
import com.la_casa_del_rosariello.exception.InvalidGuestNumberException;
import com.la_casa_del_rosariello.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Metodo di utilità per la mappatura DTO (assicurati che sia corretto come discusso)
    private BookingResponseDTO mapToBookingResponseDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setDataInizio(booking.getDataInizio());
        dto.setDataFine(booking.getDataFine());
        dto.setNomeOspite(booking.getOspiteNome());
        dto.setCognomeOspite(booking.getOspiteCognome());
        dto.setEmailOspite(booking.getOspiteEmail());
        dto.setNumeroOspiti(booking.getNumeroOspiti());
        dto.setNote(booking.getNote());
        dto.setDataCreazione(booking.getDataCreazione());

        dto.setStato(booking.getStatoPrenotazione());

        dto.setPrezzoTotale(bookingService.calcolaPrezzoTotale(booking.getDataInizio(), booking.getDataFine()));
        return dto;
    }

    // --- Endpoint per il Prezzo per Notte ---
    @GetMapping("/prezzo")
    public ResponseEntity<PrezzoResponseDTO> getPrezzoPerNotte() {
        double prezzo = bookingService.getPrezzoPerNotte();
        return ResponseEntity.ok(new PrezzoResponseDTO(prezzo));
    }

    // --- Endpoint per la Verifica Disponibilità ---
    @GetMapping("/disponibilita")
    public ResponseEntity<DisponibilitaResponseDTO> verificaDisponibilita(
            @RequestParam("dataInizio") LocalDate dataInizio,
            @RequestParam("dataFine") LocalDate dataFine) {

        // Controllo base sulle date prima di passare al Service
        if (dataFine.isBefore(dataInizio) || dataFine.isEqual(dataInizio)) {

            return ResponseEntity.badRequest().body(new DisponibilitaResponseDTO(false));
        }

        boolean disponibile = bookingService.verificaDisponibilita(dataInizio, dataFine);
        return ResponseEntity.ok(new DisponibilitaResponseDTO(disponibile));
    }

    // --- Endpoint per Creare una Prenotazione ---
    @PostMapping
    public ResponseEntity<BookingResponseDTO> creaPrenotazione(@Valid @RequestBody BookingRequestDTO requestDTO) {
        // Mappatura da DTO a Entità
        Booking nuovaPrenotazione = new Booking();
        nuovaPrenotazione.setDataInizio(requestDTO.getDataInizio());
        nuovaPrenotazione.setDataFine(requestDTO.getDataFine());
        nuovaPrenotazione.setOspiteNome(requestDTO.getNomeOspite());
        nuovaPrenotazione.setOspiteCognome(requestDTO.getCognomeOspite());
        nuovaPrenotazione.setOspiteEmail(requestDTO.getEmailOspite());
        nuovaPrenotazione.setNumeroOspiti(requestDTO.getNumeroOspiti());
        nuovaPrenotazione.setNote(requestDTO.getNote());
        // Lo stato e la data di creazione sono gestiti nell'entità/service

        try {
            Booking prenotazioneSalvata = bookingService.createdBooking(nuovaPrenotazione);

            // Mappatura da Entità a Response DTO
            BookingResponseDTO responseDTO = mapToBookingResponseDTO(prenotazioneSalvata);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED); // 201 Created
        } catch (InvalidGuestNumberException | BookingConflictException | IllegalArgumentException e) {
            // Questi errori verranno catturati dal @ControllerAdvice per ritornare un 400 o 409
            throw e;
        }
    }

    // --- Endpoint per Visualizzare i Dettagli di una Singola Prenotazione ---
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getPrenotazioneById(@PathVariable Long id) {
        Booking booking = bookingService.findBookingById(id)
                .orElseThrow(() -> new BookingNotFoundException("Prenotazione non trovata con ID: " + id));

        BookingResponseDTO responseDTO = mapToBookingResponseDTO(booking);
        return ResponseEntity.ok(responseDTO); // 200 OK
    }

    // --- Endpoint per Aggiornare una Prenotazione Esistente ---
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> aggiornaPrenotazione(
            @PathVariable Long id,
            @Valid @RequestBody BookingRequestDTO requestDTO) { // Qui ricevi il DTO corretto

        try {
            // Passa DIRETTAMENTE il 'requestDTO' al service.
            Booking bookingAggiornata = bookingService.aggiornaPrenotazione(id, requestDTO);

            BookingResponseDTO responseDTO = mapToBookingResponseDTO(bookingAggiornata);
            return ResponseEntity.ok(responseDTO); // 200 OK
        } catch (BookingNotFoundException | InvalidGuestNumberException | BookingConflictException |
                 IllegalArgumentException e) {
            // Qui stai ri-lanciando l'eccezione, il che è corretto se hai un @ControllerAdvice
            // per gestirle e mappare a risposte HTTP appropriate (es. 404, 400, 409).
            throw e;
        }
    }

    // --- Endpoint per Cancellare una Prenotazione (cambia stato) ---
    @PatchMapping("/{id}/cancella") // PATCH è appropriato per un aggiornamento parziale/cambio di stato
    public ResponseEntity<BookingResponseDTO> cancellaPrenotazione(@PathVariable Long id) {
        try {
            Booking bookingCancellata = bookingService.cancellaPrenotazione(id);
            BookingResponseDTO responseDTO = mapToBookingResponseDTO(bookingCancellata);
            return ResponseEntity.ok(responseDTO); // 200 OK
        } catch (BookingNotFoundException e) {
            throw e;
        }
    }

    // --- Endpoint (Admin) per visualizzare tutte le prenotazioni ---
    // Potrebbe richiedere Spring Security per essere protetto
    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllPrenotazioni() {
        List<Booking> bookings = bookingService.findAllBookings();
        List<BookingResponseDTO> responseDTOs = bookings.stream()
                .map(this::mapToBookingResponseDTO) // Usa il metodo di mapping
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }
}
