package com.example.lmmovilmantenimiento.Preventivos.ViewHolderPreventivos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.lmmovilmantenimiento.R;

import java.util.List;

public class AdapterListadoPropios extends RecyclerView.Adapter<AdapterListadoPropiosViewHolder>{
    private final List<ObjetoListadoPreventivos> listadoPreventivos;
    private final Context context;

    public AdapterListadoPropios(List<ObjetoListadoPreventivos> listadoPreventivos, Context context) {
        this.listadoPreventivos = listadoPreventivos;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterListadoPropiosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solo_item_preventivo_propios,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        //AdapterListadoPropiosViewHolder tareasViewHolder = new AdapterListadoPropiosViewHolder(layoutView);
        return new AdapterListadoPropiosViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListadoPropiosViewHolder holder, int position) {
        switch (listadoPreventivos.get(position).getStatus()){
            case "pendiente":
                holder.vEstado.setBackgroundColor(context.getResources().getColor(R.color.pendiente));
                break;
            case "en proceso":
                holder.vEstado.setBackgroundColor(context.getResources().getColor(R.color.enproceso));
                break;
            case "en revision":
                holder.vEstado.setBackgroundColor(context.getResources().getColor(R.color.enrevision));
                break;
            case "terminada":
                holder.vEstado.setBackgroundColor(context.getResources().getColor(R.color.terminado));
                break;
        }
        holder.txtIdTarea.setText(listadoPreventivos.get(position).getIdTarea());
        holder.txtFechaProgramacion.setText(listadoPreventivos.get(position).getFechaProgramacion());
        holder.context = context;
        holder.descripcion = listadoPreventivos.get(position).getDescripcion();
        holder.sucursal = listadoPreventivos.get(position).getSucursal();
        holder.status = listadoPreventivos.get(position).getStatus();
        holder.area = listadoPreventivos.get(position).getArea();
        holder.equipo = listadoPreventivos.get(position).getEquipo();
        holder.fechaProgramacion = listadoPreventivos.get(position).getFechaProgramacion();
        holder.horaProgramacion = listadoPreventivos.get(position).getHoraProgramacion();
        holder.responsable = listadoPreventivos.get(position).getNombre_responsable();
        holder.txtDescripcionMisPreventivos.setText(listadoPreventivos.get(position).getDescripcion());
    }

    @Override
    public int getItemCount() {
        return listadoPreventivos.size();
    }
}
