package com.example.lmmovilmantenimiento.ContenedorViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lmmovilmantenimiento.R;

import java.util.List;

public class AdapterListadoSucursales extends RecyclerView.Adapter<AdapterListadoSucursalesViewHolder> {
    private final List<ObjetoListadoSucursales> listadoSucursales;
    private final Context context;

    public AdapterListadoSucursales(List<ObjetoListadoSucursales> listadoSucursales, Context context) {
        this.listadoSucursales = listadoSucursales;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterListadoSucursalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solo_item_sucursal,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new AdapterListadoSucursalesViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListadoSucursalesViewHolder holder, int position) {
        holder.txtNombreListadoSucursal.setText(listadoSucursales.get(position).getNombre_sucursal());
        holder.context = context;
    }

    @Override
    public int getItemCount() {
        return listadoSucursales.size();
    }
}
