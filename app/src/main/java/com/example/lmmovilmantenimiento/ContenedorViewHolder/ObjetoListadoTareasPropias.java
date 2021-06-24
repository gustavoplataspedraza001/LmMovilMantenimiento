package com.example.lmmovilmantenimiento.ContenedorViewHolder;

public class ObjetoListadoTareasPropias {
    private String sucursal,idTarea,descripcion,usuario_responsable,evidenciaAudio,evidenciaVideo,evidenciaImagen,fechaAlta,status;

    public ObjetoListadoTareasPropias() {
    }

    public ObjetoListadoTareasPropias(String sucursal, String idTarea, String descripcion, String usuario_responsable, String evidenciaAudio, String evidenciaVideo, String evidenciaImagen, String fechaAlta, String status) {
        this.sucursal = sucursal;
        this.idTarea = idTarea;
        this.descripcion = descripcion;
        this.usuario_responsable = usuario_responsable;
        this.evidenciaAudio = evidenciaAudio;
        this.evidenciaVideo = evidenciaVideo;
        this.evidenciaImagen = evidenciaImagen;
        this.fechaAlta = fechaAlta;
        this.status = status;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getUsuario_responsable() {
        return usuario_responsable;
    }

    public void setUsuario_responsable(String usuario_responsable) {
        this.usuario_responsable = usuario_responsable;
    }

    public String getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(String idTarea) {
        this.idTarea = idTarea;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEvidenciaAudio() {
        return evidenciaAudio;
    }

    public void setEvidenciaAudio(String evidenciaAudio) {
        this.evidenciaAudio = evidenciaAudio;
    }

    public String getEvidenciaVideo() {
        return evidenciaVideo;
    }

    public void setEvidenciaVideo(String evidenciaVideo) {
        this.evidenciaVideo = evidenciaVideo;
    }

    public String getEvidenciaImagen() {
        return evidenciaImagen;
    }

    public void setEvidenciaImagen(String evidenciaImagen) {
        this.evidenciaImagen = evidenciaImagen;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
