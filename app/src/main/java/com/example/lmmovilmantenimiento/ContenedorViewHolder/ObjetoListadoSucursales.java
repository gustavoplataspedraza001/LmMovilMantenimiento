package com.example.lmmovilmantenimiento.ContenedorViewHolder;

public class ObjetoListadoSucursales {
    private String nombre_sucursal;

    public ObjetoListadoSucursales() {
    }

    public ObjetoListadoSucursales(String nombre_sucursal) {
        this.nombre_sucursal = nombre_sucursal;
    }

    public String getNombre_sucursal() {
        return nombre_sucursal;
    }

    public void setNombre_sucursal(String nombre_sucursal) {
        this.nombre_sucursal = nombre_sucursal;
    }
}
