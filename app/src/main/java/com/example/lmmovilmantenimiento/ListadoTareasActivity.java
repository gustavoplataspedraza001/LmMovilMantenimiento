package com.example.lmmovilmantenimiento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.view.MenuItem;

import com.example.lmmovilmantenimiento.Clases.constantes;
import com.example.lmmovilmantenimiento.fragments.FragmentGrupal;
import com.example.lmmovilmantenimiento.fragments.FragmentPropios;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class ListadoTareasActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_tareas);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    private void init() {
        bottomNavigationView = findViewById(R.id.btmNAvigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationClicked);
        constantes.SUCURSAL_ELEGIDA = getIntent().getExtras().getString("sucursal");
        constantes.TIPO_USUARIO = getIntent().getExtras().getString("tipo_usuario","comun");
        String validar = getIntent().getExtras().getString("tipo_origen", "");
        if (validar.equals("propios")){
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, new FragmentPropios()).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, new FragmentGrupal()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        regresar();
    }

    private void regresar() {
        String tipo_usuario = getIntent().getExtras().getString("tipo_usuario","comun");
        Intent intent;
        if (tipo_usuario.equals("gerente")){
            intent = new Intent(ListadoTareasActivity.this, InicioActivity.class);
        }
        else {
            intent = new Intent(ListadoTareasActivity.this, MuestreoPendientesActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationClicked = new
    BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint({"RestrictedApi", "NonConstantResourceId"})
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()){
                case R.id.nav_propias:
                    fragment = new FragmentPropios();
                    BottomNavigationItemView item1 = findViewById(R.id.nav_propias);
                    item1.setEnabled(false);
                    BottomNavigationItemView item2 = findViewById(R.id.nav_grupal);
                    item2.setEnabled(true);
                    break;
                case R.id.nav_grupal:
                    fragment = new FragmentGrupal();
                    BottomNavigationItemView item3 = findViewById(R.id.nav_propias);
                    item3.setEnabled(true);
                    BottomNavigationItemView item4 = findViewById(R.id.nav_grupal);
                    item4.setEnabled(false);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, Objects.requireNonNull(fragment)).commit();
            return true;
        }
    };
}