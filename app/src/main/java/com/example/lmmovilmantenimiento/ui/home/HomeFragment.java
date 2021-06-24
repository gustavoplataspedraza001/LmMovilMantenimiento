package com.example.lmmovilmantenimiento.ui.home;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.lmmovilmantenimiento.ActivityAltaCorrectivos;
import com.example.lmmovilmantenimiento.ActivityNoInternet;
import com.example.lmmovilmantenimiento.Almacenaje.Comun;
import com.example.lmmovilmantenimiento.AltaActividadActivity;
import com.example.lmmovilmantenimiento.Clases.constantes;
import com.example.lmmovilmantenimiento.MuestreoPendientesActivity;
import com.example.lmmovilmantenimiento.Preventivos.ActivityAltaPreventivo;
import com.example.lmmovilmantenimiento.Preventivos.MuestreoSucursales;
import com.example.lmmovilmantenimiento.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private CardView btnAltaCorrectivos,btnUsoComun,btnSucursal,btnAltaPreventivo,btnListarPreventivos;
    /***carga firebase*/
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference referenciaDetallePerfil,referenciaUsuario;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_home, container, false);
        init(mView);
        cargarTipoUsuario();
        new asincrona().execute();
        listeners();
        return mView;
    }

    private void cargarTipoUsuario() {
        referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    referenciaUsuario.child(snapshot1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.child("uid").getValue().toString().equals(auth.getCurrentUser().getUid())) {
                                Comun.id_perfil_tabla_usuario = snapshot1.child("id_perfil").getValue().toString();
                                DatabaseReference referenciaDetalleUsuario = db.getReference("DetalleUsuario");
                                referenciaDetalleUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                            referenciaDetalleUsuario.child(snapshot1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot2.child("id_usuario").getValue().toString().equals(snapshot1.getKey())) {
                                                        Comun.solo_lectura = snapshot.child("solo_lectura").getValue().toString();
                                                        Comun.solo_sucursal = snapshot.child("solo_sucursal").getValue().toString();
                                                        Comun.registro = snapshot.child("registros").getValue().toString();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(getContext(), "Error detalle usuario " + error.getCode(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), "Error obteniendo de detalle usuario " + error.getCode(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Error obteniendo info. usuario " + error.getCode(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public class asincrona extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            extras();
            return null;
        }
    }
    private void extras() {
        referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (snapshot1.child("uid").getValue().toString().equals(auth.getCurrentUser().getUid())){
                        referenciaDetallePerfil.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                referenciaDetallePerfil.child(snapshot2.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.child("id_perfil").getValue().toString().equals(snapshot1.child("id_perfil").getValue().toString())){
                                            String id_modulo = snapshot.child("id_modulo").getValue().toString();
                                            DatabaseReference referenciaModulos = db.getReference("Modulos");
                                            referenciaModulos.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot snapshot3 : snapshot.getChildren()){
                                                        if (snapshot3.getKey().equals(id_modulo)){
                                                            referenciaModulos.child(snapshot3.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.child("modulo").getValue().toString().equals("ActivityCorrectivos")){
                                                                        btnAltaCorrectivos.setEnabled(true);
                                                                        btnAltaCorrectivos.setCardBackgroundColor(getContext().getResources().getColor(R.color.white));
                                                                    }
                                                                    /*
                                                                    else if (snapshot.child("modulo").getValue().toString().equals("ActivityAltaPreventivo")){
                                                                        btnAltaPreventivo.setVisibility(View.VISIBLE);
                                                                    }*/
                                                                    else if (snapshot.child("modulo").getValue().toString().equals("ActivityActividadPropia")){
                                                                        btnSucursal.setEnabled(true);
                                                                        btnSucursal.setCardBackgroundColor(getContext().getResources().getColor(R.color.white));
                                                                    }

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    Toast.makeText(getContext(), "Error: "+error.getCode(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(getContext(), "Error en info. modelos: "+error.getCode(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), "Error detalle perfil: "+error.getCode(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Error detalle perfil: "+error.getCode(), Toast.LENGTH_SHORT).show();
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

    private void listeners() {
        btnAltaCorrectivos.setOnClickListener(view ->{
            if (Comun.solo_lectura.equals("1")){
                Toast.makeText(getContext(), "No puede acceder a este módulo, permisos: solo lectura", Toast.LENGTH_SHORT).show();
            }else if (Comun.solo_lectura.equals("0")){
                if (validarConexion()) {
                    calculo();
                    Intent intent = new Intent(getContext(), ActivityAltaCorrectivos.class);
                    startActivity(intent);
                    getActivity().finish();
                }else {
                    Intent intent = new Intent(getContext(), ActivityNoInternet.class);
                    intent.putExtra("origen","principal");
                    startActivity(intent);
                    getActivity().finish();
                    Animatoo.animateSlideLeft(getContext());
                    getActivity().finish();
                }
            }else{
                Toast.makeText(getContext(), "Error inesperado", Toast.LENGTH_SHORT).show();
            }
        });
        btnUsoComun.setOnClickListener(view ->{
            Intent intent = new Intent(getContext(), MuestreoPendientesActivity.class);
            startActivity(intent);
        });
        btnSucursal.setOnClickListener(view ->{
            calculo();
            Intent intent = new Intent(getContext(), AltaActividadActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
        btnAltaPreventivo.setOnClickListener(view ->{
            if (Comun.solo_lectura.equals("1")){
                Toast.makeText(getContext(), "No puede acceder a este módulo, permisos: solo lectura", Toast.LENGTH_SHORT).show();
            }
            else if (Comun.solo_lectura.equals("0")){
                if (validarConexion()) {
                    calculo();
                    Intent intent = new Intent(getContext(), ActivityAltaPreventivo.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getContext(), ActivityNoInternet.class);
                    intent.putExtra("origen","principal");
                    startActivity(intent);
                    getActivity().finish();
                    Animatoo.animateSlideLeft(getContext());
                }
            }
            else{
                Toast.makeText(getContext(), "Error inesperado", Toast.LENGTH_SHORT).show();
            }
        });
        btnListarPreventivos.setOnClickListener(view ->{
            Intent intent = new Intent(getContext(), MuestreoSucursales.class);
            startActivity(intent);
        });
    }

    private void init(View mView) {
        btnAltaCorrectivos = (CardView) mView.findViewById(R.id.btnActivityCorrectivos);
        btnUsoComun = (CardView) mView.findViewById(R.id.btnListadoActividades);
        btnSucursal = (CardView) mView.findViewById(R.id.btnSucursal);
        btnSucursal.setEnabled(false);
        btnSucursal.setCardBackgroundColor(getResources().getColor(R.color.pistaColor));
        btnAltaCorrectivos.setEnabled(false);
        btnAltaCorrectivos.setCardBackgroundColor(getResources().getColor(R.color.pistaColor));
        btnAltaPreventivo = (CardView) mView.findViewById(R.id.btnAltaPreventivo);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        referenciaUsuario = db.getReference("UsuariosLm");
        referenciaDetallePerfil = db.getReference("DetallePerfil");
        btnListarPreventivos = (CardView) mView.findViewById(R.id.btnListarPreventivos);
    }
    private boolean validarConexion() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo internetActivo = cm.getActiveNetworkInfo();
        if (internetActivo == null){
            return false;
        }else{
            return true;
        }
    }
    public void calculo() {
        List<String> latitudes = new ArrayList<String>();
        List<String> longitudes = new ArrayList<String>();
        List<Double> resultados = new ArrayList<Double>();
        latitudes.add("24.86664051365798");
        latitudes.add("24.86672032869385");
        latitudes.add("24.85823317291077");
        latitudes.add("25.276759163660444");
        latitudes.add("24.857187187707332");
        longitudes.add("-99.57310624232748");
        longitudes.add("-99.55843806264654");
        longitudes.add("-99.56188446930987");
        longitudes.add("-100.01812577214193");
        longitudes.add("-99.53908324237378");
        for (int i = 0; i < longitudes.size(); i++){
            double resultado = calcularDistancia(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i)));
            resultados.add(resultado);
        }
        int sucursalPosicion= 0;
        for (int j = 0; j<resultados.size();j++){
            if (resultados.get(j) == Collections.min(resultados)){
                sucursalPosicion = j;
            }
        }
        switch (sucursalPosicion){
            case 0:
                constantes.NOMBRE_SUCURSAL = "Díaz Ordaz";
                break;
            case 1:
                constantes.NOMBRE_SUCURSAL ="Arboledas";
                break;
            case 2:
                constantes.NOMBRE_SUCURSAL = "Villegas";
                break;
            case 3:
                constantes.NOMBRE_SUCURSAL = "Allende";
                break;
            case 4:
                constantes.NOMBRE_SUCURSAL = "La Petaca";
                break;
        }
    }
    private double calcularDistancia(double latitud,double longitud){
        Location locationA = new Location("punto A");
        locationA.setLatitude(Double.parseDouble(constantes.LATITUD));
        locationA.setLongitude(Double.parseDouble(constantes.LONGITUD));
        Location locationB = new Location("punto B");
        locationB.setLatitude(latitud);
        locationB.setLongitude(longitud);
        float distancia = locationA.distanceTo(locationB);
        return distancia;
    }
}