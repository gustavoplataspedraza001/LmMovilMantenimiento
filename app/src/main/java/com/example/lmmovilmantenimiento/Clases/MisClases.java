package com.example.lmmovilmantenimiento.Clases;

import android.content.Context;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lmmovilmantenimiento.ContenedorViewHolder.ObjetoListadoTareas;
import com.example.lmmovilmantenimiento.ContenedorViewHolder.ObjetoListadoTareasPropias;
import com.example.lmmovilmantenimiento.Preventivos.ViewHolderPreventivos.ObjetoListadoPreventivos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MisClases {
    public static void cargarExistencias(DatabaseReference referenciaCorrectivos, Button btnFiltroPendiente, Button btnFiltroEnProceso, Button btnFiltroEnRevision, Button btnFiltroTerminada, Context context, String tipo,String sucursal) {
        referenciaCorrectivos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int pendientes = 0, en_proceso = 0, en_revision = 0, terminadas = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (snapshot1.child("tipo_actividad").getValue().toString().equals(tipo) &&
                            snapshot1.child("sucursal").getValue().toString().equals(sucursal) &&
                            snapshot1.child("status").getValue().toString().equals("pendiente")) {
                        pendientes++;
                    }else if (snapshot1.child("tipo_actividad").getValue().toString().equals(tipo) &&
                            snapshot1.child("sucursal").getValue().toString().equals(sucursal) &&
                            snapshot1.child("status").getValue().toString().equals("en proceso")) {
                        en_proceso++;
                    }else if (snapshot1.child("tipo_actividad").getValue().toString().equals(tipo) &&
                            snapshot1.child("sucursal").getValue().toString().equals(sucursal) &&
                            snapshot1.child("status").getValue().toString().equals("en revision")) {
                        en_revision++;
                    }else if (snapshot1.child("tipo_actividad").getValue().toString().equals(tipo) &&
                            snapshot1.child("sucursal").getValue().toString().equals(sucursal) &&
                            snapshot1.child("status").getValue().toString().equals("terminada")) {
                        terminadas++;
                    }
                }
                btnFiltroPendiente.setText(btnFiltroPendiente.getText().toString() + "  " + pendientes);
                btnFiltroEnProceso.setText(btnFiltroEnProceso.getText().toString() + "  " + en_proceso);
                btnFiltroEnRevision.setText(btnFiltroEnRevision.getText().toString() + "  " + en_revision);
                btnFiltroTerminada.setText(btnFiltroTerminada.getText().toString() + "  " + terminadas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error: " + error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void cargarTerminada(ArrayList histialResult, RecyclerView.Adapter mAdapter, DatabaseReference referenciaPreventivos, Context context, String tipo, String sucursal) {
        histialResult.removeAll(histialResult);
        mAdapter.notifyDataSetChanged();
        referenciaPreventivos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (tipo.equals("PREVENTIVO")) {
                        if (snapshot1.child("status").getValue().toString().equals("terminada")
                                && snapshot1.child("tipo_actividad").getValue().toString().equals(tipo)
                                && snapshot1.child("sucursal").getValue().toString().equals(sucursal)) {
                            ObjetoListadoPreventivos objeto = new ObjetoListadoPreventivos(
                                    "" + snapshot1.child("cantidad_frecuencia").getValue().toString(),
                                    "" + snapshot1.child("tipo_frecuencia").getValue().toString(),
                                    "" + snapshot1.child("nombre_responsable").getValue().toString(),
                                    "" + snapshot1.getKey(),
                                    "" + snapshot1.child("area").getValue().toString(),
                                    "" + snapshot1.child("descripcion").getValue().toString(),
                                    "" + snapshot1.child("equipo").getValue().toString(),
                                    "" + snapshot1.child("fechaAlta").getValue().toString(),
                                    "" + snapshot1.child("fechaProgramacion").getValue().toString(),
                                    "" + snapshot1.child("fechaTermino").getValue().toString(),
                                    "" + snapshot1.child("horaProgramacion").getValue().toString(),
                                    "" + snapshot1.child("nombre_alta").getValue().toString(),
                                    "" + snapshot1.child("status").getValue().toString(),
                                    "" + snapshot1.child("sucursal").getValue().toString(),
                                    "" + snapshot1.child("tipo_actividad").getValue().toString(),
                                    "" + snapshot1.child("tipo_programacion").getValue().toString(),
                                    "" + snapshot1.child("usuario_alta").getValue().toString());
                            histialResult.add(objeto);
                            mAdapter.notifyDataSetChanged();
                        }
                    }else if (tipo.equals("CORRECTIVO")){
                        if (snapshot1.child("status").getValue().toString().equals("terminada")
                                && snapshot1.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")
                                && snapshot1.child("sucursal").getValue().toString().equals(sucursal)) {
                            DatabaseReference referenciaUsuarios = FirebaseDatabase.getInstance().getReference("UsuariosLm");
                            referenciaUsuarios.child(snapshot1.child("usuario_responsable").getValue().toString()).child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ObjetoListadoTareas objeto = new ObjetoListadoTareas(
                                            "" + sucursal,
                                            "" + snapshot1.getKey(),
                                            "" + snapshot1.child("descripcion").getValue().toString(),
                                            "" + snapshot1.child("nombre_responsable").getValue().toString(),
                                            "" + snapshot1.child("evidenciaAudio").getValue().toString(),
                                            "" + snapshot1.child("evidenciaVideo").getValue().toString(),
                                            "" + snapshot1.child("evidenciaImagen").getValue().toString(),
                                            "" + snapshot1.child("fechaAlta").getValue().toString(),
                                            "" + snapshot1.child("status").getValue().toString());
                                    histialResult.add(objeto);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    public static void cargarEnRevision(ArrayList histialResult, RecyclerView.Adapter mAdapter, DatabaseReference referenciaPreventivos, Context context, String tipo, String sucursal) {
        histialResult.removeAll(histialResult);
        mAdapter.notifyDataSetChanged();
        referenciaPreventivos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (tipo.equals("PREVENTIVO")) {
                        if (snapshot1.child("status").getValue().toString().equals("en revision")
                        && snapshot1.child("tipo_actividad").getValue().toString().equals(tipo)
                        && snapshot1.child("sucursal").getValue().toString().equals(sucursal)) {
                            ObjetoListadoPreventivos objeto = new ObjetoListadoPreventivos(
                                    "" + snapshot1.child("cantidad_frecuencia").getValue().toString(),
                                    "" + snapshot1.child("tipo_frecuencia").getValue().toString(),
                                    "" + snapshot1.child("nombre_responsable").getValue().toString(),
                                    "" + snapshot1.getKey(),
                                    "" + snapshot1.child("area").getValue().toString(),
                                    "" + snapshot1.child("descripcion").getValue().toString(),
                                    "" + snapshot1.child("equipo").getValue().toString(),
                                    "" + snapshot1.child("fechaAlta").getValue().toString(),
                                    "" + snapshot1.child("fechaProgramacion").getValue().toString(),
                                    "" + snapshot1.child("fechaTermino").getValue().toString(),
                                    "" + snapshot1.child("horaProgramacion").getValue().toString(),
                                    "" + snapshot1.child("nombre_alta").getValue().toString(),
                                    "" + snapshot1.child("status").getValue().toString(),
                                    "" + snapshot1.child("sucursal").getValue().toString(),
                                    "" + snapshot1.child("tipo_actividad").getValue().toString(),
                                    "" + snapshot1.child("tipo_programacion").getValue().toString(),
                                    "" + snapshot1.child("usuario_alta").getValue().toString());
                            histialResult.add(objeto);
                            mAdapter.notifyDataSetChanged();
                        }
                    }else if (tipo.equals("CORRECTIVO")){
                        if (snapshot1.child("status").getValue().toString().equals("en revision")
                        && snapshot1.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")
                        && snapshot1.child("sucursal").getValue().toString().equals(sucursal)) {
                            DatabaseReference referenciaUsuarios = FirebaseDatabase.getInstance().getReference("UsuariosLm");
                            referenciaUsuarios.child(snapshot1.child("usuario_responsable").getValue().toString()).child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ObjetoListadoTareas objeto = new ObjetoListadoTareas(
                                            "" + sucursal,
                                            "" + snapshot1.getKey(),
                                            "" + snapshot1.child("descripcion").getValue().toString(),
                                            "" + snapshot.getValue().toString(),
                                            "" + snapshot1.child("evidenciaAudio").getValue().toString(),
                                            "" + snapshot1.child("evidenciaVideo").getValue().toString(),
                                            "" + snapshot1.child("evidenciaImagen").getValue().toString(),
                                            "" + snapshot1.child("fechaAlta").getValue().toString(),
                                            "" + snapshot1.child("status").getValue().toString());
                                    histialResult.add(objeto);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    public static void cargarEnProceso(ArrayList histialResult, RecyclerView.Adapter mAdapter, DatabaseReference referenciaPreventivos, Context context, String tipo, String sucursal) {
        histialResult.removeAll(histialResult);
        mAdapter.notifyDataSetChanged();
        referenciaPreventivos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (tipo.equals("PREVENTIVO")) {
                        if (snapshot1.child("status").getValue().toString().equals("en proceso")
                        && snapshot1.child("tipo_actividad").getValue().toString().equals(tipo)
                        && snapshot1.child("sucursal").getValue().toString().equals(sucursal)) {
                            ObjetoListadoPreventivos objeto = new ObjetoListadoPreventivos(
                                    "" + snapshot1.child("cantidad_frecuencia").getValue().toString(),
                                    "" + snapshot1.child("tipo_frecuencia").getValue().toString(),
                                    "" + snapshot1.child("nombre_responsable").getValue().toString(),
                                    "" + snapshot1.getKey(),
                                    "" + snapshot1.child("area").getValue().toString(),
                                    "" + snapshot1.child("descripcion").getValue().toString(),
                                    "" + snapshot1.child("equipo").getValue().toString(),
                                    "" + snapshot1.child("fechaAlta").getValue().toString(),
                                    "" + snapshot1.child("fechaProgramacion").getValue().toString(),
                                    "" + snapshot1.child("fechaTermino").getValue().toString(),
                                    "" + snapshot1.child("horaProgramacion").getValue().toString(),
                                    "" + snapshot1.child("nombre_alta").getValue().toString(),
                                    "" + snapshot1.child("status").getValue().toString(),
                                    "" + snapshot1.child("sucursal").getValue().toString(),
                                    "" + snapshot1.child("tipo_actividad").getValue().toString(),
                                    "" + snapshot1.child("tipo_programacion").getValue().toString(),
                                    "" + snapshot1.child("usuario_alta").getValue().toString());
                            histialResult.add(objeto);
                            mAdapter.notifyDataSetChanged();
                        }
                    }else if (tipo.equals("CORRECTIVO")){
                        if (snapshot1.child("status").getValue().toString().equals("en proceso")
                        && snapshot1.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")
                        && snapshot1.child("sucursal").getValue().toString().equals(sucursal)) {
                            DatabaseReference referenciaUsuarios = FirebaseDatabase.getInstance().getReference("UsuariosLm");
                            referenciaUsuarios.child(snapshot1.child("usuario_responsable").getValue().toString()).child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ObjetoListadoTareas objeto = new ObjetoListadoTareas(
                                            "" + sucursal,
                                            "" + snapshot1.getKey(),
                                            "" + snapshot1.child("descripcion").getValue().toString(),
                                            ""+snapshot1.child("nombre_responsable").getValue().toString(),
                                            "" + snapshot1.child("evidenciaAudio").getValue().toString(),
                                            "" + snapshot1.child("evidenciaVideo").getValue().toString(),
                                            "" + snapshot1.child("evidenciaImagen").getValue().toString(),
                                            "" + snapshot1.child("fechaAlta").getValue().toString(),
                                            "" + snapshot1.child("status").getValue().toString());
                                    histialResult.add(objeto);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });

    }

    public static void cargarPendientes(ArrayList histialResult, RecyclerView.Adapter mAdapter, DatabaseReference referenciaPreventivos, Context context, String tipo, String sucursal) {
        histialResult.removeAll(histialResult);
        mAdapter.notifyDataSetChanged();
        referenciaPreventivos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (tipo.equals("PREVENTIVO")) {
                        if (snapshot1.child("status").getValue().toString().equals("pendiente")
                        && snapshot1.child("tipo_actividad").getValue().toString().equals(tipo)
                        && snapshot1.child("sucursal").getValue().toString().equals(sucursal)) {
                            ObjetoListadoPreventivos objeto = new ObjetoListadoPreventivos(
                                    "" + snapshot1.child("cantidad_frecuencia").getValue().toString(),
                                    "" + snapshot1.child("tipo_frecuencia").getValue().toString(),
                                    "" + snapshot1.child("nombre_responsable").getValue().toString(),
                                    "" + snapshot1.getKey(),
                                    "" + snapshot1.child("area").getValue().toString(),
                                    "" + snapshot1.child("descripcion").getValue().toString(),
                                    "" + snapshot1.child("equipo").getValue().toString(),
                                    "" + snapshot1.child("fechaAlta").getValue().toString(),
                                    "" + snapshot1.child("fechaProgramacion").getValue().toString(),
                                    "" + snapshot1.child("fechaTermino").getValue().toString(),
                                    "" + snapshot1.child("horaProgramacion").getValue().toString(),
                                    "" + snapshot1.child("nombre_alta").getValue().toString(),
                                    "" + snapshot1.child("status").getValue().toString(),
                                    "" + snapshot1.child("sucursal").getValue().toString(),
                                    "" + snapshot1.child("tipo_actividad").getValue().toString(),
                                    "" + snapshot1.child("tipo_programacion").getValue().toString(),
                                    "" + snapshot1.child("usuario_alta").getValue().toString());
                            histialResult.add(objeto);
                            mAdapter.notifyDataSetChanged();
                        }
                    }else if (tipo.equals("CORRECTIVO")){
                        if (snapshot1.child("status").getValue().toString().equals("pendiente" )
                        && snapshot1.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")
                        && snapshot1.child("sucursal").getValue().toString().equals(sucursal)) {
                            ObjetoListadoTareas objeto = new ObjetoListadoTareas(
                                    "" + sucursal,
                                    "" + snapshot1.getKey(),
                                    "" + snapshot1.child("descripcion").getValue().toString(),
                                    "Sin asignar",
                                    "" + snapshot1.child("evidenciaAudio").getValue().toString(),
                                    "" + snapshot1.child("evidenciaVideo").getValue().toString(),
                                    "" + snapshot1.child("evidenciaImagen").getValue().toString(),
                                    "" + snapshot1.child("fechaAlta").getValue().toString(),
                                    "" + snapshot1.child("status").getValue().toString());
                            histialResult.add(objeto);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    public static void cargarEnProcesoPropios(ArrayList histialResult, RecyclerView.Adapter mAdapter, DatabaseReference referenciaCorrectivos, Context context, String tipo,String sucursal){
        histialResult.removeAll(histialResult);
        mAdapter.notifyDataSetChanged();
        DatabaseReference referenciaUsuario = FirebaseDatabase.getInstance().getReference("UsuariosLm");
        referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (snapshot1.child("uid").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        referenciaCorrectivos.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                    if (tipo.equals("CORRECTIVO_PROPIO")) {
                                        if (snapshot2.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")) {
                                            if (snapshot1.getKey().equals(snapshot2.child("usuario_responsable").getValue().toString())
                                            && snapshot2.child("sucursal").getValue().toString().equals(sucursal)
                                            && snapshot2.child("status").getValue().toString().equals("en proceso")) {
                                                ObjetoListadoTareasPropias objeto = new ObjetoListadoTareasPropias(
                                                        "" + sucursal,
                                                        "" + snapshot2.getKey(),
                                                        "" + snapshot2.child("descripcion").getValue().toString(),
                                                        "" + snapshot2.child("nombre_responsable").getValue().toString(),
                                                        "" + snapshot2.child("evidenciaAudio").getValue().toString(),
                                                        "" + snapshot2.child("evidenciaVideo").getValue().toString(),
                                                        "" + snapshot2.child("evidenciaImagen").getValue().toString(),
                                                        "" + snapshot2.child("fechaAlta").getValue().toString(),
                                                        "" + snapshot2.child("status").getValue().toString());
                                                histialResult.add(objeto);
                                                mAdapter.notifyDataSetChanged();
                                            }

                                        }
                                    }
                                    else if (tipo.equals("PREVENTIVO_PROPIO")){
                                        if (snapshot2.child("tipo_actividad").getValue().toString().equals("PREVENTIVO")) {
                                            if (snapshot1.getKey().equals(snapshot2.child("usuario_responsable").getValue().toString())
                                            && snapshot2.child("sucursal").getValue().toString().equals(sucursal)
                                            && snapshot2.child("status").getValue().toString().equals("en proceso")) {
                                                ObjetoListadoPreventivos objeto = new ObjetoListadoPreventivos(
                                                        ""+snapshot2.child("cantidad_frecuencia").getValue().toString(),
                                                        ""+snapshot2.child("tipo_frecuencia").getValue().toString(),
                                                        ""+snapshot2.child("nombre_responsable").getValue().toString(),
                                                        ""+snapshot2.getKey(),
                                                        ""+snapshot2.child("area").getValue().toString(),
                                                        ""+snapshot2.child("descripcion").getValue().toString(),
                                                        ""+snapshot2.child("equipo").getValue().toString(),
                                                        ""+snapshot2.child("fechaAlta").getValue().toString(),
                                                        ""+snapshot2.child("fechaProgramacion").getValue().toString(),
                                                        ""+snapshot2.child("fechaTermino").getValue().toString(),
                                                        ""+snapshot2.child("horaProgramacion").getValue().toString(),
                                                        ""+snapshot2.child("nombre_alta").getValue().toString(),
                                                        ""+snapshot2.child("status").getValue().toString(),
                                                        ""+snapshot2.child("sucursal").getValue().toString(),
                                                        ""+snapshot2.child("tipo_actividad").getValue().toString(),
                                                        ""+snapshot2.child("tipo_programacion").getValue().toString(),
                                                        ""+snapshot2.child("usuario_alta").getValue().toString());
                                                histialResult.add(objeto);
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toasty.error(context, "Error : " + error.getCode(), Toasty.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    public static void cargarEnRevisionPropios(ArrayList histialResult, RecyclerView.Adapter mAdapter, DatabaseReference referenciaCorrectivos, Context context, String tipo,String sucursal){
        histialResult.removeAll(histialResult);
        mAdapter.notifyDataSetChanged();
        DatabaseReference referenciaUsuario = FirebaseDatabase.getInstance().getReference("UsuariosLm");
        referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (snapshot1.child("uid").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        referenciaCorrectivos.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                    if (tipo.equals("CORRECTIVO_PROPIO")) {
                                        if (snapshot2.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")) {
                                            if (snapshot1.getKey().equals(snapshot2.child("usuario_responsable").getValue().toString())
                                                    && snapshot2.child("sucursal").getValue().toString().equals(sucursal)
                                                    && snapshot2.child("status").getValue().toString().equals("en revision")) {
                                                ObjetoListadoTareasPropias objeto = new ObjetoListadoTareasPropias(
                                                        "" + sucursal,
                                                        "" + snapshot2.getKey(),
                                                        "" + snapshot2.child("descripcion").getValue().toString(),
                                                        "" + snapshot2.child("nombre_responsable").getValue().toString(),
                                                        "" + snapshot2.child("evidenciaAudio").getValue().toString(),
                                                        "" + snapshot2.child("evidenciaVideo").getValue().toString(),
                                                        "" + snapshot2.child("evidenciaImagen").getValue().toString(),
                                                        "" + snapshot2.child("fechaAlta").getValue().toString(),
                                                        "" + snapshot2.child("status").getValue().toString());
                                                histialResult.add(objeto);
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                    else if (tipo.equals("PREVENTIVO_PROPIO")){
                                        if (snapshot2.child("tipo_actividad").getValue().toString().equals("PREVENTIVO")) {
                                            if (snapshot1.getKey().equals(snapshot2.child("usuario_responsable").getValue().toString())
                                                    && snapshot2.child("sucursal").getValue().toString().equals(sucursal)
                                                    && snapshot2.child("status").getValue().toString().equals("en revision")) {
                                                ObjetoListadoPreventivos objeto = new ObjetoListadoPreventivos(
                                                        ""+snapshot2.child("cantidad_frecuencia").getValue().toString(),
                                                        ""+snapshot2.child("tipo_frecuencia").getValue().toString(),
                                                        ""+snapshot2.child("nombre_responsable").getValue().toString(),
                                                        ""+snapshot2.getKey(),
                                                        ""+snapshot2.child("area").getValue().toString(),
                                                        ""+snapshot2.child("descripcion").getValue().toString(),
                                                        ""+snapshot2.child("equipo").getValue().toString(),
                                                        ""+snapshot2.child("fechaAlta").getValue().toString(),
                                                        ""+snapshot2.child("fechaProgramacion").getValue().toString(),
                                                        ""+snapshot2.child("fechaTermino").getValue().toString(),
                                                        ""+snapshot2.child("horaProgramacion").getValue().toString(),
                                                        ""+snapshot2.child("nombre_alta").getValue().toString(),
                                                        ""+snapshot2.child("status").getValue().toString(),
                                                        ""+snapshot2.child("sucursal").getValue().toString(),
                                                        ""+snapshot2.child("tipo_actividad").getValue().toString(),
                                                        ""+snapshot2.child("tipo_programacion").getValue().toString(),
                                                        ""+snapshot2.child("usuario_alta").getValue().toString());
                                                histialResult.add(objeto);
                                                mAdapter.notifyDataSetChanged();
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    public static void cargarTerminadasPropias(ArrayList histialResult, RecyclerView.Adapter mAdapter, DatabaseReference referenciaCorrectivos, Context context, String tipo, String sucursal){
        histialResult.removeAll(histialResult);
        mAdapter.notifyDataSetChanged();
        DatabaseReference referenciaUsuario = FirebaseDatabase.getInstance().getReference("UsuariosLm");
        referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (snapshot1.child("uid").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        referenciaCorrectivos.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                    if (tipo.equals("CORRECTIVO_PROPIO")) {
                                        if (snapshot2.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")) {
                                            if (snapshot1.getKey().equals(snapshot2.child("usuario_responsable").getValue().toString())
                                                    && snapshot2.child("sucursal").getValue().toString().equals(sucursal)
                                                    && snapshot2.child("status").getValue().toString().equals("terminada")) {
                                                ObjetoListadoTareasPropias objeto = new ObjetoListadoTareasPropias(
                                                        "" + sucursal,
                                                        "" + snapshot2.getKey(),
                                                        "" + snapshot2.child("descripcion").getValue().toString(),
                                                        "" + snapshot2.child("nombre_responsable").getValue().toString(),
                                                        "" + snapshot2.child("evidenciaAudio").getValue().toString(),
                                                        "" + snapshot2.child("evidenciaVideo").getValue().toString(),
                                                        "" + snapshot2.child("evidenciaImagen").getValue().toString(),
                                                        "" + snapshot2.child("fechaAlta").getValue().toString(),
                                                        "" + snapshot2.child("status").getValue().toString());
                                                histialResult.add(objeto);
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                    else if (tipo.equals("PREVENTIVO_PROPIO")){
                                        if (snapshot2.child("tipo_actividad").getValue().toString().equals("PREVENTIVO")) {
                                            if (snapshot1.getKey().equals(snapshot2.child("usuario_responsable").getValue().toString())
                                                    && snapshot2.child("sucursal").getValue().toString().equals(sucursal)
                                                    && snapshot2.child("status").getValue().toString().equals("terminada")) {
                                                ObjetoListadoPreventivos objeto = new ObjetoListadoPreventivos(
                                                        ""+snapshot2.child("cantidad_frecuencia").getValue().toString(),
                                                        ""+snapshot2.child("tipo_frecuencia").getValue().toString(),
                                                        ""+snapshot2.child("nombre_responsable").getValue().toString(),
                                                        ""+snapshot2.getKey(),
                                                        ""+snapshot2.child("area").getValue().toString(),
                                                        ""+snapshot2.child("descripcion").getValue().toString(),
                                                        ""+snapshot2.child("equipo").getValue().toString(),
                                                        ""+snapshot2.child("fechaAlta").getValue().toString(),
                                                        ""+snapshot2.child("fechaProgramacion").getValue().toString(),
                                                        ""+snapshot2.child("fechaTermino").getValue().toString(),
                                                        ""+snapshot2.child("horaProgramacion").getValue().toString(),
                                                        ""+snapshot2.child("nombre_alta").getValue().toString(),
                                                        ""+snapshot2.child("status").getValue().toString(),
                                                        ""+snapshot2.child("sucursal").getValue().toString(),
                                                        ""+snapshot2.child("tipo_actividad").getValue().toString(),
                                                        ""+snapshot2.child("tipo_programacion").getValue().toString(),
                                                        ""+snapshot2.child("usuario_alta").getValue().toString());
                                                histialResult.add(objeto);
                                                mAdapter.notifyDataSetChanged();
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }
}