package com.la_casa_del_rosariello.dto;

public class PrezzoResponseDTO {
    private double prezzoPerNotte;

    public PrezzoResponseDTO() {}

    public PrezzoResponseDTO(double prezzoPerNotte) {
        this.prezzoPerNotte = prezzoPerNotte;
    }

    public double getPrezzoPerNotte() { return prezzoPerNotte; }
    public void setPrezzoPerNotte(double prezzoPerNotte) { this.prezzoPerNotte = prezzoPerNotte; }
}
