package com.example.lmmovilmantenimiento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.lmmovilmantenimiento.ContenedorViewHolder.AdapterListadoSucursales;
import com.example.lmmovilmantenimiento.ContenedorViewHolder.ObjetoListadoSucursales;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class MuestreoPendientesActivity extends AppCompatActivity {

    DatabaseReference referenciaCargarInicial;
    FirebaseDatabase myDb;
    RecyclerView rvListadoErroresPDF;
    FirebaseAuth auth;
    private ImageButton ibRegresar;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ConstraintLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muestreo_pendientes);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        listeners();
        validarUsuario();
        cargarRv();
        cargarInfoRv();
    }

    private void listeners() {
        ibRegresar.setOnClickListener(view ->{
            Intent intent = new Intent(MuestreoPendientesActivity.this,InicioActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void validarUsuario() {
        DatabaseReference referenciaUsuario = myDb.getReference("UsuariosLm");
        referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (Objects.requireNonNull(snapshot1.child("uid").getValue()).toString().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())){
                        DatabaseReference referenciaPerfifles = myDb.getReference("Perfiles");
                        referenciaPerfifles.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                    if (Objects.requireNonNull(snapshot1.child("id_perfil").getValue()).toString().equals(snapshot2.getKey())){
                                        if (Objects.requireNonNull(snapshot2.child("descripcion").getValue()).toString().equals("gerente")){
                                            Intent intent = new Intent(MuestreoPendientesActivity.this, ListadoTareasActivity.class);
                                            intent.putExtra("sucursal", Objects.requireNonNull(snapshot1.child("sucursal").getValue()).toString());
                                            intent.putExtra("tipo_usuario", Objects.requireNonNull(snapshot2.child("descripcion").getValue()).toString());
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toasty.error(MuestreoPendientesActivity.this, "Error: "+error.getCode(), Toasty.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(MuestreoPendientesActivity.this, "Error: "+error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarInfoRv() {
        referenciaCargarInicial.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ObjetoListadoSucursales objeto = new ObjetoListadoSucursales(dataSnapshot.getKey());
                        histialResult.add(objeto);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cargarRv() {
        rvListadoErroresPDF.setNestedScrollingEnabled(true);
        rvListadoErroresPDF.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MuestreoPendientesActivity.this);
        rvListadoErroresPDF.setLayoutManager(mLayoutManager);
        layout = findViewById(R.id.consLayout);
        mAdapter = new AdapterListadoSucursales(obtenerDataHistorial(),MuestreoPendientesActivity.this);
        rvListadoErroresPDF.setAdapter(mAdapter);
        ibRegresar = findViewById(R.id.ibRegresar);
    }

    private ArrayList histialResult = new ArrayList<ObjetoListadoSucursales>();

    private ArrayList<ObjetoListadoSucursales> obtenerDataHistorial(){
        return histialResult;
    }

    private void init() {
        myDb = FirebaseDatabase.getInstance();
        referenciaCargarInicial = myDb.getReference("Sucursale");
        rvListadoErroresPDF = (RecyclerView) findViewById(R.id.rvMiListadoSucursales);
        auth = FirebaseAuth.getInstance();
        ibRegresar = (ImageButton) findViewById(R.id.ibRegresar);
    }
}