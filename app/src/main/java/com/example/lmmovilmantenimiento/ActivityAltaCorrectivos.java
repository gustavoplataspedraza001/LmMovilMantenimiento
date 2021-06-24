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
import android.app.ProgressDialog;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.lmmovilmantenimiento.Clases.MySingleton;
import com.example.lmmovilmantenimiento.Clases.constantes;
import com.example.lmmovilmantenimiento.Modelos.ModeloAltaCorrectivo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import es.dmoral.toasty.Toasty;


public class ActivityAltaCorrectivos extends AppCompatActivity {

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAD89wScc:APA91bFvA5kfgDqnf2A6KJ7oAIYjcsneAyEVSUSlapvwepGb43Ts7zK0f0D37JiD0pgyFFyz_gO_a16EP6FR_PJxz8ch4-ThtbIhVx_VIKfcep62HcUqvl12TsXBiOglKFymR-dBF2eB";
    final private String contentType = "application/json";

    String pathImagen ="",pathVideo ="",pathAudio ="",evidenciaAudio = "",evidenciaImagen = "", evidenciaVideo ="";
    int PERMISOS_CAMARA = 120;
    static final int PETICION_CAMARA_CODIGO = 1,VIDEO_TOMAR_CAMARA = 101,VIDEO_PERMISOS = 102;

    private String[] permisosVideo;
    private final ArrayList<String> arrayAreas = new ArrayList<>();
    private Uri uriVideo;

    ImageButton btnAgregarEvidenciaFoto, btnAgregarEvidenciaVideo, btnAgregarEvidenciaAudio,ibRegresar;
    Button btnFinalizarTarea;
    EditText etDescripcion;
    TextView txtSucursalUsuario;
    ConstraintLayout clAltaCorrectivos;
    private AutoCompleteTextView txtAreasCargar;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference referenciaAltaCorrectivos;
    DatabaseReference referenciaUsuarios;
    StorageReference referenciaAlmacenaje;

    private MediaRecorder mediaRecorder;
    @SuppressLint("SimpleDateFormat")
    String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    private final String archivo = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + timeStamp+"_"+UUID.randomUUID()+".3gp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_correctivos);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        permisosAudioS();
        init();
        cargarAreas();
        listeners();
    }

    private void cargarAreas() {
        DatabaseReference referenciaAreas = db.getReference("Areas");
        referenciaAreas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int i = 0;
                    /**preguntar si las areas pueden ir vacías**/
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        arrayAreas.add(i, snapshot1.getKey());
                        i += 1;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ActivityAltaCorrectivos.this, R.layout.support_simple_spinner_dropdown_item, arrayAreas);
                    txtAreasCargar.setAdapter(arrayAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(ActivityAltaCorrectivos.this,"Error: " + error.getCode(),Toast.LENGTH_LONG).show();
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
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }else{
            dialogoAudio();
        }
    }

    private void listeners() {
        btnFinalizarTarea.setOnClickListener(view ->{
            if (txtAreasCargar.getText().toString().equals("")){
                Toasty.error(this,"Se debe seleccionar una área", Toasty.LENGTH_LONG).show();
            }else {
                if (etDescripcion.getText().toString().equals("")) {
                    etDescripcion.setError("Campo obligatorio");
                    Toasty.error(this, "El campo de descripción es obligatorio", Toasty.LENGTH_SHORT).show();
                } else {
                    if (validarConexion()) {
                        btnFinalizarTarea.setEnabled(false);
                        darAltaCorrectivo();
                    } else {
                        Intent intent = new Intent(this, ActivityNoInternet.class);
                        intent.putExtra("origen", "altaCorrectivos");
                        startActivity(intent);
                        finish();
                        Animatoo.animateSlideLeft(this);
                    }
                }
            }
        });

        btnAgregarEvidenciaFoto.setOnClickListener(view -> solicitarPermisos());
        btnAgregarEvidenciaAudio.setOnClickListener(view -> permisosAudio());
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
        clAltaCorrectivos.setOnClickListener(view2 -> {
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    private void darAltaCorrectivo() {
        referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    segundoPasoCorrectivo(snapshot1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(ActivityAltaCorrectivos.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void segundoPasoCorrectivo(DataSnapshot snapshot1) {
        referenciaUsuarios.child(Objects.requireNonNull(snapshot1.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Objects.requireNonNull(snapshot.child("uid").getValue()).toString().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())) {
                    referenciaAltaCorrectivos.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                            ModeloAltaCorrectivo modeloAltaCorrectivo = new ModeloAltaCorrectivo("CORRECTIVO","","","","pendiente", txtSucursalUsuario.getText().toString(), ""+snapshot1.getKey(), Objects.requireNonNull(snapshot1.child("nombre").getValue()).toString(), "", evidenciaImagen, evidenciaVideo, evidenciaAudio,
                                    etDescripcion.getText().toString(), ""+timeStamp, "","", txtAreasCargar.getText().toString());
                            tercerPasoAltaCorrectivo(modeloAltaCorrectivo,snapshot1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toasty.error(ActivityAltaCorrectivos.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(ActivityAltaCorrectivos.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void tercerPasoAltaCorrectivo(ModeloAltaCorrectivo modeloAltaCorrectivo, DataSnapshot snapshot1) {
        referenciaAltaCorrectivos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String idDefinido = referenciaAltaCorrectivos.push().getKey();
                assert idDefinido != null;
                referenciaAltaCorrectivos.child(Objects.requireNonNull(idDefinido)).setValue(modeloAltaCorrectivo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ProgressDialog dialog = new ProgressDialog(ActivityAltaCorrectivos.this);
                            dialog.setMessage("Espere porfavor");
                            dialog.setCancelable(false);
                            dialog.show();
                            if (pathAudio.equals("") && pathVideo.equals("") && pathImagen.equals("")){
                                dialog.dismiss();
                                FirebaseMessaging.getInstance().unsubscribeFromTopic("200").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        cargarNoti("200","Nuevo Correctivo por " + Objects.requireNonNull(snapshot1.child("nombre").getValue()).toString(), etDescripcion.getText().toString());
                                    }
                                });
                                terminarModulo();
                            }else {
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
                                                        referenciaAltaCorrectivos.child(idDefinido).child("evidenciaImagen").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    evidenciaImagen = "";
                                                                    pathImagen = "";
                                                                    if (evidenciaAudio.equals("") && pathAudio.equals("") && pathVideo.equals("") && evidenciaVideo.equals("")) {
                                                                        dialog.dismiss();
                                                                        FirebaseMessaging.getInstance().unsubscribeFromTopic("200").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                cargarNoti("200","Nuevo Correctivo por " + Objects.requireNonNull(snapshot1.child("nombre").getValue()).toString(), etDescripcion.getText().toString());
                                                                            }
                                                                        });
                                                                        terminarModulo();
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    } else if (pathVideo != "" && i == 2) {
                                        File file = new File(pathVideo);
                                        final StorageReference reference = referenciaAlmacenaje.child("CorrectivosVideos/" + file.getName());
                                        reference.putFile(uriVideo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        evidenciaVideo = uri.toString();
                                                        referenciaAltaCorrectivos.child(idDefinido).child("evidenciaVideo").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    if (task.isSuccessful()) {
                                                                        evidenciaVideo = "";
                                                                        pathVideo = "";
                                                                        if (evidenciaAudio.equals("") && evidenciaImagen.equals("")
                                                                                && pathAudio.equals("") && pathImagen.equals("")) {
                                                                            dialog.dismiss();
                                                                            FirebaseMessaging.getInstance().unsubscribeFromTopic("200").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    cargarNoti("200","Nuevo Correctivo por " + Objects.requireNonNull(snapshot1.child("nombre").getValue()).toString(), etDescripcion.getText().toString());
                                                                                }
                                                                            });
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
                                                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                                    evidenciaAudio = uri.toString();
                                                    referenciaAltaCorrectivos.child(idDefinido).child("evidenciaAudio").setValue(uri.toString()).addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            evidenciaAudio = "";
                                                            pathAudio = "";
                                                            if (evidenciaImagen.equals("") && evidenciaVideo.equals("")
                                                                    && pathImagen.equals("") && pathVideo.equals("")) {
                                                                dialog.dismiss();
                                                                FirebaseMessaging.getInstance().unsubscribeFromTopic("200").addOnSuccessListener(aVoid -> cargarNoti("200","Nuevo Correctivo por " + Objects.requireNonNull(snapshot1.child("nombre").getValue()).toString(), etDescripcion.getText().toString()));
                                                                terminarModulo();
                                                            }
                                                        }
                                                    });
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(ActivityAltaCorrectivos.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void terminarModulo() {
        Intent intent = new Intent(ActivityAltaCorrectivos.this, ActivityAltaCorrectivos.class);
        startActivity(intent);
        finish();
    }

    private void cargarNoti(String tipo, String titulo, String mensaje) {
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", titulo);
            notifcationBody.put("message", mensaje);
            notifcationBody.put("tipo", "CORRECTIVO_ALTA");
            notifcationBody.put("sucursal", txtSucursalUsuario.getText().toString());

            notification.put("to", "/topics/"+tipo);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                response -> FirebaseMessaging.getInstance().subscribeToTopic("200").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toasty.info(ActivityAltaCorrectivos.this, "Notificado a otros usuarios", Toasty.LENGTH_SHORT).show();
                    }
                }),
                error -> Toasty.error(ActivityAltaCorrectivos.this, "Error: " + error.getMessage(), Toasty.LENGTH_LONG).show()){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void regresar() {
        Intent intent = new Intent(ActivityAltaCorrectivos.this,InicioActivity.class);
        startActivity(intent);
        finish();
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
        LayoutInflater inflater = ActivityAltaCorrectivos.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_grabar_audio, findViewById(R.id.clOrigen));
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
        ibReproducir.setOnClickListener(view4 -> reproducir());

        builder.setView(view);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        btnAceptarAudio.setOnClickListener(viewAudio -> dialog.dismiss());
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
        clAltaCorrectivos = findViewById(R.id.clAltaCorrectivos);
        ibRegresar = findViewById(R.id.ibRegresar);
        btnFinalizarTarea = findViewById(R.id.btnFinalizarAlta);
        etDescripcion = findViewById(R.id.etDescripcion);
        txtSucursalUsuario = findViewById(R.id.txtSucursalUsuario);
        txtSucursalUsuario.setText(constantes.NOMBRE_SUCURSAL);
        txtAreasCargar = findViewById(R.id.txtAreasCargar);
        txtAreasCargar.setInputType(InputType.TYPE_NULL);
        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        referenciaUsuarios = db.getReference("UsuariosLm");
        referenciaAltaCorrectivos = db.getReference("Actividades");
        referenciaAlmacenaje = FirebaseStorage.getInstance().getReference();
        permisosVideo = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
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
            if (resultCode == RESULT_OK){
                Toasty.success(this, "Evidencia tomada", Toasty.LENGTH_SHORT).show();
            }
        }
        if (requestCode == VIDEO_TOMAR_CAMARA){
            if (resultCode == RESULT_OK){
                uriVideo = Objects.requireNonNull(data).getData();
                pathVideo = data.getData().toString();
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
}