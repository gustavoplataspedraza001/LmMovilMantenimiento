package com.example.lmmovilmantenimiento.Modelos;

public class ModeloAlataPreventivo {
    private String tipo_frecuencia,cantidad_frecuencia,usuario_responsable,nombre_responsable,tipo_actividad,tipo_programacion,equipo, status, sucursal, usuario_alta, nombre_alta, descripcion, fechaAlta, fechaTermino, area, fechaProgramacion, horaProgramacion;

    public ModeloAlataPreventivo() {
    }

    public ModeloAlataPreventivo(String tipo_frecuencia, String cantidad_frecuencia, String usuario_responsable, String nombre_responsable, String tipo_actividad, String tipo_programacion, String equipo, String status, String sucursal, String usuario_alta, String nombre_alta, String descripcion, String fechaAlta, String fechaTermino, String area, String fechaProgramacion, String horaProgramacion) {
        this.tipo_frecuencia = tipo_frecuencia;
        this.cantidad_frecuencia = cantidad_frecuencia;
        this.usuario_responsable = usuario_responsable;
        this.nombre_responsable = nombre_responsable;
        this.tipo_actividad = tipo_actividad;
        this.tipo_programacion = tipo_programacion;
        this.equipo = equipo;
        this.status = status;
        this.sucursal = sucursal;
        this.usuario_alta = usuario_alta;
        this.nombre_alta = nombre_alta;
        this.descripcion = descripcion;
        this.fechaAlta = fechaAlta;
        this.fechaTermino = fechaTermino;
        this.area = area;
        this.fechaProgramacion = fechaProgramacion;
        this.horaProgramacion = horaProgramacion;
    }

    public String getTipo_frecuencia() {
        return tipo_frecuencia;
    }

    public void setTipo_frecuencia(String tipo_frecuencia) {
        this.tipo_frecuencia = tipo_frecuencia;
    }

    public String getCantidad_frecuencia() {
        return cantidad_frecuencia;
    }

    public void setCantidad_frecuencia(String cantidad_frecuencia) {
        this.cantidad_frecuencia = cantidad_frecuencia;
    }

    public String getTipo_programacion() {
        return tipo_programacion;
    }

    public void setTipo_programacion(String tipo_programacion) {
        this.tipo_programacion = tipo_programacion;
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

    public String getFechaProgramacion() {
        return fechaProgramacion;
    }

    public void setFechaProgramacion(String fechaProgramacion) {
        this.fechaProgramacion = fechaProgramacion;
    }

    public String getHoraProgramacion() {
        return horaProgramacion;
    }

    public void setHoraProgramacion(String horaProgramacion) {
        this.horaProgramacion = horaProgramacion;
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
}
