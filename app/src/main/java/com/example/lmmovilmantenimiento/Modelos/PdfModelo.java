package com.example.lmmovilmantenimiento.Modelos;

public class PdfModelo {
    String url,responsable,fechaLibaración,tipoActividad;

    public PdfModelo() {
    }

    public PdfModelo(String url, String responsable, String fechaLibaración, String tipoActividad) {
        this.url = url;
        this.responsable = responsable;
        this.fechaLibaración = fechaLibaración;
        this.tipoActividad = tipoActividad;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getFechaLibaración() {
        return fechaLibaración;
    }

    public void setFechaLibaración(String fechaLibaración) {
        this.fechaLibaración = fechaLibaración;
    }

    public String getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(String tipoActividad) {
        this.tipoActividad = tipoActividad;
    }
}
