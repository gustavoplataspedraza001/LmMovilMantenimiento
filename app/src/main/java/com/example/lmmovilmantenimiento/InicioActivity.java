package com.example.lmmovilmantenimiento;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.lmmovilmantenimiento.Clases.constantes;
import com.example.lmmovilmantenimiento.Preventivos.ListadoPreventivos;
import com.example.lmmovilmantenimiento.Preventivos.MuestreoSucursales;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class InicioActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference referenciaUsuario;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        validarConexion();
        permisos();
        init();
        validarTipoUsuario();
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        ImageView imageView = view.findViewById(R.id.imgPerfil);
        TextView txtNombreUser = view.findViewById(R.id.txtNombreUser),
        txtCorreoUser = view.findViewById(R.id.txtCorreoUser);
        cargarTipoUsuario(imageView,txtNombreUser,txtCorreoUser);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        if (getIntent().getExtras() != null) {
            String redir = getIntent().getExtras().getString("redir");
            if (redir.equals("CORRECTIVO_GERENTE")) {
                Intent intent = new Intent(InicioActivity.this, MuestreoPendientesActivity.class);
                if (loading.isShowing()){
                loading.dismiss();
                }
                startActivity(intent);
            }
            else if (redir.equals("CORRECTIVO_USUARIO")){
                Intent intent = new Intent(InicioActivity.this, ListadoTareasActivity.class);
                intent.putExtra("sucursal", getIntent().getExtras().getString("sucursal"));
                intent.putExtra("tipo_usuario","normal");
                if (loading.isShowing()){
                    loading.dismiss();
                }
                startActivity(intent);
            }
            else if (redir.equals("CORRECTIVO_ALTA")){
                referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            DatabaseReference referenciaPerfiles = db.getReference("Perfiles");
                            referenciaPerfiles.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                        if (Objects.requireNonNull(auth.getCurrentUser()).getUid().equals(Objects.requireNonNull(snapshot1.child("uid").getValue()).toString())) {
                                            if (Objects.requireNonNull(snapshot1.child("id_perfil").getValue()).toString().equals(snapshot2.getKey())) {
                                                if (Objects.requireNonNull(snapshot2.child("descripcion").getValue()).toString().equals("gerente")) {
                                                    Intent intent = new Intent(InicioActivity.this, MuestreoPendientesActivity.class);
                                                    if (loading.isShowing()){
                                                        loading.dismiss();
                                                    }
                                                    startActivity(intent);
                                                } else {
                                                    Intent intent = new Intent(InicioActivity.this, ListadoTareasActivity.class);
                                                    intent.putExtra("sucursal", getIntent().getExtras().getString("sucursal"));
                                                    intent.putExtra("tipo_usuario", Objects.requireNonNull(snapshot2.child("descripcion").getValue()).toString());
                                                    if (loading.isShowing()) {
                                                        loading.dismiss();
                                                    }
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toasty.error(InicioActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toasty.error(InicioActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                    }
                });
            }
            else if (redir.equals("PREVENTIVO_ALTA")){
                referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            DatabaseReference referenciaPerfiles = db.getReference("Perfiles");
                            referenciaPerfiles.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                        if (Objects.requireNonNull(auth.getCurrentUser()).getUid().equals(Objects.requireNonNull(snapshot1.child("uid").getValue()).toString())) {
                                            if (Objects.requireNonNull(snapshot1.child("id_perfil").getValue()).toString().equals(snapshot2.getKey())) {
                                                if (Objects.requireNonNull(snapshot2.child("descripcion").getValue()).toString().equals("gerente")){
                                                    Intent intent = new Intent(InicioActivity.this, MuestreoSucursales.class);
                                                    if (loading.isShowing()){
                                                        loading.dismiss();
                                                    }
                                                    startActivity(intent);
                                                }else{
                                                    Intent intent = new Intent(InicioActivity.this, ListadoPreventivos.class);
                                                    intent.putExtra("sucursal",getIntent().getExtras().getString("sucursal"));
                                                    intent.putExtra("tipo_usuario",getIntent().getExtras().getString("permiso"));
                                                    if (loading.isShowing()){
                                                        loading.dismiss();
                                                    }
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toasty.error(InicioActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toasty.error(InicioActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                    }
                });
            }
            else if (redir.equals("PREVENTIVO_GERENTE")){
                Intent intent = new Intent(InicioActivity.this, MuestreoSucursales.class);
                if (loading.isShowing()){
                    loading.dismiss();
                }
                startActivity(intent);
            }
            else if (redir.equals("PREVENTIVO_USUARIO")){
                Intent intent = new Intent(InicioActivity.this, ListadoPreventivos.class);
                intent.putExtra("sucursal", getIntent().getExtras().getString("sucursal"));
                intent.putExtra("tipo_usuario","normal");
                if (loading.isShowing()){
                    loading.dismiss();
                }
                startActivity(intent);
            }
        }
    }

    private void cargarTipoUsuario(ImageView imageView, TextView txtNombreUser, TextView txtCorreoUser) {
        referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (Objects.requireNonNull(snapshot1.child("uid").getValue()).toString().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())){
                        DatabaseReference referenciaPerfil = db.getReference("Perfiles");
                        referenciaPerfil.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                    if (Objects.equals(snapshot2.getKey(), Objects.requireNonNull(snapshot1.child("id_perfil").getValue()).toString())) {
                                        cargarInfoUsuario(snapshot1,snapshot2,imageView,txtCorreoUser,txtNombreUser);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toasty.error(InicioActivity.this, "Error: "+ error.getCode(), Toasty.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(InicioActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void cargarInfoUsuario(DataSnapshot snapshot1, DataSnapshot snapshot2, ImageView imageView, TextView txtCorreoUser, TextView txtNombreUser) {
        if (Objects.requireNonNull(snapshot2.child("descripcion").getValue()).toString().equals("admin")){
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.casco));
            txtCorreoUser.setText(Objects.requireNonNull(snapshot1.child("correo").getValue()).toString());
            txtNombreUser.setText(Objects.requireNonNull(snapshot1.child("nombre").getValue()).toString());
        }else if (Objects.requireNonNull(snapshot2.child("descripcion").getValue()).toString().equals("comun")){
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.mantenimiento));
            txtCorreoUser.setText(Objects.requireNonNull(snapshot1.child("correo").getValue()).toString());
            txtNombreUser.setText(Objects.requireNonNull(snapshot1.child("nombre").getValue()).toString());
        }else if (Objects.requireNonNull(snapshot2.child("descripcion").getValue()).toString().equals("gerente")){
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.gerente));
            txtCorreoUser.setText(Objects.requireNonNull(snapshot1.child("correo").getValue()).toString());
            txtNombreUser.setText(Objects.requireNonNull(snapshot1.child("nombre").getValue()).toString());
        }
    }

    private void validarTipoUsuario() {
        FirebaseMessaging.getInstance().subscribeToTopic(Objects.requireNonNull(auth.getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseMessaging.getInstance().subscribeToTopic("200").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    if (Objects.requireNonNull(snapshot1.child("uid").getValue()).toString().equals(auth.getCurrentUser().getUid())) {
                                        DatabaseReference referenciaPerfil = db.getReference("Perfiles");
                                        referenciaPerfil.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                                    if (Objects.equals(snapshot2.getKey(), Objects.requireNonNull(snapshot1.child("id_perfil").getValue()).toString())){
                                                        if (Objects.requireNonNull(snapshot2.child("descripcion").getValue()).toString().equals("gerente")){
                                                            String sucursal = "";
                                                            if (Objects.requireNonNull(snapshot1.child("sucursal").getValue()).toString().equals("Díaz Ordaz")){
                                                                sucursal = "DO";
                                                            }else if (Objects.requireNonNull(snapshot1.child("sucursal").getValue()).toString().equals("Arboledas")){
                                                                sucursal = "AR";
                                                            }else if (Objects.requireNonNull(snapshot1.child("sucursal").getValue()).toString().equals("Allende"))
                                                                sucursal = "ALL";
                                                            else if (Objects.requireNonNull(snapshot1.child("sucursal").getValue()).toString().equals("Villegas")){
                                                                sucursal = "VLL";
                                                            }else if (Objects.requireNonNull(snapshot.child("sucursal").getValue()).toString().equals("Petaca")){
                                                                sucursal = "PTC";
                                                            }
                                                            FirebaseMessaging.getInstance().subscribeToTopic(sucursal).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toasty.error(InicioActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toasty.error(InicioActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    private void init() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        referenciaUsuario = db.getReference("UsuariosLm");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void cerrarSesion(MenuItem item) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(Objects.requireNonNull(auth.getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("200").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    if (Objects.requireNonNull(snapshot1.child("uid").getValue()).toString().equals(auth.getCurrentUser().getUid())) {
                                        DatabaseReference referenciaPerfil = db.getReference("Perfiles");
                                        referenciaPerfil.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                                    if (Objects.equals(snapshot2.getKey(), snapshot1.child("id_perfil").getValue().toString())) {
                                                        if (Objects.requireNonNull(snapshot2.child("descripcion").getValue()).toString().equals("gerente")) {
                                                            String sucursal = "";
                                                            if (Objects.requireNonNull(snapshot1.child("sucursal").getValue()).toString().equals("Díaz Ordaz")) {
                                                                sucursal = "DO";
                                                            } else if (Objects.requireNonNull(snapshot1.child("sucursal").getValue()).toString().equals("Arboledas")) {
                                                                sucursal = "AR";
                                                            } else if (Objects.requireNonNull(snapshot1.child("sucursal").getValue()).toString().equals("Allende")) {
                                                                sucursal = "ALL";
                                                            } else if (Objects.requireNonNull(snapshot1.child("sucursal").getValue()).toString().equals("Villegas")) {
                                                                sucursal = "VLL";
                                                            } else if (Objects.requireNonNull(snapshot.child("sucursal").getValue()).toString().equals("Petaca")) {
                                                                sucursal = "PTC";
                                                            }
                                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(sucursal).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                }
                                                            });
                                                        }
                                                    }

                                                }
                                                auth = FirebaseAuth.getInstance();
                                                auth.signOut();
                                                Intent intent = new Intent(InicioActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toasty.error(InicioActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toasty.error(InicioActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void validarConexion() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo internetActivo = cm.getActiveNetworkInfo();
        if (internetActivo == null) {
            Intent intent = new Intent(InicioActivity.this, ActivityNoInternet.class);
            intent.putExtra("origen", "principal");
            startActivity(intent);
            finish();
            Animatoo.animateSlideLeft(InicioActivity.this);
        }
    }

    private void permisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    1000);
            return;
        } else {
            validarStatus();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && grantResults[0] >= 0) {
            validarStatus();
        } else if (requestCode == 1000 && grantResults[0] < 0) {
            permisos();
        }
    }

    private void validarStatus() {
        LocationManager locationManager = (LocationManager) InicioActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean status2 = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (status || status2) {
            obtener_ubicacion();
        } else {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    /**Cambiar al entregar avances**/
    private void obtener_ubicacion() {
        loading = ProgressDialog.show(this, "Obteniendo su ubicación...", "Espere un momento", true, false);
        if (!constantes.LATITUD.equals("") && !constantes.LONGITUD.equals("")) {
            loading.dismiss();
        }
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String latitud = String.valueOf(location.getLatitude());
                String longitud = String.valueOf(location.getLongitude());
                if (latitud != "" && longitud != "") {
                    loading.dismiss();
                    constantes.LATITUD = latitud;
                    constantes.LONGITUD = longitud;
                    locationManager.removeUpdates(this);
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(@NonNull String provider) {
                obtener_ubicacion();
            }
            @Override
            public void onProviderDisabled(@NonNull String provider) {
                validarStatus();
            }
        };
        /**
         * aunque los permisos no estén definidos o mas bien sus variables no se deben borrar se siguen activando pese a no estar
         * usandose las variables**/
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permiso2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        boolean status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean status2 = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean status3 = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

        if (status) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else if (status2) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            validarStatus();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        permisos();
    }
}