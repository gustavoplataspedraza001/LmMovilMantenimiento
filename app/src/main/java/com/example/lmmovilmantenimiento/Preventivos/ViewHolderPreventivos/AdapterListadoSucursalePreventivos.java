package com.example.lmmovilmantenimiento.Preventivos.ViewHolderPreventivos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lmmovilmantenimiento.ContenedorViewHolder.ObjetoListadoSucursales;
import com.example.lmmovilmantenimiento.R;

import java.util.List;

public class AdapterListadoSucursalePreventivos extends RecyclerView.Adapter<AdapterListadoSucursalesPreventivosViewHolder>{
    private final List<ObjetoListadoSucursales> listadoSucursales;
    private final Context context;

    public AdapterListadoSucursalePreventivos(List<ObjetoListadoSucursales> listadoSucursales, Context context) {
        this.listadoSucursales = listadoSucursales;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterListadoSucursalesPreventivosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solo_item_sucursal,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new AdapterListadoSucursalesPreventivosViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListadoSucursalesPreventivosViewHolder holder, int position) {
        holder.txtNombreListadoSucursal.setText(listadoSucursales.get(position).getNombre_sucursal());
        holder.context = context;
    }

    @Override
    public int getItemCount() {
        return listadoSucursales.size();
    }
}
