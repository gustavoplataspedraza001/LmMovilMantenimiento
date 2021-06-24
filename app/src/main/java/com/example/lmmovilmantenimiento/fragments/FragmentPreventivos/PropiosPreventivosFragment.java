package com.example.lmmovilmantenimiento.fragments.FragmentPreventivos;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.lmmovilmantenimiento.Clases.MisClases;
import com.example.lmmovilmantenimiento.Clases.constantes;
import com.example.lmmovilmantenimiento.InicioActivity;
import com.example.lmmovilmantenimiento.Preventivos.MuestreoSucursales;
import com.example.lmmovilmantenimiento.Preventivos.ViewHolderPreventivos.AdapterListadoPropios;
import com.example.lmmovilmantenimiento.Preventivos.ViewHolderPreventivos.ObjetoListadoPreventivos;
import com.example.lmmovilmantenimiento.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class PropiosPreventivosFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    DatabaseReference referenciaPreventivos;
    FirebaseDatabase db;
    FirebaseAuth auth;
    ImageButton ibRegresarTareasP;
    RecyclerView rvMiListadoTareasP;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button btnFiltrosEnProceso,btnFiltrosEnRevision,btnFiltrosTerminadas,btnFiltroTodos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_propios_preventivos, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init(view);
        listeners();
        cargarRv();
        extras();
        return view;
    }

    private void init(View view) {
        db = FirebaseDatabase.getInstance();
        referenciaPreventivos = db.getReference("Actividades");
        auth = FirebaseAuth.getInstance();
        rvMiListadoTareasP = view.findViewById(R.id.rvMiListadoTareasPropias);
        ibRegresarTareasP = view.findViewById(R.id.ibRegresarTareasP);
        btnFiltrosEnProceso = view.findViewById(R.id.btnFiltroEnProceso);
        btnFiltrosEnRevision = view.findViewById(R.id.btnFiltroEnRevision);
        btnFiltroTodos = view.findViewById(R.id.btnFiltroTodos);
        btnFiltrosTerminadas = view.findViewById(R.id.btnFiltroTerminada);
    }

    private void listeners(){
        btnFiltrosEnProceso.setOnClickListener(view ->{
            histialResult.removeAll(histialResult);
            mAdapter.notifyDataSetChanged();
            MisClases.cargarEnProcesoPropios(histialResult,mAdapter, referenciaPreventivos,getContext(),"PREVENTIVO_PROPIO",constantes.SUCURSAL_ELEGIDA);
        });
        btnFiltrosEnRevision.setOnClickListener(view ->{
            histialResult.removeAll(histialResult);
            mAdapter.notifyDataSetChanged();
            MisClases.cargarEnRevisionPropios(histialResult,mAdapter,referenciaPreventivos,getContext(),"PREVENTIVO_PROPIO",constantes.SUCURSAL_ELEGIDA);
        });
        btnFiltrosTerminadas.setOnClickListener(view ->{
            histialResult.removeAll(histialResult);
            mAdapter.notifyDataSetChanged();
            MisClases.cargarTerminadasPropias(histialResult,mAdapter,referenciaPreventivos,getContext(),"PREVENTIVO_PROPIO",constantes.SUCURSAL_ELEGIDA);
        });
        btnFiltroTodos.setOnClickListener(view ->{
            histialResult.removeAll(histialResult);
            mAdapter.notifyDataSetChanged();
            cargarTodos();
        });
        ibRegresarTareasP.setOnClickListener(view ->{
            regresar();
        });
    }

    private void regresar() {
        if (constantes.TIPO_USUARIO.equals("gerente")){
            Intent intent = new Intent(getContext(), InicioActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        else {
            Intent intent = new Intent(getContext(), MuestreoSucursales.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void cargarTodos() {
        histialResult.removeAll(histialResult);
        mAdapter.notifyDataSetChanged();
        extras();
    }

    private void cargarRv() {
        rvMiListadoTareasP.setNestedScrollingEnabled(true);
        rvMiListadoTareasP.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        rvMiListadoTareasP.setLayoutManager(mLayoutManager);
        mAdapter = new AdapterListadoPropios(obtenerDataHistorial(),getContext());
        rvMiListadoTareasP.setAdapter(mAdapter);
    }

    private ArrayList histialResult = new ArrayList<>();

    private ArrayList<ObjetoListadoPreventivos> obtenerDataHistorial(){return histialResult;}

    private void extras() {
        DatabaseReference referenciaUsuario = db.getReference("UsuariosLm");
        referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (snapshot1.child("uid").getValue().toString().equals(auth.getCurrentUser().getUid())){
                        referenciaPreventivos.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                    if (snapshot2.child("tipo_actividad").getValue().toString().equals("PREVENTIVO")) {
                                        if (snapshot2.child("sucursal").getValue().toString().equals(constantes.SUCURSAL_ELEGIDA)
                                                && snapshot2.child("usuario_responsable").getValue().toString().equals(snapshot1.getKey())
                                                && !snapshot2.child("status").getValue().toString().equals("liberada")) {
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