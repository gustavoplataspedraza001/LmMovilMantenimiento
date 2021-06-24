package com.example.lmmovilmantenimiento.ContenedorViewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lmmovilmantenimiento.ListadoTareasActivity;
import com.example.lmmovilmantenimiento.R;


public class AdapterListadoSucursalesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtNombreListadoSucursal;
    public Context context;
    public AdapterListadoSucursalesViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        init();
    }

    private void init() {
        txtNombreListadoSucursal = (TextView) itemView.findViewById(R.id.txtNombreListadoSucursal);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), ListadoTareasActivity.class);
        intent.putExtra("sucursal", txtNombreListadoSucursal.getText().toString());
        context.startActivity(intent);
        ((Activity) context).finish();
    }
}
