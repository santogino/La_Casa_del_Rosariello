package com.la_casa_del_rosariello.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class BookingRequestDTO {
    @NotNull(message = "La data di inizio non può essere nulla.")
    @FutureOrPresent(message = "La data di inizio deve essere nel presente o nel futuro.")
    private LocalDate dataInizio;

    @NotNull(message = "La data di fine non può essere nulla.")
    @Future(message = "La data di fine deve essere nel futuro.")
    private LocalDate dataFine;

    @NotBlank(message = "Il nome dell'ospite non può essere vuoto.")
    private String nomeOspite;

    @NotBlank(message = "Il cognome dell'ospite non può essere vuoto.")
    private String cognomeOspite;

    @NotBlank(message = "L'email dell'ospite non può essere vuota.")
    @Email(message = "Formato email non valido.")
    private String emailOspite;

    @Min(value = 1, message = "Il numero di ospiti deve essere almeno 1.")
    @Max(value = 2, message = "Il numero massimo di ospiti consentito è 2.")
    private int numeroOspiti;

    private String note; // Campo opzionale

    // Costruttore senza argomenti
    public BookingRequestDTO() {}

    // Costruttore con tutti gli argomenti (opzionale, ma utile per test)
    public BookingRequestDTO(LocalDate dataInizio, LocalDate dataFine, String nomeOspite, String cognomeOspite, String emailOspite, int numeroOspiti, String note) {
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.nomeOspite = nomeOspite;
        this.cognomeOspite = cognomeOspite;
        this.emailOspite = emailOspite;
        this.numeroOspiti = numeroOspiti;
        this.note = note;
    }

    // Getter e Setter
    public LocalDate getDataInizio() { return dataInizio; }
    public void setDataInizio(LocalDate dataInizio) { this.dataInizio = dataInizio; }

    public LocalDate getDataFine() { return dataFine; }
    public void setDataFine(LocalDate dataFine) { this.dataFine = dataFine; }

    public String getNomeOspite() { return nomeOspite; }
    public void setNomeOspite(String nomeOspite) { this.nomeOspite = nomeOspite; }

    public String getCognomeOspite() { return cognomeOspite; }
    public void setCognomeOspite(String cognomeOspite) { this.cognomeOspite = cognomeOspite; }

    public String getEmailOspite() { return emailOspite; }
    public void setEmailOspite(String emailOspite) { this.emailOspite = emailOspite; }

    public int getNumeroOspiti() { return numeroOspiti; }
    public void setNumeroOspiti(int numeroOspiti) { this.numeroOspiti = numeroOspiti; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
