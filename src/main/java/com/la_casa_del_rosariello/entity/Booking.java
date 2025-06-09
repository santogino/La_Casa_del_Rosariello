package com.la_casa_del_rosariello.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @FutureOrPresent
    private LocalDate dataInizio;

    @NotNull
    @Future
    private LocalDate dataFine;

    @NotBlank
    private String ospiteNome;

    @NotBlank
    private String ospiteCognome;

    @NotBlank
    @Email
    private String ospiteEmail;

    @Min(value = 1)
    @Max(value = 2)
    private int numeroOspiti;

    @NotNull
    @Enumerated
    private StatoPrenotazione statoPrenotazione = StatoPrenotazione.PENDENTE;

    private LocalDateTime dataCreazione;
    private String note;

    public Booking() {}

    public Booking(Long id, LocalDate dataInizio, LocalDate dataFine, String ospiteNome, String ospiteCognome, String ospiteEmail, int numeroOspiti, StatoPrenotazione statoPrenotazione, LocalDateTime dataCreazione) {
        this.id = id;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.ospiteNome = ospiteNome;
        this.ospiteCognome = ospiteCognome;
        this.ospiteEmail = ospiteEmail;
        this.numeroOspiti = numeroOspiti;
        this.statoPrenotazione = statoPrenotazione;
        this.dataCreazione = dataCreazione;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public String getOspiteNome() {
        return ospiteNome;
    }

    public void setOspiteNome(String ospiteNome) {
        this.ospiteNome = ospiteNome;
    }

    public String getOspiteCognome() {
        return ospiteCognome;
    }

    public void setOspiteCognome(String ospiteCognome) {
        this.ospiteCognome = ospiteCognome;
    }

    public String getOspiteEmail() {
        return ospiteEmail;
    }

    public void setOspiteEmail(String ospiteEmail) {
        this.ospiteEmail = ospiteEmail;
    }

    public int getNumeroOspiti() {
        return numeroOspiti;
    }

    public void setNumeroOspiti(int numeroOspiti) {
        this.numeroOspiti = numeroOspiti;
    }

    public StatoPrenotazione getStatoPrenotazione() {
        return statoPrenotazione;
    }

    public void setStatoPrenotazione(StatoPrenotazione statoPrenotazione) {
        this.statoPrenotazione = statoPrenotazione;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    //Quando un'entità Booking viene salvata per la prima volta nel database, spesso è utile registrarne il momento esatto della creazione. Questo è il ruolo del campo createdAt (LocalDateTime).
    @PrePersist
    protected void onCreate() {
        dataCreazione = LocalDateTime.now();
    }
}
