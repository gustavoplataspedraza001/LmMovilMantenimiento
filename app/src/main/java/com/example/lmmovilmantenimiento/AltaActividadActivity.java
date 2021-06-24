package com.example.lmmovilmantenimiento;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.example.lmmovilmantenimiento.Clases.MySingleton;
import com.example.lmmovilmantenimiento.Clases.constantes;
import com.example.lmmovilmantenimiento.Modelos.ModeloAltaCorrectivo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

public class AltaActividadActivity extends AppCompatActivity {

    private final ArrayList<String> arrayAreas = new ArrayList<>();
    private final ArrayList<String> arrayEquipo = new ArrayList<>();
    private final ArrayList<String> arraySucursales = new ArrayList<>();
    private String pathImagen ="",pathVideo ="",pathAudio ="",evidenciaAudio = "",evidenciaImagen = "", evidenciaVideo ="",
    fecha = "fecha",fechaInicio = "fecha", hora = "hora";
    private static final int PERMISOS_CAMARA = 120;
    private static final int PETICION_CAMARA_CODIGO = 1,VIDEO_TOMAR_CAMARA = 101,VIDEO_PERMISOS = 102;

    private final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAD89wScc:APA91bFvA5kfgDqnf2A6KJ7oAIYjcsneAyEVSUSlapvwepGb43Ts7zK0f0D37JiD0pgyFFyz_gO_a16EP6FR_PJxz8ch4-ThtbIhVx_VIKfcep62HcUqvl12TsXBiOglKFymR-dBF2eB";
    final private String contentType = "application/json";

    private String[] permisosVideo;
    private Uri uriVideo;

    private ImageButton btnAgregarEvidenciaFoto, btnAgregarEvidenciaVideo, btnAgregarEvidenciaAudio,ibRegresar;
    private Button btnFinalizarTarea;
    //, btnFechaHoraTermino, btnFechaInicio;
    private AutoCompleteTextView spnAreas, spnEquipos;
    private TextInputEditText txtFechaInicio,txtFechaFinal;
    private TextInputLayout txtFechaInicioDetectado,txtFechaFinalDetectado;
    private EditText etDescripcion;
    private AutoCompleteTextView spnSucursales;
    private ConstraintLayout clAltaDetectado;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference referenciaAltaCorrectivos;
    private DatabaseReference referenciaUsuarios;
    private StorageReference referenciaAlmacenaje;

    private MediaRecorder mediaRecorder;
    @SuppressLint("SimpleDateFormat")
    private final String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    private final String archivo = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + timeStamp+"_"+UUID.randomUUID()+".3gp";

    private DatePickerDialog mDialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_actividad);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        permisosAudioS();
        init();
        cargarSpinner();
        listeners();
    }

    private void cargarSpinner() {
        DatabaseReference referenciaAreas = db.getReference("Areas");
        referenciaAreas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int i = 0;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        arrayAreas.add(i, snapshot1.getKey());

                        i += 1;
                    }
                    spnAreas.setText(arrayAreas.get(0));
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AltaActividadActivity.this, R.layout.support_simple_spinner_dropdown_item, arrayAreas);
                    spnAreas.setAdapter(arrayAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(AltaActividadActivity.this,"Error: " + error.getCode(),Toast.LENGTH_LONG).show();
            }
        });
        DatabaseReference referenciaSucursales = db.getReference("Sucursale");
        referenciaSucursales.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int i = 0;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        arraySucursales.add(i,snapshot1.getKey());
                        i+=1;
                    }
                    spnSucursales.setText(constantes.NOMBRE_SUCURSAL);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AltaActividadActivity.this,R.layout.support_simple_spinner_dropdown_item,arraySucursales);
                    spnSucursales.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(AltaActividadActivity.this,"Error: " + error.getCode(),Toasty.LENGTH_LONG).show();
            }
        });
        DatabaseReference referenciaEquipo = db.getReference("Equipos");
        referenciaEquipo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int i = 1;
                    arrayEquipo.add(i-1,"");
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        arrayEquipo.add(i, snapshot1.getKey());
                        i += 1;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AltaActividadActivity.this, R.layout.support_simple_spinner_dropdown_item, arrayEquipo);
                    spnEquipos.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(AltaActividadActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void permisosAudioS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
    }

    private void permisosAudio() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }else{
            dialogoAudio();
        }
    }

    private void listeners() {
        btnFinalizarTarea.setOnClickListener(view ->{
            if (etDescripcion.getText().toString().equals("")){
                etDescripcion.setError("Campo obligatorio");
                Toasty.warning(AltaActividadActivity.this, "El campo de descripción es obligatorio", Toasty.LENGTH_SHORT).show();
            }else {
                if (fecha.equals("fecha") || hora.equals("hora") || txtFechaFinal.getText().toString().equals("*Fecha/Hora Termino")){
                    Toasty.error(AltaActividadActivity.this,"Seleccione fecha y hora",Toasty.LENGTH_SHORT).show();
                }else {
                    if (fechaInicio.equals("fecha")){
                        Toasty.error(AltaActividadActivity.this,"Seleccione fecha de inicio", Toasty.LENGTH_LONG).show();
                    }else {
                        if (validarConexion()) {
                            btnFinalizarTarea.setEnabled(false);
                            referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        referenciaUsuarios.child(Objects.requireNonNull(snapshot1.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (Objects.requireNonNull(snapshot.child("uid").getValue()).toString().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())) {
                                                    String key = snapshot.getKey();
                                                    String nombre = Objects.requireNonNull(snapshot.child("nombre").getValue()).toString();
                                                    referenciaAltaCorrectivos.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                                            ModeloAltaCorrectivo modeloAltaCorrectivo = new ModeloAltaCorrectivo("CORRECTIVO",""+spnEquipos.getText().toString(), key, nombre, "terminada", spnSucursales.getText().toString(), "" + snapshot1.getKey(), Objects.requireNonNull(snapshot1.child("nombre").getValue()).toString(), "", evidenciaImagen, evidenciaVideo, evidenciaAudio,
                                                                    etDescripcion.getText().toString(), "" + timeStamp, "" + fecha, "" + hora, spnAreas.getText().toString());
                                                            referenciaAltaCorrectivos.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    String idGenerado = referenciaAltaCorrectivos.push().getKey();
                                                                    referenciaAltaCorrectivos.child(Objects.requireNonNull(idGenerado)).setValue(modeloAltaCorrectivo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            referenciaAltaCorrectivos.child(idGenerado).child("fecha_inicio").setValue(fechaInicio).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    if (task.isSuccessful()) {
                                                                                        ProgressDialog dialog = new ProgressDialog(AltaActividadActivity.this);
                                                                                        dialog.setMessage("Espere porfavor");
                                                                                        dialog.setCancelable(false);
                                                                                        dialog.show();
                                                                                        if (pathAudio.equals("") && pathVideo.equals("") && pathImagen.equals("")) {
                                                                                            dialog.dismiss();
                                                                                            String sucursalTk = "";
                                                                                            if (spnSucursales.getText().toString().equals("Díaz Ordaz")) {
                                                                                                sucursalTk = "DO";
                                                                                            } else if (spnSucursales.getText().toString().equals("Arboledas")) {
                                                                                                sucursalTk = "AR";
                                                                                            } else if (spnSucursales.getText().toString().equals("Allende")) {
                                                                                                sucursalTk = "ALL";
                                                                                            } else if (spnSucursales.getText().toString().equals("Villegas")) {
                                                                                                sucursalTk = "VLL";
                                                                                            } else if (spnSucursales.getText().toString().equals("Petaca")) {
                                                                                                sucursalTk = "PTC";
                                                                                            }
                                                                                            cargarNoti(sucursalTk, "Detectado realizado por" + nombre, "Correctivo");
                                                                                            terminarModulo();
                                                                                        }
                                                                                        else {
                                                                                            for (int i = 1; i <= 3; i++) {
                                                                                                if (pathImagen != "" && i == 1) {
                                                                                                    File file = new File(pathImagen);
                                                                                                    Uri contentUri = Uri.fromFile(file);
                                                                                                    final StorageReference reference = referenciaAlmacenaje.child("CorrectivosImagenes/" + file.getName());
                                                                                                    reference.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                                                @Override
                                                                                                                public void onSuccess(Uri uri) {
                                                                                                                    evidenciaImagen = uri.toString();
                                                                                                                    referenciaAltaCorrectivos.child(idGenerado).child("evidenciaImagen").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                evidenciaImagen = "";
                                                                                                                                pathImagen = "";
                                                                                                                                if (evidenciaAudio.equals("") && pathAudio.equals("") && pathVideo.equals("") && evidenciaVideo.equals("")) {
                                                                                                                                    dialog.dismiss();
                                                                                                                                    terminarModulo();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    });
                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                                else if (pathVideo != "" && i == 2) {
                                                                                                    File file = new File(pathVideo);
                                                                                                    final StorageReference reference = referenciaAlmacenaje.child("CorrectivosVideos/" + file.getName());
                                                                                                    reference.putFile(uriVideo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                                                @Override
                                                                                                                public void onSuccess(Uri uri) {
                                                                                                                    evidenciaVideo = uri.toString();
                                                                                                                    referenciaAltaCorrectivos.child(idGenerado).child("evidenciaVideo").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    evidenciaVideo = "";
                                                                                                                                    pathVideo = "";
                                                                                                                                    if (evidenciaAudio.equals("") && evidenciaImagen.equals("")
                                                                                                                                            && pathAudio.equals("") && pathImagen.equals("")) {
                                                                                                                                        dialog.dismiss();
                                                                                                                                        terminarModulo();
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    });
                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    });
                                                                                                } else if (pathAudio != "" && i == 3) {
                                                                                                    File file = new File(archivo);
                                                                                                    Uri contentUri = Uri.fromFile(file);
                                                                                                    final StorageReference reference = referenciaAlmacenaje.child("CorrectivosAudios/" + file.getName());
                                                                                                    reference.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                                                @Override
                                                                                                                public void onSuccess(Uri uri) {
                                                                                                                    evidenciaAudio = uri.toString();
                                                                                                                    referenciaAltaCorrectivos.child(idGenerado).child("evidenciaAudio").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                evidenciaAudio = "";
                                                                                                                                pathAudio = "";
                                                                                                                                if (evidenciaImagen.equals("") && evidenciaVideo.equals("")
                                                                                                                                        && pathImagen.equals("") && pathVideo.equals("")) {
                                                                                                                                    dialog.dismiss();
                                                                                                                                    terminarModulo();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    });
                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    });

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    Toasty.error(AltaActividadActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Toasty.error(AltaActividadActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toasty.error(AltaActividadActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toasty.error(AltaActividadActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Intent intent = new Intent(this, ActivityNoInternet.class);
                            intent.putExtra("origen", "altaCorrectivos");
                            startActivity(intent);
                            finish();
                            Animatoo.animateSlideLeft(this);
                        }
                    }
                }
            }
        });

        btnAgregarEvidenciaFoto.setOnClickListener(view ->{
            solicitarPermisos();

        });

        btnAgregarEvidenciaAudio.setOnClickListener(view ->{
            permisosAudio();
        });

        btnAgregarEvidenciaVideo.setOnClickListener(view ->{
            if (!verificarPermisosCamara()){
                permisosCamaraVideo();
            }else{
                videoPickCamara();
            }
        });

        ibRegresar.setOnClickListener(view ->{
            regresar();
        });

        txtFechaFinal.setOnClickListener(view ->{
            Calendar c = Calendar.getInstance();
            int dia = c.get(Calendar.DAY_OF_MONTH);
            int mes = c.get(Calendar.MONTH );
            int anio = c.get(Calendar.YEAR);
            mDialogo = new DatePickerDialog(AltaActividadActivity.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    fecha = dayOfMonth+"-"+(month+1)+"-"+year;
                    LayoutInflater inflater = AltaActividadActivity.this.getLayoutInflater();
                    View vista = inflater.inflate(R.layout.dialog_tiempo_realizado,null);
                    Button btnCerrarDialogo = vista.findViewById(R.id.btnCancelarDialogo),
                    btnAceptarDialogo = vista.findViewById(R.id.btnAceptarDialogo);
                    TextInputEditText etHorasRealizado = vista.findViewById(R.id.etHorasRealizado),
                    etMinutosRealizado = vista.findViewById(R.id.etMinutosRealizado);
                    AlertDialog.Builder builder = new AlertDialog.Builder(AltaActividadActivity.this);
                    builder.setCancelable(true);
                    builder.setView(vista);
                    AlertDialog dialog = builder.create();
                    btnCerrarDialogo.setOnClickListener(view2 ->{
                        dialog.dismiss();
                    });
                    btnAceptarDialogo.setOnClickListener(view22 ->{
                        if (etHorasRealizado.getText().toString().equals("") || etMinutosRealizado.getText().toString().equals("")){
                            Toasty.warning(AltaActividadActivity.this,"Algún campo está vacío",Toasty.LENGTH_SHORT).show();
                        }else {
                            if (etHorasRealizado.length() == 1) {
                                if (etMinutosRealizado.length() == 1 && !etMinutosRealizado.getText().toString().equals("0")) {
                                    dialog.dismiss();
                                    hora = "0"+etHorasRealizado.getText().toString() + ":" + "0"+etMinutosRealizado.getText().toString();
                                    txtFechaFinal.setText(dayOfMonth + "-" + (month + 1) + "-" + year + "/" + hora);
                                }else{
                                    dialog.dismiss();
                                    hora = "0"+etHorasRealizado.getText().toString() + ":" + etMinutosRealizado.getText().toString();
                                    txtFechaFinal.setText(dayOfMonth + "-" + (month + 1) + "-" + year + "/" + hora);
                                }
                            }else{
                                dialog.dismiss();
                                hora = etHorasRealizado.getText().toString() + ":" + etMinutosRealizado.getText().toString();
                                txtFechaFinal.setText(dayOfMonth + "-" + (month + 1) + "-" + year + "/" + hora);
                            }
                        }
                    });
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }
                    dialog.show();
                }
            },anio,mes,dia);
            mDialogo.setTitle("Fecha de finalización");
            mDialogo.show();
        });

        txtFechaInicio.setOnClickListener(view ->{
            Calendar c = Calendar.getInstance();
            int dia = c.get(Calendar.DAY_OF_MONTH);
            int mes = c.get(Calendar.MONTH );
            int anio = c.get(Calendar.YEAR);
            mDialogo = new DatePickerDialog(AltaActividadActivity.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    fechaInicio = dayOfMonth+"-"+(month+1)+"-"+year;
                    txtFechaInicio.setText(dayOfMonth+"-"+(month+1)+"-"+year);
                }
            },anio,mes,dia);
            mDialogo.setTitle("Fecha de Inicio");
            mDialogo.show();
        });

        clAltaDetectado.setOnClickListener(view2 ->{
            View view = getCurrentFocus();
            if (view != null){
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        });
    }

    private void regresar() {
        Intent intent = new Intent(AltaActividadActivity.this,InicioActivity.class);
        startActivity(intent);
        finish();
    }

    private void cargarNoti(String tipo, String titulo, String mensaje) {
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", titulo);
            notifcationBody.put("message", mensaje);
            notifcationBody.put("tipo", "CORRECTIVO");
            notifcationBody.put("permiso","GERENTE");

            notification.put("to", "/topics/"+tipo);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Toasty.error(AltaActividadActivity.this, "Error: " + e.getMessage(), Toasty.LENGTH_SHORT).show();
        }
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toasty.error(AltaActividadActivity.this, "Request error " + error.getMessage(), Toasty.LENGTH_LONG).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(AltaActividadActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        regresar();
    }

    private void dialogoAudio() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(archivo);
        LayoutInflater inflater = AltaActividadActivity.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_grabar_audio,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ImageButton ibGrabar = view.findViewById(R.id.ibGrabar);
        ImageButton ibDetener = view.findViewById(R.id.ibDetener);
        ImageButton ibReproducir = view.findViewById(R.id.ibReproducir);
        Button btnAceptarAudio = view.findViewById(R.id.btnAceptarAudio),
        btnCancelarAudio = view.findViewById(R.id.btnCancelarAudio);
        ibGrabar.setEnabled(true);
        ibDetener.setEnabled(false);
        ibReproducir.setEnabled(false);
        ibGrabar.setOnClickListener(view2 ->{
            grabar();
            ibGrabar.setVisibility(View.GONE);
            ibDetener.setEnabled(true);
            ibReproducir.setEnabled(true);
        });
        ibDetener.setOnClickListener(view3 ->{
            detener();
            ibDetener.setVisibility(View.GONE);
        });
        ibReproducir.setOnClickListener(view4 ->{
            reproducir();
        });
        builder.setCancelable(true);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        btnAceptarAudio.setOnClickListener(viewAudio ->{
            dialog.dismiss();
        });
        btnCancelarAudio.setOnClickListener(viewAudio ->{
            pathAudio = "";
            evidenciaAudio = "";
            dialog.dismiss();
        });
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }

    private void grabar() {
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toasty.info(this, "Grabando audio", Toasty.LENGTH_SHORT).show();
    }

    private void detener() {
        mediaRecorder.stop();
        mediaRecorder.release();
        File f = new File(archivo);
        pathAudio = f.getName();
        Toasty.info(this, "Grabación detenida", Toasty.LENGTH_SHORT).show();
    }

    private void reproducir() {
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(archivo);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toasty.info(this, "Reproduciendo audio", Toasty.LENGTH_SHORT).show();

    }

    private void solicitarPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},PERMISOS_CAMARA);
        }else{
            btnFinalizarTarea.setEnabled(false);
            intentoImgDP();

        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void intentoImgDP(){
        Intent tomarFotoInent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tomarFotoInent.resolveActivity(getPackageManager())!= null){
            File photoFile = null;
            try {
                photoFile = miArchivoEvidencia();
            }catch (IOException e){
                e.printStackTrace();
            }
            if (photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(null,
                        "com.example.lmmovilmantenimiento.fileprovider",
                        photoFile);
                tomarFotoInent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(tomarFotoInent,PETICION_CAMARA_CODIGO);
            }
        }
    }

    private File miArchivoEvidencia() throws IOException{
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreArchivoImagen = "JPEG_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                nombreArchivoImagen,
                ".jpg",
                storageDir
        );
        pathImagen = image.getAbsolutePath();
        return  image;
    }

    private void init() {
        btnAgregarEvidenciaFoto = findViewById(R.id.btnAgregarEvidenciaFoto);
        btnAgregarEvidenciaAudio = findViewById(R.id.btnAgregarEvidenciaAudio);
        btnAgregarEvidenciaVideo = findViewById(R.id.btnAgregarEvidenciaVideo);
        ibRegresar = findViewById(R.id.ibRegresar);
        btnFinalizarTarea = findViewById(R.id.btnFinalizarAlta);
        //btnFechaHoraTermino = findViewById(R.id.btnFechaHoraTermino);
        clAltaDetectado = findViewById(R.id.clAltaDetectado);
        spnEquipos = findViewById(R.id.spnEquipos);
        spnAreas = findViewById(R.id.spnAreas);
        txtFechaInicio = findViewById(R.id.txtFechaInicioDetectado);
        txtFechaFinal = findViewById(R.id.txtFechaFinalDetectado);
        txtFechaInicioDetectado = findViewById(R.id.txtInpFechaInicioDetectado);
        txtFechaFinalDetectado = findViewById(R.id.txtInpFechaFinalDetectado);
        txtFechaInicio.setInputType(InputType.TYPE_NULL);
        txtFechaFinal.setInputType(InputType.TYPE_NULL);
        spnEquipos.setInputType(InputType.TYPE_NULL);
        spnAreas.setInputType(InputType.TYPE_NULL);
        etDescripcion = findViewById(R.id.etDescripcion);
        spnSucursales = findViewById(R.id.spnSucursales);
        spnSucursales.setInputType(InputType.TYPE_NULL);
        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        referenciaUsuarios = db.getReference("UsuariosLm");
        referenciaAltaCorrectivos = db.getReference("Actividades");
        referenciaAlmacenaje = FirebaseStorage.getInstance().getReference();
        permisosVideo = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //btnFechaInicio = findViewById(R.id.btnFechaInicio);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISOS_CAMARA){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                intentoImgDP();
            }else{
                Toasty.warning(this, "La aplicación requiere los permisos de camara para realizar esta acción", Toasty.LENGTH_SHORT).show();
            }
        }
        if (requestCode == VIDEO_PERMISOS){
            if (grantResults.length > 0){
                boolean camaraAceptada = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean almacenamientoAceptado = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (camaraAceptada && almacenamientoAceptado){
                    videoPickCamara();
                }else{
                    Toasty.warning(this, "Permisos concedidos de camara y almacenamiento son necesarios", Toasty.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PETICION_CAMARA_CODIGO){
            if (resultCode == RESULT_OK ){
                Toasty.info(this, "Evidencia tomada", Toasty.LENGTH_SHORT).show();
                btnFinalizarTarea.setEnabled(true);
            }
        }
        if (requestCode == VIDEO_TOMAR_CAMARA){
            if (resultCode == RESULT_OK && null != data){
                uriVideo = data.getData();
                pathVideo = data.getData().toString();
                btnFinalizarTarea.setEnabled(true);
            }else if (resultCode == RESULT_CANCELED){

            }else{
                Toasty.error(this, "Error ocurrido al cargar video", Toasty.LENGTH_SHORT).show();
            }
        }
    }

    private void permisosCamaraVideo(){
        ActivityCompat.requestPermissions(this,permisosVideo,VIDEO_PERMISOS);
    }

    private boolean verificarPermisosCamara(){
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;
        return result1 && result2;
    }

    private void videoPickCamara(){
        btnFinalizarTarea.setEnabled(false);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent,VIDEO_TOMAR_CAMARA);
    }

    private boolean validarConexion() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo internetActivo = cm.getActiveNetworkInfo();
        if (internetActivo == null){
            return false;
        }else{
            return true;
        }
    }

    private void terminarModulo() {
        Intent intent = new Intent(AltaActividadActivity.this, AltaActividadActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }
}