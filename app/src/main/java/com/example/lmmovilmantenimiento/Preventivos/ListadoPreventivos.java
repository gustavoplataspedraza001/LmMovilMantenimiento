package com.example.lmmovilmantenimiento.Preventivos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.lmmovilmantenimiento.Clases.constantes;
import com.example.lmmovilmantenimiento.InicioActivity;
import com.example.lmmovilmantenimiento.R;
import com.example.lmmovilmantenimiento.fragments.FragmentPreventivos.PropiosPreventivosFragment;
import com.example.lmmovilmantenimiento.fragments.FragmentPreventivos.TodosPreventivosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ListadoPreventivos extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private String verficar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_preventivos);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    private void init() {
        bottomNavigationView = findViewById(R.id.btmNAvigationViewPreventivos);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationClicked);
        constantes.SUCURSAL_ELEGIDA = getIntent().getExtras().getString("sucursal");
        constantes.TIPO_USUARIO = getIntent().getExtras().getString("tipo_usuario","comun");
        verficar = getIntent().getExtras().getString("tipo_origen","");
        if (verficar.equals("propios")){
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_preventivos, new PropiosPreventivosFragment()).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_preventivos, new TodosPreventivosFragment()).commit();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        regresar();
    }

    private void regresar() {
        String tipo_usuario = getIntent().getExtras().getString("tipo_usuario","comun");
        if (tipo_usuario.equals("gerente")){
            Intent intent = new Intent(ListadoPreventivos.this, InicioActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(ListadoPreventivos.this, MuestreoSucursales.class);
            intent.putExtra("origen",constantes.ORIGEN);
            startActivity(intent);
            finish();
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationClicked = new
    BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint({"RestrictedApi", "NonConstantResourceId"})
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()){
                case R.id.nav_propias_preventivos:
                    fragment = new PropiosPreventivosFragment();
                    BottomNavigationItemView item1 = (BottomNavigationItemView) findViewById(R.id.nav_propias_preventivos);
                    item1.setEnabled(false);
                    BottomNavigationItemView item2 = (BottomNavigationItemView) findViewById(R.id.nav_grupal_preventivos);
                    item2.setEnabled(true);
                    break;
                case R.id.nav_grupal_preventivos:
                    fragment = new TodosPreventivosFragment();
                    BottomNavigationItemView item3 = (BottomNavigationItemView) findViewById(R.id.nav_propias_preventivos);
                    item3.setEnabled(true);
                    BottomNavigationItemView item4 = (BottomNavigationItemView) findViewById(R.id.nav_grupal_preventivos);
                    item4.setEnabled(false);
                    break;
            }
            assert fragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_preventivos,fragment).commit();
            return true;
        }
    };
}