package com.example.lmmovilmantenimiento.Preventivos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.lmmovilmantenimiento.Clases.MySingleton;
import com.example.lmmovilmantenimiento.Clases.constantes;
import com.example.lmmovilmantenimiento.InicioActivity;
import com.example.lmmovilmantenimiento.Modelos.ModeloAlataPreventivo;
import com.example.lmmovilmantenimiento.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ActivityAltaPreventivo extends AppCompatActivity {
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAD89wScc:APA91bFvA5kfgDqnf2A6KJ7oAIYjcsneAyEVSUSlapvwepGb43Ts7zK0f0D37JiD0pgyFFyz_gO_a16EP6FR_PJxz8ch4-ThtbIhVx_VIKfcep62HcUqvl12TsXBiOglKFymR-dBF2eB";
    final private String contentType = "application/json";

    Spinner spnTipoProgramacion,spnAreasPreventivos,spnEquipoPreventivos;
    String[] arrayTipo = new String[]{"Correctivo programado", "Preventivo"};
    String[] frecuenciaTipo = new String[]{"","Diario","Semanalmente","Mensualmente","Anualmente"};
    ArrayList<String> arrayAreas = new ArrayList<String>()
    ,arrayEquipo = new ArrayList<String>();
    String fecha = "fecha",
            hora = "hora";
    String idDefinido,tipoUsuario,frecuencia = "",cantidadFrecuencia = "";

    TextView txtSucursalPreventivo;
    Button btnFechaYHora,btnGuardarPreventivo;
    EditText etDetallePreventivo;
    ImageButton ibRegresar;

    DatabaseReference referenciaAreas,referenciaEquipo,referenciaActividades,referenciaUsuarios;
    FirebaseDatabase db;
    FirebaseAuth auth;

    DatePickerDialog mDialogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_preventivo);
        ActivityAltaPreventivo.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        cargarSpinners();
        listeners();
    }

    private void listeners() {
        btnGuardarPreventivo.setOnClickListener(view ->{
            if (etDetallePreventivo.getText().toString().equals("")){
                etDetallePreventivo.setError("Vacío");
            }else {
                if (fecha.equals("fecha") || hora.equals("hora")){
                    Toasty.error(ActivityAltaPreventivo.this,"Seleccione fecha y hora",Toasty.LENGTH_LONG).show();
                }else {
                    referenciaUsuariosDb();
                }
            }
        });
        btnFechaYHora.setOnClickListener(view ->{
            if (etDetallePreventivo.getText().toString().equals("")){
                Toasty.warning(ActivityAltaPreventivo.this,"Llene el campo de descripciòn\npara guardarlo en el recordatorio",Toasty.LENGTH_LONG).show();
            }else {
                Calendar c = Calendar.getInstance();
                int dia = c.get(Calendar.DAY_OF_MONTH);
                int mes = c.get(Calendar.MONTH);
                int anio = c.get(Calendar.YEAR);
                mDialogo = new DatePickerDialog(ActivityAltaPreventivo.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fecha = dayOfMonth + "-" + (month) + "-" + year;
                        final int[] horas = new int[1];
                        final int[] minutos = new int[1];
                        TimePickerDialog timePickerDialog = new TimePickerDialog(ActivityAltaPreventivo.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        horas[0] = hourOfDay;
                                        minutos[0] = minute;
                                        hora = hourOfDay + ":" + minute;
                                        btnFechaYHora.setText(dayOfMonth + "-" + (month + 1) + "-" + year + "/" + hora);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAltaPreventivo.this, R.style.AlertDialogTheme);
                                        View vistaRepetir = LayoutInflater.from(ActivityAltaPreventivo.this).inflate(R.layout.custom_dialog_repetir, (ConstraintLayout) findViewById(R.id.layoutDialogContainer));
                                        Spinner spnTipo = (Spinner) vistaRepetir.findViewById(R.id.spnTipoRepeticion);
                                        EditText etFrecuencia = (EditText) vistaRepetir.findViewById(R.id.etCantidadRepetir);
                                        spnTipo.setVisibility(View.VISIBLE);
                                        etFrecuencia.setVisibility(View.VISIBLE);
                                        TextView txtTitulo = vistaRepetir.findViewById(R.id.txtTitulo);
                                        ImageView ivTitulo = vistaRepetir.findViewById(R.id.ivTitulo);
                                        txtTitulo.setText("Agregar recordatorio");
                                        Glide.with(ActivityAltaPreventivo.this).load(R.drawable.ic_calendar).into(ivTitulo);
                                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(vistaRepetir.getContext(), R.layout.spinner_custom, frecuenciaTipo);
                                        spnTipo.setAdapter(arrayAdapter);
                                        Button btnAceptar = (Button) vistaRepetir.findViewById(R.id.btnAceptarRDate),
                                                btnCancelar = (Button) vistaRepetir.findViewById(R.id.btnCancelarSeleccion);
                                        builder.setView(vistaRepetir);
                                        final AlertDialog alertDialog = builder.create();
                                        btnAceptar.setOnClickListener(viewAceptar -> {
                                            alertDialog.dismiss();
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.set(0, 0, 0, hourOfDay, minute);
                                            Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                            calIntent.setData(CalendarContract.Events.CONTENT_URI);
                                            calIntent.putExtra(CalendarContract.Events.TITLE, etDetallePreventivo.getText().toString());
                                            GregorianCalendar calDate = new GregorianCalendar(year, month, dayOfMonth
                                                    , horas[0], minutos[0]);
                                            GregorianCalendar calDateEnd = new GregorianCalendar(year, month, dayOfMonth
                                                    , (horas[0] + 1), minutos[0]);

                                            calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
                                            calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                                    calDate.getTimeInMillis());
                                            calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                                    calDateEnd.getTimeInMillis());
                                            String tipo = "";
                                            if (spnTipo.getSelectedItem().toString().equals("Diario")) {
                                                tipo = "DAILY";
                                            } else if (spnTipo.getSelectedItem().toString().equals("Semanalmente")) {
                                                tipo = "WEEKLY";
                                            } else if (spnTipo.getSelectedItem().toString().equals("Mensualmente")) {
                                                tipo = "MONTHLY";
                                            } else if (spnTipo.getSelectedItem().toString().equals("Anualmente")) {
                                                tipo = "YEARLY";
                                            }
                                            calIntent.putExtra(CalendarContract.EventsEntity.RRULE, "FREQ=" + tipo + ";INTERVAL=" + etFrecuencia.getText().toString());
                                            frecuencia = tipo;
                                            cantidadFrecuencia = etFrecuencia.getText().toString();
                                            startActivity(calIntent);
                                        });
                                        btnCancelar.setOnClickListener(viewCancelar -> {
                                            alertDialog.dismiss();
                                        });
                                        if (alertDialog.getWindow() != null) {
                                            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                                        }
                                        alertDialog.show();
                                    }
                                }, horas[0], minutos[0], true);

                        timePickerDialog.updateTime(horas[0], minutos[0]);
                        timePickerDialog.setMessage("Definir la hora de recordatorio");
                        timePickerDialog.show();
                    }
                }, anio, mes, dia);
                mDialogo.show();
            }
        });

        ibRegresar.setOnClickListener(view ->{
            regresar();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        regresar();
    }

    private void regresar() {
        Intent intent = new Intent(ActivityAltaPreventivo.this, InicioActivity.class);
        startActivity(intent);
        finish();
    }

    private void referenciaUsuariosDb() {
        referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    referenciaPreventivosAlta(snapshot1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(ActivityAltaPreventivo.this,"Error: "+ error.getCode(),Toasty.LENGTH_LONG).show();
            }
        });
    }

    private void referenciaPreventivosAlta(DataSnapshot snapshot1) {
        referenciaUsuarios.child(Objects.requireNonNull(snapshot1.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Objects.requireNonNull(snapshot.child("uid").getValue()).toString().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())){
                    referenciaActividades.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                            ModeloAlataPreventivo modeloAlataPreventivo = new ModeloAlataPreventivo(""+frecuencia,""+cantidadFrecuencia,"","","PREVENTIVO",
                                    spnTipoProgramacion.getSelectedItem().toString(),spnEquipoPreventivos.getSelectedItem().toString(),"pendiente",txtSucursalPreventivo.getText().toString(),
                                    snapshot.getKey(), Objects.requireNonNull(snapshot.child("nombre").getValue()).toString(),etDetallePreventivo.getText().toString(),timeStamp,"",spnAreasPreventivos.getSelectedItem().toString(),""+fecha,""+hora);
                            referenciaActividades.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    idDefinido = referenciaActividades.push().getKey();
                                    referenciaActividades.child(Objects.requireNonNull(idDefinido)).setValue(modeloAlataPreventivo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseMessaging.getInstance().unsubscribeFromTopic("200").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    referenciaUsuarios.child(snapshot1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshotR) {
                                                            DatabaseReference referenciaPerfiles = db.getReference("Perfiles");
                                                            referenciaPerfiles.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    for (DataSnapshot snapshot3 : snapshot.getChildren()){
                                                                        if (snapshot3.getKey().equals(Objects.requireNonNull(snapshotR.child("id_perfil").getValue()).toString())){
                                                                            tipoUsuario = Objects.requireNonNull(snapshot3.child("descripcion").getValue()).toString();
                                                                            cargarNoti("200","Nuevo Preventivo por " + Objects.requireNonNull(snapshot1.child("nombre").getValue()).toString(), etDetallePreventivo.getText().toString());
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    Toasty.error(ActivityAltaPreventivo.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Toasty.error(ActivityAltaPreventivo.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });

                                            Intent intent = new Intent(ActivityAltaPreventivo.this, ActivityAltaPreventivo.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toasty.error(ActivityAltaPreventivo.this,"Error: " + error.getCode(),Toasty.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toasty.error(ActivityAltaPreventivo.this,"Error: " + error.getCode(),Toasty.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(ActivityAltaPreventivo.this,"Error: " + error.getCode(),Toasty.LENGTH_LONG).show();
            }
        });
    }

    private void cargarNoti(String tipo, String titulo, String mensaje) {
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", titulo);
            notifcationBody.put("message", mensaje);
            notifcationBody.put("tipo", "PREVENTIVO_ALTA");
            notifcationBody.put("sucursal", txtSucursalPreventivo.getText().toString());

            notification.put("to", "/topics/"+tipo);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Toasty.error(this, "Error: " + e.getMessage(), Toasty.LENGTH_SHORT).show();
        }
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        FirebaseMessaging.getInstance().subscribeToTopic("200").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toasty.success(ActivityAltaPreventivo.this, "Correcto", Toasty.LENGTH_SHORT).show();
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toasty.error(ActivityAltaPreventivo.this, "Error: " + error.getMessage(), Toasty.LENGTH_LONG).show();
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
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void cargarSpinners() {
        referenciaAreas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int i = 0;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        arrayAreas.add(i, snapshot1.getKey());
                        i += 1;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ActivityAltaPreventivo.this, R.layout.spinner_custom, arrayAreas);
                    spnAreasPreventivos.setAdapter(arrayAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(ActivityAltaPreventivo.this,"Error: " + error.getCode(),Toasty.LENGTH_LONG).show();
            }
        });
        referenciaEquipo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int i = 0;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        arrayEquipo.add(i, snapshot1.getKey());
                        i += 1;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ActivityAltaPreventivo.this, R.layout.spinner_custom, arrayEquipo);
                    spnEquipoPreventivos.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(ActivityAltaPreventivo.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        spnTipoProgramacion = findViewById(R.id.spnTipoProgramacion);
        spnAreasPreventivos = findViewById(R.id.spnAreasPreventivos);
        spnEquipoPreventivos = findViewById(R.id.spnEquipoPreventivos);
        txtSucursalPreventivo = findViewById(R.id.txtSucursalPreventivo);
        etDetallePreventivo = findViewById(R.id.etDetallePreventivo);
        ibRegresar = findViewById(R.id.ibRegresar);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_custom, arrayTipo);
        spnTipoProgramacion.setAdapter(arrayAdapter);
        db = FirebaseDatabase.getInstance();
        referenciaAreas = db.getReference("Areas");
        referenciaEquipo = db.getReference("Equipos");
        referenciaActividades = db.getReference("Actividades");
        referenciaUsuarios = db.getReference("UsuariosLm");
        auth = FirebaseAuth.getInstance();
        txtSucursalPreventivo.setText(constantes.NOMBRE_SUCURSAL);
        btnFechaYHora = findViewById(R.id.btnFechaYHora);
        btnGuardarPreventivo = findViewById(R.id.btnGuardarPreventivo);
    }
}