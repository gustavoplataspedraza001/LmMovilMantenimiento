package com.example.lmmovilmantenimiento.ContenedorViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lmmovilmantenimiento.R;
import com.google.firebase.database.core.view.View;

import java.util.List;

public class AdapterListadoTareas extends RecyclerView.Adapter<AdapterListadoTareasViewHolder> {
    private final List<ObjetoListadoTareas> listadoTareas;
    private final Context context;

    public AdapterListadoTareas(List<ObjetoListadoTareas> listadoTareas, Context context) {
        this.listadoTareas = listadoTareas;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterListadoTareasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solo_item_tarea,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new AdapterListadoTareasViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListadoTareasViewHolder holder, int position) {
        switch (listadoTareas.get(position).getStatus()){
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
            default:
                break;
        }
        holder.txtIdTarea.setText(listadoTareas.get(position).getIdTarea());
        holder.txtResponsableTarea.setText(listadoTareas.get(position).getUsuario_responsable());
        holder.txtFechaAlta.setText(listadoTareas.get(position).getFechaAlta());
        holder.descripcion  =listadoTareas.get(position).getDescripcion();
        holder.url = listadoTareas.get(position).getEvidenciaImagen();
        holder.context = context;
        holder.sucursal = listadoTareas.get(position).getSucursal();
        holder.status = listadoTareas.get(position).getStatus();
        holder.txtDescripcionCorrectivos.setText(listadoTareas.get(position).getDescripcion());
    }

    @Override
    public int getItemCount() {
        return listadoTareas.size();
    }
}
