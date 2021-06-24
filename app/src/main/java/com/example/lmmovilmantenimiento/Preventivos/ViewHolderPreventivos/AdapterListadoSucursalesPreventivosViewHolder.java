package com.example.lmmovilmantenimiento.Preventivos.ViewHolderPreventivos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lmmovilmantenimiento.Preventivos.ListadoPreventivos;
import com.example.lmmovilmantenimiento.R;

public class AdapterListadoSucursalesPreventivosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtNombreListadoSucursal;
    public Context context;

    public AdapterListadoSucursalesPreventivosViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        init();
    }

    private void init() {
        txtNombreListadoSucursal = itemView.findViewById(R.id.txtNombreListadoSucursal);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), ListadoPreventivos.class);
        intent.putExtra("sucursal", txtNombreListadoSucursal.getText().toString());
        context.startActivity(intent);
        ((Activity) context).finish();
    }
}
