package com.la_casa_del_rosariello.dto;

import com.la_casa_del_rosariello.entity.StatoPrenotazione;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingResponseDTO {
    private Long id;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private String nomeOspite;
    private String cognomeOspite;
    private String emailOspite;
    private int numeroOspiti;
    private StatoPrenotazione stato; // Enum per lo stato
    private LocalDateTime dataCreazione;
    private String note;
    private double prezzoTotale; // Aggiungiamo il prezzo totale calcolato

    // Costruttore senza argomenti
    public BookingResponseDTO() {}

    // Costruttore con tutti gli argomenti (utile per la mappatura)
    public BookingResponseDTO(Long id, LocalDate dataInizio, LocalDate dataFine, String nomeOspite, String cognomeOspite, String emailOspite, int numeroOspiti, StatoPrenotazione stato, LocalDateTime dataCreazione, String note, double prezzoTotale) {
        this.id = id;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.nomeOspite = nomeOspite;
        this.cognomeOspite = cognomeOspite;
        this.emailOspite = emailOspite;
        this.numeroOspiti = numeroOspiti;
        this.stato = stato;
        this.dataCreazione = dataCreazione;
        this.note = note;
        this.prezzoTotale = prezzoTotale;
    }

    // Getter e Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public StatoPrenotazione getStato() { return stato; }
    public void setStato(StatoPrenotazione stato) { this.stato = stato; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public double getPrezzoTotale() { return prezzoTotale; }
    public void setPrezzoTotale(double prezzoTotale) { this.prezzoTotale = prezzoTotale; }
}
