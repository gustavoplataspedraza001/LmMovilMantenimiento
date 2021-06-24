package com.example.lmmovilmantenimiento.fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lmmovilmantenimiento.Clases.MisClases;
import com.example.lmmovilmantenimiento.Clases.constantes;
import com.example.lmmovilmantenimiento.ContenedorViewHolder.AdapterListadoTareas;
import com.example.lmmovilmantenimiento.ContenedorViewHolder.ObjetoListadoTareas;
import com.example.lmmovilmantenimiento.InicioActivity;
import com.example.lmmovilmantenimiento.MuestreoPendientesActivity;
import com.example.lmmovilmantenimiento.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class FragmentGrupal extends Fragment{

    DatabaseReference referenciaCorrectivos;
    FirebaseDatabase db;
    FirebaseAuth auth;
    ImageButton ibRegresarTareasG;
    RecyclerView rvMiListadoTareas;
    private RecyclerView.Adapter mAdapter;
    private LinearLayout llFiltros;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button btnFiltroPendiente,btnFiltroEnProceso,btnFiltroEnRevision,btnFiltroTerminada,btnFiltroTodos;

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grupal, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init(view);
        validarUsuario();
        listeners();
        cargarRv();
        extras2();
        MisClases.cargarExistencias(referenciaCorrectivos,btnFiltroPendiente,btnFiltroEnProceso,btnFiltroEnRevision,btnFiltroTerminada,getContext(),"CORRECTIVO",constantes.SUCURSAL_ELEGIDA);
        return view;
    }

    private void validarUsuario() {
        DatabaseReference referenciaUsuario = db.getReference("UsuariosLm");
        referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (snapshot1.child("uid").getValue().toString().equals(auth.getCurrentUser().getUid())){
                        DatabaseReference referenciaPerfiles = db.getReference("Perfiles");
                        referenciaPerfiles.child(snapshot1.child("id_perfil").getValue().toString()).child("descripcion").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue().toString().equals("admin")){
                                    llFiltros.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toasty.error(getContext(), "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(getContext(), "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void listeners() {
        ibRegresarTareasG.setOnClickListener(view ->{
            regresar();
        });
        btnFiltroPendiente.setOnClickListener(view ->{
            btnFiltroPendiente.setVisibility(View.GONE);
            btnFiltroTodos.setVisibility(View.VISIBLE);
            btnFiltroTerminada.setVisibility(View.VISIBLE);
            btnFiltroEnRevision.setVisibility(View.VISIBLE);
            btnFiltroEnProceso.setVisibility(View.VISIBLE);
            histialResult.removeAll(histialResult);
            mAdapter.notifyDataSetChanged();
            MisClases.cargarPendientes(histialResult,mAdapter,referenciaCorrectivos,getContext(),"CORRECTIVO",constantes.SUCURSAL_ELEGIDA);
        });
        btnFiltroEnProceso.setOnClickListener(view ->{
            btnFiltroEnProceso.setVisibility(View.GONE);
            btnFiltroTodos.setVisibility(View.VISIBLE);
            btnFiltroPendiente.setVisibility(View.VISIBLE);
            btnFiltroTerminada.setVisibility(View.VISIBLE);
            btnFiltroEnRevision.setVisibility(View.VISIBLE);
            histialResult.removeAll(histialResult);
            mAdapter.notifyDataSetChanged();
            MisClases.cargarEnProceso(histialResult,mAdapter,referenciaCorrectivos,getContext(),"CORRECTIVO",constantes.SUCURSAL_ELEGIDA);
        });
        btnFiltroEnRevision.setOnClickListener(view ->{
            btnFiltroEnRevision.setVisibility(View.GONE);
            btnFiltroTodos.setVisibility(View.VISIBLE);
            btnFiltroEnProceso.setVisibility(View.VISIBLE);
            btnFiltroPendiente.setVisibility(View.VISIBLE);
            btnFiltroTerminada.setVisibility(View.VISIBLE);
            histialResult.removeAll(histialResult);
            mAdapter.notifyDataSetChanged();
            MisClases.cargarEnRevision(histialResult,mAdapter,referenciaCorrectivos,getContext(),"CORRECTIVO",constantes.SUCURSAL_ELEGIDA);
        });
        btnFiltroTerminada.setOnClickListener(view ->{
            btnFiltroTerminada.setVisibility(View.GONE);
            btnFiltroTodos.setVisibility(View.VISIBLE);
            btnFiltroEnRevision.setVisibility(View.VISIBLE);
            btnFiltroEnProceso.setVisibility(View.VISIBLE);
            btnFiltroPendiente.setVisibility(View.VISIBLE);
            histialResult.removeAll(histialResult);
            mAdapter.notifyDataSetChanged();
            MisClases.cargarTerminada(histialResult,mAdapter,referenciaCorrectivos,getContext(),"CORRECTIVO",constantes.SUCURSAL_ELEGIDA);
        });
        btnFiltroTodos.setOnClickListener(view ->{
            btnFiltroTerminada.setVisibility(View.VISIBLE);
            btnFiltroTodos.setVisibility(View.GONE);
            btnFiltroEnRevision.setVisibility(View.VISIBLE);
            btnFiltroEnProceso.setVisibility(View.VISIBLE);
            btnFiltroPendiente.setVisibility(View.VISIBLE);
            histialResult.removeAll(histialResult);
            mAdapter.notifyDataSetChanged();
            extras();
        });
        btnFiltroPendiente.setOnLongClickListener(v -> {
            Toasty.info(getContext(),"Pendientes",Toasty.LENGTH_SHORT).show();
            return true;
        });
        btnFiltroEnProceso.setOnLongClickListener(view ->{
            Toasty.info(getContext(),"En proceso",Toasty.LENGTH_SHORT).show();
            return true;
        });
        btnFiltroEnRevision.setOnLongClickListener(view ->{
            Toasty.info(getContext(),"En revisión", Toasty.LENGTH_SHORT).show();
            return true;
        });
        btnFiltroTerminada.setOnLongClickListener(view ->{
            Toasty.info(getContext(),"Terminadas",Toasty.LENGTH_SHORT).show();
            return true;
        });
    }

    private void regresar() {
        if (constantes.TIPO_USUARIO.equals("gerente")){
            Intent intent = new Intent(getContext(), InicioActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        else {
            Intent intent = new Intent(getContext(), MuestreoPendientesActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void extras() {
        referenciaCorrectivos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                histialResult.removeAll(histialResult);
                mAdapter.notifyDataSetChanged();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.child("sucursal").getValue().toString().equals(constantes.SUCURSAL_ELEGIDA) && snapshot1.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")) {
                        DatabaseReference referenciaUsuarios = db.getReference("UsuariosLm");
                        referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                    if (snapshot2.child("uid").getValue().toString().equals(auth.getCurrentUser().getUid())){
                                        DatabaseReference referenciaPerfiles = db.getReference("Perfiles");
                                        referenciaPerfiles.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot snapshot3 : snapshot.getChildren()){
                                                    if (snapshot2.child("id_perfil").getValue().toString().equals(snapshot3.getKey())){
                                                        if (snapshot3.child("descripcion").getValue().toString().equals("gerente")){
                                                            if (snapshot1.child("status").getValue().toString().equals("terminada") && snapshot1.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")) {
                                                                referenciaUsuarios.child(snapshot1.child("usuario_responsable").getValue().toString()).child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        ObjetoListadoTareas objeto = new ObjetoListadoTareas(
                                                                                "" + constantes.SUCURSAL_ELEGIDA,
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
                                                                        Toasty.error(getContext(), "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                        else{
                                                            if (snapshot1.child("usuario_responsable").getValue().toString().equals("") && !snapshot1.child("status").getValue().toString().equals("liberada") && snapshot1.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")) {
                                                                ObjetoListadoTareas objeto = new ObjetoListadoTareas(
                                                                        "" + constantes.SUCURSAL_ELEGIDA,
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
                                                            else if (!snapshot1.child("usuario_responsable").getValue().toString().equals("") && !snapshot1.child("status").getValue().toString().equals("liberada") && snapshot1.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")){
                                                                referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        for (DataSnapshot snapshot11 : snapshot.getChildren()) {
                                                                            if (snapshot11.child("uid").getValue().toString().equals(auth.getCurrentUser().getUid())) {
                                                                                DatabaseReference referenciaPerfiles = db.getReference("Perfiles");
                                                                                referenciaPerfiles.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                        for (DataSnapshot snapshot12 : snapshot.getChildren()) {
                                                                                            if (snapshot11.child("id_perfil").getValue().toString().equals(snapshot12.getKey())) {
                                                                                                if (snapshot12.child("descripcion").getValue().toString().equals("admin")) {
                                                                                                    referenciaUsuarios.child(snapshot1.child("usuario_responsable").getValue().toString()).child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                            ObjetoListadoTareas objeto = new ObjetoListadoTareas(
                                                                                                                    "" + constantes.SUCURSAL_ELEGIDA,
                                                                                                                    "" + snapshot1.getKey(),
                                                                                                                    "" + snapshot1.child("descripcion").getValue().toString(),
                                                                                                                    ""+snapshot.getValue().toString(),
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
                                                                                                            Toasty.error(getContext(), "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            }

                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                                                        Toasty.error(getContext(), "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toasty.error(getContext(), "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toasty.error(getContext(), "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(getContext(), "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void extras2() {
        referenciaCorrectivos.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
                if (snapshot1.child("sucursal").getValue().toString().equals(constantes.SUCURSAL_ELEGIDA)) {
                    DatabaseReference referenciaUsuarios = db.getReference("UsuariosLm");
                    referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                if (snapshot2.child("uid").getValue().toString().equals(auth.getCurrentUser().getUid())){
                                    DatabaseReference referenciaPerfiles = db.getReference("Perfiles");
                                    referenciaPerfiles.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot snapshot3 : snapshot.getChildren()){
                                                if (snapshot2.child("id_perfil").getValue().toString().equals(snapshot3.getKey())){
                                                    if (snapshot3.child("descripcion").getValue().toString().equals("gerente")){
                                                        if (snapshot1.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")) {
                                                            if (snapshot1.child("status").getValue().toString().equals("terminada") && snapshot1.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")) {
                                                                referenciaUsuarios.child(snapshot1.child("usuario_responsable").getValue().toString()).child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        ObjetoListadoTareas objeto = new ObjetoListadoTareas(
                                                                                "" + constantes.SUCURSAL_ELEGIDA,
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
                                                                        Toasty.error(getContext(), "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                    else{
                                                        if (snapshot1.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")) {
                                                            if (snapshot1.child("usuario_responsable").getValue().toString().equals("") && !snapshot1.child("status").getValue().toString().equals("liberada") && snapshot1.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")) {
                                                                ObjetoListadoTareas objeto = new ObjetoListadoTareas(
                                                                        "" + constantes.SUCURSAL_ELEGIDA,
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
                                                            else if (!snapshot1.child("usuario_responsable").getValue().toString().equals("") && !snapshot1.child("status").getValue().toString().equals("liberada") && snapshot1.child("tipo_actividad").getValue().toString().equals("CORRECTIVO")) {
                                                                referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        for (DataSnapshot snapshot11 : snapshot.getChildren()) {
                                                                            if (snapshot11.child("uid").getValue().toString().equals(auth.getCurrentUser().getUid())) {
                                                                                DatabaseReference referenciaPerfiles = db.getReference("Perfiles");
                                                                                referenciaPerfiles.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                        for (DataSnapshot snapshot12 : snapshot.getChildren()) {
                                                                                            if (snapshot11.child("id_perfil").getValue().toString().equals(snapshot12.getKey())) {
                                                                                                if (snapshot12.child("descripcion").getValue().toString().equals("admin")) {
                                                                                                    referenciaUsuarios.child(snapshot1.child("usuario_responsable").getValue().toString()).child("nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                            ObjetoListadoTareas objeto = new ObjetoListadoTareas(
                                                                                                                    "" + constantes.SUCURSAL_ELEGIDA,
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
                                                                                                            Toasty.error(getContext(), "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            }

                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                                                        Toasty.error(getContext(), "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toasty.error(getContext(), "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toasty.error(getContext(), "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot1, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot1) { extras(); }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cargarRv() {
        rvMiListadoTareas.setNestedScrollingEnabled(true);
        rvMiListadoTareas.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvMiListadoTareas.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterListadoTareas(obtenerDataHistorial(),getContext());
        rvMiListadoTareas.setAdapter(mAdapter);
    }

    private ArrayList histialResult = new ArrayList<ObjetoListadoTareas>();

    private ArrayList<ObjetoListadoTareas> obtenerDataHistorial(){ return histialResult; }

    private void init(View view) {
        db = FirebaseDatabase.getInstance();
        referenciaCorrectivos = db.getReference("Actividades");
        rvMiListadoTareas = view.findViewById(R.id.rvMiListadoTareas);
        ibRegresarTareasG = view.findViewById(R.id.ibRegresarTareasG);
        auth = FirebaseAuth.getInstance();
        llFiltros = view.findViewById(R.id.llFiltros);
        btnFiltroPendiente = view.findViewById(R.id.btnFiltroPendientes);
        btnFiltroEnProceso = view.findViewById(R.id.btnFiltroEnProceso);
        btnFiltroEnRevision = view.findViewById(R.id.btnFiltroEnRevision);
        btnFiltroTerminada = view.findViewById(R.id.btnFiltroTerminada);
        btnFiltroTodos = view.findViewById(R.id.btnFiltroTodos);
    }
}