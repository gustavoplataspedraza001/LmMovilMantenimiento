package com.example.lmmovilmantenimiento.ContenedorViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lmmovilmantenimiento.R;

import java.util.List;

public class AdapterListadoTareasPropias extends RecyclerView.Adapter<AdapterListadoTareasPropiasViewHolder> {
    private final List<ObjetoListadoTareasPropias> listadoTareasPropias;
    private final Context context;

    public AdapterListadoTareasPropias(List<ObjetoListadoTareasPropias> listadoTareasPropias, Context context) {
        this.listadoTareasPropias = listadoTareasPropias;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterListadoTareasPropiasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solo_item_tarea_propias,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new AdapterListadoTareasPropiasViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListadoTareasPropiasViewHolder holder, int position) {
        switch (listadoTareasPropias.get(position).getStatus()){
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
        holder.txtIdTarea.setText(listadoTareasPropias.get(position).getIdTarea());
        holder.txtFechaAlta.setText(listadoTareasPropias.get(position).getFechaAlta());
        holder.context = context;
        holder.url = listadoTareasPropias.get(position).getEvidenciaImagen();
        holder.descripcion = listadoTareasPropias.get(position).getDescripcion();
        holder.sucursal = listadoTareasPropias.get(position).getSucursal();
        holder.status = listadoTareasPropias.get(position).getStatus();
        holder.responsable = listadoTareasPropias.get(position).getUsuario_responsable();
        holder.txtDescMisCorrectivos.setText(listadoTareasPropias.get(position).getDescripcion());
    }

    @Override
    public int getItemCount() {
        return listadoTareasPropias.size();
    }
}
