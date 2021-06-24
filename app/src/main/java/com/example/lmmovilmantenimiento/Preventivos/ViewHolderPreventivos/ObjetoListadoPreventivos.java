package com.example.lmmovilmantenimiento.Preventivos.ViewHolderPreventivos;

public class ObjetoListadoPreventivos {
    String  cantidad_frecuencia
            ,tipo_frecuencia
            ,nombre_responsable
            ,idTarea
            ,area
            ,descripcion
            ,equipo
            ,fechaAlta
            ,fechaProgramacion
            ,fechaTermino
            ,horaProgramacion
            ,nombre_alta
            ,status
            ,sucursal
            ,tipo_actividad
            ,tipo_programacion
            ,usuario_alta;

    public ObjetoListadoPreventivos() {
    }

    public ObjetoListadoPreventivos(String cantidad_frecuencia, String tipo_frecuencia, String nombre_responsable, String idTarea, String area, String descripcion, String equipo, String fechaAlta, String fechaProgramacion, String fechaTermino, String horaProgramacion, String nombre_alta, String status, String sucursal, String tipo_actividad, String tipo_programacion, String usuario_alta) {
        this.cantidad_frecuencia = cantidad_frecuencia;
        this.tipo_frecuencia = tipo_frecuencia;
        this.nombre_responsable = nombre_responsable;
        this.idTarea = idTarea;
        this.area = area;
        this.descripcion = descripcion;
        this.equipo = equipo;
        this.fechaAlta = fechaAlta;
        this.fechaProgramacion = fechaProgramacion;
        this.fechaTermino = fechaTermino;
        this.horaProgramacion = horaProgramacion;
        this.nombre_alta = nombre_alta;
        this.status = status;
        this.sucursal = sucursal;
        this.tipo_actividad = tipo_actividad;
        this.tipo_programacion = tipo_programacion;
        this.usuario_alta = usuario_alta;
    }

    public String getCantidad_frecuencia() {
        return cantidad_frecuencia;
    }

    public void setCantidad_frecuencia(String cantidad_frecuencia) {
        this.cantidad_frecuencia = cantidad_frecuencia;
    }

    public String getTipo_frecuencia() {
        return tipo_frecuencia;
    }

    public void setTipo_frecuencia(String tipo_frecuencia) {
        this.tipo_frecuencia = tipo_frecuencia;
    }

    public String getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(String idTarea) {
        this.idTarea = idTarea;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getFechaProgramacion() {
        return fechaProgramacion;
    }

    public void setFechaProgramacion(String fechaProgramacion) {
        this.fechaProgramacion = fechaProgramacion;
    }

    public String getFechaTermino() {
        return fechaTermino;
    }

    public void setFechaTermino(String fechaTermino) {
        this.fechaTermino = fechaTermino;
    }

    public String getHoraProgramacion() {
        return horaProgramacion;
    }

    public void setHoraProgramacion(String horaProgramacion) {
        this.horaProgramacion = horaProgramacion;
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

    public String getTipo_actividad() {
        return tipo_actividad;
    }

    public void setTipo_actividad(String tipo_actividad) {
        this.tipo_actividad = tipo_actividad;
    }

    public String getTipo_programacion() {
        return tipo_programacion;
    }

    public void setTipo_programacion(String tipo_programacion) {
        this.tipo_programacion = tipo_programacion;
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
}
