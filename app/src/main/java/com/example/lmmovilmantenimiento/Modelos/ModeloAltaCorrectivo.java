package com.example.lmmovilmantenimiento.Modelos;

public class ModeloAltaCorrectivo {
    private String tipo_actividad,equipo,usuario_responsable,nombre_responsable,status,sucursal, usuario_alta,nombre_alta, nombreTarea, evidenciaImagen, evidenciaVideo, evidenciaAudio, descripcion, fechaAlta, fechaTermino,
    tiempoRealizacion,area;

    public ModeloAltaCorrectivo() {
    }

    public ModeloAltaCorrectivo(String tipo_actividad, String equipo, String usuario_responsable, String nombre_responsable, String status, String sucursal, String usuario_alta, String nombre_alta, String nombreTarea, String evidenciaImagen, String evidenciaVideo, String evidenciaAudio, String descripcion, String fechaAlta, String fechaTermino, String tiempoRealizacion, String area) {
        this.tipo_actividad = tipo_actividad;
        this.equipo = equipo;
        this.usuario_responsable = usuario_responsable;
        this.nombre_responsable = nombre_responsable;
        this.status = status;
        this.sucursal = sucursal;
        this.usuario_alta = usuario_alta;
        this.nombre_alta = nombre_alta;
        this.nombreTarea = nombreTarea;
        this.evidenciaImagen = evidenciaImagen;
        this.evidenciaVideo = evidenciaVideo;
        this.evidenciaAudio = evidenciaAudio;
        this.descripcion = descripcion;
        this.fechaAlta = fechaAlta;
        this.fechaTermino = fechaTermino;
        this.tiempoRealizacion = tiempoRealizacion;
        this.area = area;
    }

    public String getTiempoRealizacion() {
        return tiempoRealizacion;
    }

    public void setTiempoRealizacion(String tiempoRealizacion) {
        this.tiempoRealizacion = tiempoRealizacion;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public String getTipo_actividad() {
        return tipo_actividad;
    }

    public void setTipo_actividad(String tipo_actividad) {
        this.tipo_actividad = tipo_actividad;
    }

    public String getUsuario_responsable() {
        return usuario_responsable;
    }

    public void setUsuario_responsable(String usuario_responsable) {
        this.usuario_responsable = usuario_responsable;
    }

    public String getNombre_responsable() {
        return nombre_responsable;
    }

    public void setNombre_responsable(String nombre_responsable) {
        this.nombre_responsable = nombre_responsable;
    }

    public String getUsuario_alta() {
        return usuario_alta;
    }

    public void setUsuario_alta(String usuario_alta) {
        this.usuario_alta = usuario_alta;
    }

    public String getNombre_alta() {
        return nombre_alta;
    }

    public void setNombre_alta(String nombre_alta) {
        this.nombre_alta = nombre_alta;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getNombreTarea() {
        return nombreTarea;
    }

    public void setNombreTarea(String nombreTarea) {
        this.nombreTarea = nombreTarea;
    }

    public String getEvidenciaImagen() {
        return evidenciaImagen;
    }

    public void setEvidenciaImagen(String evidenciaImagen) {
        this.evidenciaImagen = evidenciaImagen;
    }

    public String getEvidenciaVideo() {
        return evidenciaVideo;
    }

    public void setEvidenciaVideo(String evidenciaVideo) {
        this.evidenciaVideo = evidenciaVideo;
    }

    public String getEvidenciaAudio() {
        return evidenciaAudio;
    }

    public void setEvidenciaAudio(String evidenciaAudio) {
        this.evidenciaAudio = evidenciaAudio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getFechaTermino() {
        return fechaTermino;
    }

    public void setFechaTermino(String fechaTermino) {
        this.fechaTermino = fechaTermino;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
