package com.example.lmmovilmantenimiento.Preventivos.ViewHolderPreventivos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lmmovilmantenimiento.R;

import java.util.List;

public class AdapterListadoPreventivos extends RecyclerView.Adapter<AdapterListadoPreventivosViewHolder>{
    private List<ObjetoListadoPreventivos> listadoTareas;
    private Context context;
    private View view;

    public AdapterListadoPreventivos(List<ObjetoListadoPreventivos> listadoTareas, Context context, View view) {
        this.listadoTareas = listadoTareas;
        this.context = context;
        this.view = view;
    }

    @NonNull
    @Override
    public AdapterListadoPreventivosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        android.view.View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solo_item_preventivo,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new AdapterListadoPreventivosViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListadoPreventivosViewHolder holder, int position) {
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
        }
        holder.txtIdTarea.setText(listadoTareas.get(position).getIdTarea());
        holder.txtFechaProgramacion.setText(listadoTareas.get(position).getFechaProgramacion());
        holder.fechaProgramacion = listadoTareas.get(position).getFechaProgramacion();
        holder.horaProgramacion = listadoTareas.get(position).getHoraProgramacion();
        holder.context = context;
        holder.descripcion = listadoTareas.get(position).getDescripcion();
        holder.sucursal = listadoTareas.get(position).getSucursal();
        holder.area = listadoTareas.get(position).getArea();
        holder.equipo = listadoTareas.get(position).getEquipo();
        holder.sucursal = listadoTareas.get(position).getSucursal();
        holder.status = listadoTareas.get(position).getStatus();
        holder.txtResponsablePreventivo.setText(listadoTareas.get(position).getNombre_responsable());
        holder.viewReal = view;
        holder.tipo_frecuencia = listadoTareas.get(position).getTipo_frecuencia();
        holder.cantidad_frecuencia = listadoTareas.get(position).getCantidad_frecuencia();
        holder.txtDescTodosPreventivos.setText(listadoTareas.get(position).getDescripcion());
    }

    @Override
    public int getItemCount() {
        return listadoTareas.size();
    }
}
