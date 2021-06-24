package com.example.lmmovilmantenimiento.Preventivos.ViewHolderPreventivos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.lmmovilmantenimiento.Clases.ItemUsuario;
import com.example.lmmovilmantenimiento.Clases.MySingleton;
import com.example.lmmovilmantenimiento.ContenedorViewHolder.AdapterListadoTareasViewHolder;
import com.example.lmmovilmantenimiento.LiberarActivity;
import com.example.lmmovilmantenimiento.Preventivos.ListadoPreventivos;
import com.example.lmmovilmantenimiento.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class AdapterListadoPreventivosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public View vEstado;
    public TextView txtIdTarea,txtFechaProgramacion,txtResponsablePreventivo,txtDescTodosPreventivos;
    public CircleImageView ibDescripciones;
    public ImageButton ibDesplegarOpciones;
    String fechaProgramacion,horaProgramacion,descripcion,area,equipo,sucursal,status;
    Context context;
    View viewReal;
    public String tipo_frecuencia,cantidad_frecuencia;

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAD89wScc:APA91bFvA5kfgDqnf2A6KJ7oAIYjcsneAyEVSUSlapvwepGb43Ts7zK0f0D37JiD0pgyFFyz_gO_a16EP6FR_PJxz8ch4-ThtbIhVx_VIKfcep62HcUqvl12TsXBiOglKFymR-dBF2eB";
    final private String contentType = "application/json";

    FirebaseDatabase db;
    DatabaseReference referenciaActividades,referenciaUsuarios;
    FirebaseAuth auth;

    public AdapterListadoPreventivosViewHolder(@NonNull View itemView) {
        super(itemView);
        init(itemView);
        listeners();
    }


    @SuppressLint("SetTextI18n")
    private void listeners() {
        ibDescripciones.setOnClickListener(view ->{
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            View vista = inflater.inflate(R.layout.dialog_descripcion_preventivos,null);
            TextView txtArea = vista.findViewById(R.id.txtArea),
            txtEquipo = vista.findViewById(R.id.txtEquipo),
            txtFechaHora = vista.findViewById(R.id.txtFechaHora),
            txtDesc = vista.findViewById(R.id.txtDesc);
            txtArea.setText(txtArea.getText().toString()+" "+area);
            txtEquipo.setText(txtEquipo.getText().toString()+" "+ equipo);
            txtFechaHora.setText(String.format(txtFechaHora.getText().toString()+ " " + "%s/%s", fechaProgramacion, horaProgramacion));
            txtDesc.setText(txtDesc.getText().toString()+ " " +descripcion);
            Button btnCerrarDialogo = vista.findViewById(R.id.btnCerrarDialogo);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(true);
            builder.setView(vista);
            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.show();
            btnCerrarDialogo.setOnClickListener(view2 ->{
                dialog.dismiss();
            });
        });

        ibDesplegarOpciones.setOnClickListener(view ->{
            dialog();
        });

        txtFechaProgramacion.setOnClickListener(view ->{
            if (status.equals("pendiente")) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context, R.style.AlertDialogTheme);
                View vistaRepetir = LayoutInflater.from(context).inflate(R.layout.custom_dialog_repetir, (ConstraintLayout) viewReal.findViewById(R.id.layoutDialogContainer));
                TextView txtInfo = vistaRepetir.findViewById(R.id.txtInfoRecordatorio),
                        txtTitulo = vistaRepetir.findViewById(R.id.txtTitulo);
                ImageView ivTitulo = vistaRepetir.findViewById(R.id.ivTitulo);
                txtInfo.setVisibility(View.VISIBLE);
                txtTitulo.setText(R.string.preguntaTipo);
                txtInfo.setText(R.string.avisoTipo);
                Glide.with(((Activity) context)).load(R.drawable.ic_pregunta).into(ivTitulo);
                Button btnAceptar = (Button) vistaRepetir.findViewById(R.id.btnAceptarRDate),
                        btnCancelar = (Button) vistaRepetir.findViewById(R.id.btnCancelarSeleccion);
                builder.setView(vistaRepetir);
                final android.app.AlertDialog alertDialog = builder.create();
                btnAceptar.setOnClickListener(viewAceptar -> {
                    referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                if (Objects.requireNonNull(snapshot1.child("uid").getValue()).toString().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())){
                                    referenciaActividades.child(txtIdTarea.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (Objects.requireNonNull(snapshot.child("usuario_responsable").getValue()).toString().equals("")){
                                                if (Objects.requireNonNull(snapshot.child("nombre_responsable").getValue()).toString().equals("")){
                                                    referenciaActividades.child(txtIdTarea.getText().toString()).child("usuario_responsable").setValue(snapshot1.getKey()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            referenciaActividades.child(txtIdTarea.getText().toString()).child("nombre_responsable").setValue(Objects.requireNonNull(snapshot1.child("nombre").getValue()).toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    referenciaActividades.child(txtIdTarea.getText().toString()).child("status").setValue("en proceso").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                                                            referenciaActividades.child(txtIdTarea.getText().toString()).child("fecha_inicio").setValue(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    alertDialog.dismiss();
                                                                                    Intent intent = new Intent(context,ListadoPreventivos.class);
                                                                                    intent.putExtra("sucursal",sucursal);
                                                                                    context.startActivity(intent);
                                                                                    String fechaDividida[] = fechaProgramacion.split("-");
                                                                                    String dia = fechaDividida[0];
                                                                                    String mes = fechaDividida[1];
                                                                                    String anio = fechaDividida[2];
                                                                                    String horaDividida[] = horaProgramacion.split(":");
                                                                                    String horas = horaDividida[0];
                                                                                    String minutos = horaDividida[1];
                                                                                    Calendar calendar = Calendar.getInstance();
                                                                                    calendar.set(0, 0, 0, Integer.parseInt(horas), Integer.parseInt(minutos));
                                                                                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                                                                    calIntent.setData(CalendarContract.Events.CONTENT_URI);
                                                                                    calIntent.putExtra(CalendarContract.Events.TITLE, descripcion);
                                                                                    GregorianCalendar calDate = new GregorianCalendar(Integer.parseInt(anio), Integer.parseInt(mes),
                                                                                            Integer.parseInt(dia)
                                                                                            , Integer.parseInt(horas), Integer.parseInt(minutos));
                                                                                    GregorianCalendar calDateEnd = new GregorianCalendar(Integer.parseInt(anio), Integer.parseInt(mes), Integer.parseInt(dia)
                                                                                            , (Integer.parseInt(horas) + 1),Integer.parseInt(minutos));
                                                                                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
                                                                                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                                                                            calDate.getTimeInMillis());
                                                                                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                                                                            calDateEnd.getTimeInMillis());

                                                                                    calIntent.putExtra(CalendarContract.EventsEntity.RRULE, "FREQ=" + tipo_frecuencia + ";INTERVAL=" + cantidad_frecuencia);
                                                                                    context.startActivity(calIntent);
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toasty.error(context,"Error: " + error.getCode(), Toasty.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_LONG).show();
                        }
                    });
                });
                btnCancelar.setOnClickListener(viewCancelar -> {
                    alertDialog.dismiss();
                });
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();
            }else{
                Toasty.info(context,"Recordatorios: solo en pendientes", Toasty.LENGTH_LONG).show();
            }
        });

        txtResponsablePreventivo.setOnClickListener(view ->{
            dialogoSelecciónUsuarios();
        });
    }

    private void dialog() {
        Activity activity = (Activity) context;
        LayoutInflater inflater = activity.getLayoutInflater();
        View vista = inflater.inflate(R.layout.dialog_acciones,null);
        Button btnSetEp,btnSetEr,btnSetTermiando,btnCerrarDialogo,btnSetLiberar;
        btnSetEp = vista.findViewById(R.id.btnSetEp);
        btnSetEr = vista.findViewById(R.id.btnSetEr);
        btnSetTermiando = vista.findViewById(R.id.btnSetTerminar);
        btnCerrarDialogo = vista.findViewById(R.id.btnCerrarDialogo);
        btnSetLiberar = vista.findViewById(R.id.btnSetLiberar);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setView(vista);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        if (status.equals("pendiente")){
            btnSetEp.setOnClickListener(view ->{
                referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            if (snapshot1.child("uid").getValue().toString().equals(auth.getCurrentUser().getUid())){
                                referenciaActividades.child(txtIdTarea.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (Objects.requireNonNull(snapshot.child("tipo_actividad").getValue()).toString().equals("PREVENTIVO")){
                                            referenciaActividades.child(txtIdTarea.getText().toString()).child("usuario_responsable").setValue(snapshot1.getKey()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    referenciaActividades.child(txtIdTarea.getText().toString()).child("nombre_responsable").setValue(snapshot1.child("nombre").getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                                            referenciaActividades.child(txtIdTarea.getText().toString()).child("fecha_inicio").setValue(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    referenciaActividades.child(txtIdTarea.getText().toString()).child("status").setValue("en proceso").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            dialog.dismiss();
                                                                            Intent intent = new Intent(context, ListadoPreventivos.class);
                                                                            intent.putExtra("sucursal",sucursal);
                                                                            context.startActivity(intent);
                                                                            ((Activity) context).finish();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toasty.error(activity, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toasty.error(activity, "Error firebase: "+error.getCode(), Toasty.LENGTH_SHORT).show();
                    }
                });
            });
            btnSetEr.setOnClickListener(view ->{
                referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            if (Objects.requireNonNull(snapshot1.child("uid").getValue()).toString().equals(auth.getCurrentUser().getUid())){
                                referenciaActividades.child(txtIdTarea.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.child("tipo_actividad").getValue().toString().equals("PREVENTIVO")){
                                            referenciaActividades.child(txtIdTarea.getText().toString()).child("usuario_responsable").setValue(snapshot1.getKey()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    referenciaActividades.child(txtIdTarea.getText().toString()).child("nombre_responsable").setValue(snapshot1.child("nombre").getValue().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            referenciaActividades.child(txtIdTarea.getText().toString()).child("fecha_inicio").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    referenciaActividades.child(txtIdTarea.getText().toString()).child("status").setValue("en revision").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            dialog.dismiss();
                                                                            Intent intent = new Intent(context,ListadoPreventivos.class);
                                                                            intent.putExtra("sucursal",sucursal);
                                                                            context.startActivity(intent);
                                                                            ((Activity) context).finish();
                                                                        }
                                                                    });
                                                                }
                                                            });

                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toasty.error(activity, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toasty.error(activity, "Error firebase: "+error.getCode(), Toasty.LENGTH_SHORT).show();
                    }
                });
            });
            btnSetTermiando.setVisibility(View.GONE);
        }
        else if (status.equals("en proceso")){
            btnSetEp.setVisibility(View.GONE);
            btnSetEr.setOnClickListener(view ->{
                referenciaActividades.child(txtIdTarea.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("tipo_actividad").getValue().toString().equals("PREVENTIVO")){
                            referenciaActividades.child(txtIdTarea.getText().toString()).child("fecha_inicio").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    referenciaActividades.child(txtIdTarea.getText().toString()).child("status").setValue("en revision").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(context,ListadoPreventivos.class);
                                            intent.putExtra("sucursal",sucursal);
                                            context.startActivity(intent);
                                            ((Activity) context).finish();
                                        }
                                    });
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toasty.error(activity, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                    }
                });
            });
            btnSetTermiando.setOnClickListener(view ->{
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                referenciaActividades.child(txtIdTarea.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (Objects.requireNonNull(snapshot.child("tipo_actividad").getValue()).toString().equals("PREVENTIVO")) {
                            referenciaActividades.child(txtIdTarea.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    referenciaActividades.child(txtIdTarea.getText().toString()).child("fechaTermino").setValue(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            referenciaActividades.child(txtIdTarea.getText().toString()).child("status").setValue("terminada").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    dialog.dismiss();
                                                    String sucursalTk = "";
                                                    if (sucursal.equals("Díaz Ordaz")){
                                                        sucursalTk = "DO";
                                                    }else if (sucursal.equals("Arboledas")){
                                                        sucursalTk = "AR";
                                                    }else if (sucursal.equals("Allende")){
                                                        sucursalTk = "ALL";
                                                    }else if (sucursal.equals("Villegas")){
                                                        sucursalTk = "VLL";
                                                    }else if (sucursal.equals("Petaca")){
                                                        sucursalTk = "PTC";
                                                    }
                                                    cargarNoti(sucursalTk,"Tarea terminada por : "+ txtResponsablePreventivo.getText().toString(),"Correctivo","gerente");
                                                    Intent intent = new Intent(context, ListadoPreventivos.class);
                                                    intent.putExtra("sucursal", sucursal);
                                                    context.startActivity(intent);
                                                    ((Activity) context).finish();
                                                }
                                            });
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toasty.error(activity, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toasty.error(activity, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                    }
                });
            });
        }
        else if (status.equals("en revision")){
            btnSetEr.setVisibility(View.GONE);
            btnSetEp.setOnClickListener(view ->{
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                referenciaActividades.child(txtIdTarea.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (Objects.requireNonNull(snapshot.child("tipo_actividad").getValue()).toString().equals("PREVENTIVO")){
                            referenciaActividades.child(txtIdTarea.getText().toString()).child("fecha_inicio").setValue(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    referenciaActividades.child(txtIdTarea.getText().toString()).child("status").setValue("en proceso").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(context,ListadoPreventivos.class);
                                            intent.putExtra("sucursal",sucursal);
                                            context.startActivity(intent);
                                            ((Activity) context).finish();
                                        }
                                    });
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toasty.error(activity, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                    }
                });
            });
            btnSetTermiando.setVisibility(View.GONE);
        }
        else if (status.equals("terminada")){
            btnSetEp.setVisibility(View.GONE);
            btnSetEr.setVisibility(View.GONE);
            btnSetTermiando.setVisibility(View.GONE);
            referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        if (Objects.requireNonNull(snapshot1.child("uid").getValue()).toString().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())){
                            DatabaseReference referenciaPerfiles = db.getReference("Perfiles");
                            referenciaPerfiles.child(snapshot1.child("id_perfil").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.child("descripcion").getValue().toString().equals("gerente")){
                                        if (status.equals("terminada")) {
                                            btnSetLiberar.setVisibility(View.VISIBLE);
                                            btnSetLiberar.setOnClickListener(view ->{
                                                dialog.dismiss();
                                                Intent intent = new Intent(context, LiberarActivity.class);
                                                intent.putExtra("sucursal",sucursal);
                                                intent.putExtra("id_tarea",txtIdTarea.getText().toString());
                                                context.startActivity(intent);
                                                ((Activity) context).finish();
                                            });
                                        }
                                    }else{
                                        btnSetEp.setVisibility(View.GONE);
                                        btnSetEr.setVisibility(View.GONE);
                                        btnSetTermiando.setVisibility(View.GONE);
                                        dialog.dismiss();
                                        Toasty.warning(context, "Usted no puede liberar la tarea", Toasty.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toasty.error(activity, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toasty.error(activity, "Error: "+ error.getCode(), Toasty.LENGTH_SHORT).show();
                }
            });
        }
        btnCerrarDialogo.setOnClickListener(view ->{
            dialog.dismiss();
        });
        dialog.show();
    }

    private void init(View itemView) {
        vEstado = (View) itemView.findViewById(R.id.vEstado);
        txtIdTarea = (TextView) itemView.findViewById(R.id.txtIdTarea);
        txtFechaProgramacion = (TextView) itemView.findViewById(R.id.txtFechaProgramacion);
        txtResponsablePreventivo = (TextView) itemView.findViewById(R.id.txtResponsablePreventivo);
        ibDescripciones = itemView.findViewById(R.id.ibDescripciones);
        ibDesplegarOpciones = (ImageButton) itemView.findViewById(R.id.ibDesplegarOpciones);
        db = FirebaseDatabase.getInstance();
        referenciaUsuarios = db.getReference("UsuariosLm");
        referenciaActividades = db.getReference("Actividades");
        auth = FirebaseAuth.getInstance();
        txtDescTodosPreventivos = (TextView) itemView.findViewById(R.id.txtDescTodosPreventivos);
    }

    private void dialogoSelecciónUsuarios() {
        referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (Objects.requireNonNull(snapshot1.child("uid").getValue()).toString().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())){
                        DatabaseReference referenciaPerfil = db.getReference("Perfiles");
                        referenciaPerfil.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot11 : snapshot.getChildren()) {
                                    if (Objects.requireNonNull(snapshot1.child("id_perfil").getValue()).toString().equals(snapshot11.getKey())) {
                                        if (Objects.requireNonNull(snapshot11.child("descripcion").getValue()).toString().equals("admin") && status.equals("pendiente")){
                                            Activity activity = (Activity) context;
                                            LayoutInflater inflater = activity.getLayoutInflater();
                                            View vista = inflater.inflate(R.layout.dialog_seleccion_usuario, null);
                                            Spinner spnUsuarios = vista.findViewById(R.id.spnUsuarios);
                                            Button btnSeleccionarUsuario = vista.findViewById(R.id.btnSeleccionar), btnCancelarSeleccion = vista.findViewById(R.id.btnCancelarSeleccion);
                                            llenarspinner(spnUsuarios);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                            builder.setCancelable(true);
                                            builder.setView(vista);
                                            AlertDialog dialog = builder.create();
                                            final String[] clickeado = {"",""};
                                            spnUsuarios.setSelected(false);
                                            spnUsuarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                    ItemUsuario seleccionado = (ItemUsuario) parent.getItemAtPosition(position);
                                                    clickeado[0] = seleccionado.getId_usuario();
                                                    clickeado[1] = seleccionado.getNombre_usuario();
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {

                                                }
                                            });
                                            btnSeleccionarUsuario.setOnClickListener(view -> {
                                                referenciaActividades.child(txtIdTarea.getText().toString()).child("usuario_responsable").setValue(clickeado[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        referenciaActividades.child(txtIdTarea.getText().toString()).child("nombre_responsable").setValue(clickeado[1]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                                                referenciaActividades.child(txtIdTarea.getText().toString()).child("fecha_inicio").setValue(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        referenciaActividades.child(txtIdTarea.getText().toString()).child("status").setValue("en proceso").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                        for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                                                                            if (snapshot2.getKey().equals(clickeado[0])){
                                                                                                cargarNoti(snapshot2.child("uid").getValue().toString(),"Preventivo asignado","Se le ha asignado una tarea","usuario");
                                                                                                dialog.dismiss();
                                                                                                Intent intent = new Intent(context, ListadoPreventivos.class);
                                                                                                intent.putExtra("sucursal", sucursal);
                                                                                                context.startActivity(intent);
                                                                                                ((Activity) context).finish();
                                                                                            }
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                                                        Toasty.error(context, "Error: "+error.getCode(), Toasty.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            });
                                            btnCancelarSeleccion.setOnClickListener(view -> {
                                                dialog.dismiss();
                                            });
                                            if (dialog.getWindow() != null) {
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                                            }
                                            if (dialog.getWindow() != null) {
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                                            }
                                            dialog.show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toasty.error(context, "Error: "+error.getCode(), Toasty.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(context, "Error: "+ error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarNoti(String tipo, String titulo, String mensaje,String validar) {
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            if (validar.equals("gerente")) {
            notifcationBody.put("title", titulo);
            notifcationBody.put("message", mensaje);
            notifcationBody.put("tipo", "PREVENTIVO");
            notifcationBody.put("permiso", "GERENTE");

            notification.put("to", "/topics/" + tipo);
            notification.put("data", notifcationBody);
            }else if (validar.equals("usuario")){
            notifcationBody.put("title", titulo);
            notifcationBody.put("message", mensaje);
            notifcationBody.put("tipo", "PREVENTIVO");
            notifcationBody.put("permiso", "USUARIO");
            notifcationBody.put("sucursal",sucursal);

            notification.put("to", "/topics/" + tipo);
            notification.put("data", notifcationBody);
            }
        } catch (JSONException e) {
            Toasty.error(context, "Error: " + e.getMessage(), Toasty.LENGTH_SHORT).show();
        }
        sendNotification(notification);
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toasty.success(context, "Correcto", Toasty.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toasty.error(context, "Request error", Toasty.LENGTH_LONG).show();
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
        MySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void llenarspinner(Spinner spn) {
        DatabaseReference referenciaPerfiles = db.getReference("Perfiles");
        referenciaPerfiles.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (snapshot1.child("descripcion").getValue().toString().equals("gerente")){
                        referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                final ArrayList<ItemUsuario> propiedadsn = new ArrayList<ItemUsuario>();
                                for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                    String propiedadDir = snapshot2.child("nombre").getValue(String.class);
                                    if (propiedadDir != null && !Objects.requireNonNull(snapshot2.child("id_perfil").getValue()).toString().equals(snapshot1.getKey())){
                                        propiedadsn.add(new ItemUsuario(snapshot2.getKey(),propiedadDir));
                                    }
                                }
                                UsuariosAdapter2 mAdapter = new UsuariosAdapter2(context,propiedadsn);
                                spn.setAdapter(mAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toasty.error(context, "Error firebase: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });

    }

    public class UsuariosAdapter2 extends ArrayAdapter<ItemUsuario> {
        public UsuariosAdapter2(Context context, ArrayList<ItemUsuario> usuariosList){
            super(context,0,usuariosList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return iniciarView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return iniciarView(position, convertView, parent);
        }

        private View iniciarView(int position,View convertView, ViewGroup parent){
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.dialog_spn_usuarios,parent,false);
            }
            TextView txtIdUsuario = convertView.findViewById(R.id.txtIdUsuario);
            TextView txtNombreUsuario = convertView.findViewById(R.id.txtNombreUsuario);
            ItemUsuario itemActual = getItem(position);

            if (itemActual != null) {
                txtIdUsuario.setText(itemActual.getId_usuario());
                txtNombreUsuario.setText(itemActual.getNombre_usuario());
            }
            return convertView;
        }
    }

    @Override
    public void onClick(View v) {
    }
}
