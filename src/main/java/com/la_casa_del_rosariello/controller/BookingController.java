package com.la_casa_del_rosariello.controller;

import com.la_casa_del_rosariello.dto.BookingRequestDTO;
import com.la_casa_del_rosariello.dto.BookingResponseDTO;
import com.la_casa_del_rosariello.dto.DisponibilitaResponseDTO;
import com.la_casa_del_rosariello.dto.PrezzoResponseDTO;
import com.la_casa_del_rosariello.entity.Booking;
import com.la_casa_del_rosariello.exception.BookingConflictException;
import com.la_casa_del_rosariello.exception.BookingNotFoundException;
import com.la_casa_del_rosariello.exception.InvalidGuestNumberException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.la_casa_del_rosariello.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Bookings", description = "API per la gestione delle prenotazioni")
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

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
    @Operation(summary = "Recupera il prezzo per notte",
               description = "Restituisce il prezzo attuale per notte per una prenotazione.")
    @GetMapping("/prezzo")
    public ResponseEntity<PrezzoResponseDTO> getPrezzoPerNotte() {
        double prezzo = bookingService.getPrezzoPerNotte();
        return ResponseEntity.ok(new PrezzoResponseDTO(prezzo));
    }

    // --- Endpoint per la Verifica Disponibilità ---
    @Operation(summary = "Verifica la disponibilità per un periodo",
               description = "Controlla se la struttura è disponibile tra le date specificate.")
    @Parameter(description = "Data di inizio del periodo (formato YYYY-MM-DD)", required = true)
    @Parameter(description = "Data di fine del periodo (formato YYYY-MM-DD)", required = true)
    @ApiResponse(responseCode = "200", description = "Risposta di disponibilità", content = @Content(schema = @Schema(implementation = DisponibilitaResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Richiesta non valida (es. date non corrette)")
    @GetMapping("/disponibilita")
    public ResponseEntity<DisponibilitaResponseDTO> verificaDisponibilita(
            @RequestParam("dataInizio") @Parameter(description = "Data di inizio della prenotazione") LocalDate dataInizio,
            @RequestParam("dataFine") LocalDate dataFine) {

        // Controllo base sulle date prima di passare al Service
        if (dataFine.isBefore(dataInizio) || dataFine.isEqual(dataInizio)) {

            return ResponseEntity.badRequest().body(new DisponibilitaResponseDTO(false));
        }

        boolean disponibile = bookingService.verificaDisponibilita(dataInizio, dataFine);
        return ResponseEntity.ok(new DisponibilitaResponseDTO(disponibile));
    }

    // --- Endpoint per Creare una Prenotazione ---
    @Operation(summary = "Crea una nuova prenotazione",
               description = "Permette di creare una nuova prenotazione nel sistema.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dati della prenotazione da creare", required = true,
                                                          content = @Content(schema = @Schema(implementation = BookingRequestDTO.class)))
    @ApiResponse(responseCode = "201", description = "Prenotazione creata con successo", content = @Content(schema = @Schema(implementation = BookingResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Richiesta non valida (es. errori di validazione, numero ospiti non valido)")
    @ApiResponse(responseCode = "409", description = "Conflitto (date già prenotate)")
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
    @Operation(summary = "Recupera i dettagli di una singola prenotazione",
               description = "Restituisce i dettagli completi di una prenotazione dato il suo ID.")
    @Parameter(description = "ID della prenotazione da recuperare", required = true)
    @ApiResponse(responseCode = "200", description = "Dettagli prenotazione recuperati con successo", content = @Content(schema = @Schema(implementation = BookingResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Prenotazione non trovata")
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getPrenotazioneById(@PathVariable Long id) {
        Booking booking = bookingService.findBookingById(id)
                .orElseThrow(() -> new BookingNotFoundException("Prenotazione non trovata con ID: " + id));

        BookingResponseDTO responseDTO = mapToBookingResponseDTO(booking);
        return ResponseEntity.ok(responseDTO); // 200 OK
    }

    // --- Endpoint per Aggiornare una Prenotazione Esistente ---
    @Operation(summary = "Aggiorna una prenotazione esistente",
               description = "Permette di aggiornare i dettagli di una prenotazione dato il suo ID.")
    @Parameter(description = "ID della prenotazione da aggiornare", required = true)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuovi dati della prenotazione", required = true, content = @Content(schema = @Schema(implementation = BookingRequestDTO.class)))
    @ApiResponse(responseCode = "200", description = "Prenotazione aggiornata con successo", content = @Content(schema = @Schema(implementation = BookingResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Richiesta non valida (es. errori di validazione, numero ospiti non valido)")
    @ApiResponse(responseCode = "404", description = "Prenotazione non trovata")
    @ApiResponse(responseCode = "409", description = "Conflitto (date già prenotate dopo l'aggiornamento)")
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
    @Operation(summary = "Cancella una prenotazione (cambia stato)",
               description = "Imposta lo stato di una prenotazione a 'CANCELLATA' dato il suo ID.")
    @Parameter(description = "ID della prenotazione da cancellare", required = true)
    @ApiResponse(responseCode = "200", description = "Prenotazione cancellata con successo", content = @Content(schema = @Schema(implementation = BookingResponseDTO.class)))
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
    @Operation(summary = "Recupera tutte le prenotazioni con paginazione",
               description = "Restituisce una lista paginata di tutte le prenotazioni nel sistema. Richiede autenticazione (tipicamente per ruoli admin).")
    @Parameter(name = "pageable", hidden = true) // Nasconde il parametro Pageable generato automaticamente per una migliore visualizzazione
    @ApiResponse(responseCode = "200", description = "Lista di prenotazioni recuperata con successo",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @GetMapping
    // @PreAuthorize("hasRole('ADMIN')") // Esempio di protezione con Spring Security (richiede configurazione aggiuntiva)
    public ResponseEntity<Page<BookingResponseDTO>> getAllPrenotazioni(Pageable pageable) {
        Page<Booking> bookingsPage = bookingService.findAllBookings(pageable);
        Page<BookingResponseDTO> responseDTOsPage = bookingsPage.map(this::mapToBookingResponseDTO);
        return ResponseEntity.ok(responseDTOsPage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Operation(summary = "Gestore degli errori di validazione della richiesta", hidden = true) // Nasconde dall'UI principale
    @ApiResponse(responseCode = "400", description = "Errori di validazione dei campi", content = @Content(schema = @Schema(implementation = Map.class)))
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
