package com.la_casa_del_rosariello.dto;

public class DisponibilitaResponseDTO {
    private boolean disponibile;

    public DisponibilitaResponseDTO() {}

    public DisponibilitaResponseDTO(boolean disponibile) {
        this.disponibile = disponibile;
    }

    public boolean isDisponibile() { return disponibile; }
    public void setDisponibile(boolean disponibile) { this.disponibile = disponibile; }
}
