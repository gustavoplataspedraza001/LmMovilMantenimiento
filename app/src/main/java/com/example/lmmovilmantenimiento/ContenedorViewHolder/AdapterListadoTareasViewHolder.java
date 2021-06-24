package com.example.lmmovilmantenimiento.ContenedorViewHolder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.lmmovilmantenimiento.Clases.ItemUsuario;
import com.example.lmmovilmantenimiento.Clases.MySingleton;
import com.example.lmmovilmantenimiento.LiberarActivity;
import com.example.lmmovilmantenimiento.ListadoTareasActivity;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class AdapterListadoTareasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public View vEstado;
    public TextView txtIdTarea,txtResponsableTarea,txtFechaAlta,txtDescripcionCorrectivos;
    public ImageButton ibDesplegarOpciones;
    public CircleImageView ibDescripciones;
    public String url,descripcion,sucursal,status;
    public Context context;
    private FirebaseDatabase db;
    private FirebaseAuth auth;
    private DatabaseReference referenciaActividad,referenciaUsuarios;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAD89wScc:APA91bFvA5kfgDqnf2A6KJ7oAIYjcsneAyEVSUSlapvwepGb43Ts7zK0f0D37JiD0pgyFFyz_gO_a16EP6FR_PJxz8ch4-ThtbIhVx_VIKfcep62HcUqvl12TsXBiOglKFymR-dBF2eB";
    final private String contentType = "application/json";

    public AdapterListadoTareasViewHolder(@NonNull View itemView) {
        super(itemView);
        init(itemView);
        listeners();
    }

    private void init(View itemView) {
        vEstado = itemView.findViewById(R.id.vEstado);
        txtIdTarea = itemView.findViewById(R.id.txtIdTarea);
        txtFechaAlta = itemView.findViewById(R.id.txtFechaAlta);
        txtResponsableTarea = itemView.findViewById(R.id.txtResponsableTarea);
        ibDescripciones = itemView.findViewById(R.id.ibDescripcionesImgTodosCr);
        ibDesplegarOpciones = itemView.findViewById(R.id.ibDesplegarOpciones);
        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        referenciaActividad = db.getReference("Actividades");
        referenciaUsuarios = db.getReference("UsuariosLm");
        txtDescripcionCorrectivos = itemView.findViewById(R.id.txtDescTodosCorrectivos);
    }

    private void listeners() {
        ibDescripciones.setOnClickListener(view ->{
            dialogo(url,descripcion);
        });
        ibDesplegarOpciones.setOnClickListener(view ->{
            dialog();
        });
        txtResponsableTarea.setOnClickListener(view ->{
            if (txtResponsableTarea.getText().toString().equals("Sin asignar")){
                dialogoSelecciónUsuarios();
            }
        });
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
                                                referenciaActividad.child(txtIdTarea.getText().toString()).child("usuario_responsable").setValue(clickeado[0]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        referenciaActividad.child(txtIdTarea.getText().toString()).child("nombre_responsable").setValue(clickeado[1]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                                                referenciaActividad.child(txtIdTarea.getText().toString()).child("fecha_inicio").setValue(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        referenciaActividad.child(txtIdTarea.getText().toString()).child("status").setValue("en proceso").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                        for (DataSnapshot snapshot2 : snapshot.getChildren()){
                                                                                            if (snapshot2.getKey().equals(clickeado[0])){
                                                                                                cargarNoti(Objects.requireNonNull(snapshot2.child("uid").getValue()).toString(),"Tarea asignada","Se le ha asignado una tarea","usuario");
                                                                                                dialog.dismiss();
                                                                                                Intent intent = new Intent(context, ListadoTareasActivity.class);
                                                                                                intent.putExtra("sucursal", sucursal);
                                                                                                context.startActivity(intent);
                                                                                                ((Activity) context).finish();
                                                                                            }
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                                                        Toasty.error(context, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
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
                notifcationBody.put("tipo", "CORRECTIVO");
                notifcationBody.put("permiso", "GERENTE");

                notification.put("to", "/topics/" + tipo);
                notification.put("data", notifcationBody);
            }else if (validar.equals("usuario")){
                notifcationBody.put("title", titulo);
                notifcationBody.put("message", mensaje);
                notifcationBody.put("tipo", "CORRECTIVO");
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
                                UsuariosAdapter mAdapter = new UsuariosAdapter(context,propiedadsn);
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

    public class UsuariosAdapter extends ArrayAdapter<ItemUsuario> {
        public UsuariosAdapter(Context context,ArrayList<ItemUsuario> usuariosList){
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void dialogo(String url, String descripcion) {
        Activity activity = (Activity) context;
        LayoutInflater inflater = activity.getLayoutInflater();
        View vista = inflater.inflate(R.layout.dialog_descripcion,null);
        ImageView ivDes = vista.findViewById(R.id.ivDes);
        TextView txtDes = vista.findViewById(R.id.txtDes);
        Button btnCerrarDialogo = vista.findViewById(R.id.btnCerrarDialogo);
        if (url.equals("")) {
            Glide.with(activity).load(context.getDrawable(R.drawable.noimagen)).into(ivDes);
        }else {
            Glide.with(activity).load(url).into(ivDes);
        }
        txtDes.setText(descripcion);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setView(vista);
        AlertDialog dialog = builder.create();
        btnCerrarDialogo.setOnClickListener(view ->{
            dialog.dismiss();
        });
        ivDes.setOnClickListener(view22 ->{
            dialog.dismiss();
            dialogo2(url,descripcion);
        });
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
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
                if (txtResponsableTarea.getText().toString().equals("Sin asignar")){
                    referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                if (Objects.requireNonNull(snapshot1.child("uid").getValue()).toString().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())){
                                    referenciaActividad.child(txtIdTarea.getText().toString()).child("usuario_responsable").setValue(snapshot1.getKey()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            referenciaActividad.child(txtIdTarea.getText().toString()).child("nombre_responsable").setValue(snapshot1.child("nombre").getValue()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                                                    referenciaActividad.child(txtIdTarea.getText().toString()).child("fecha_inicio").setValue(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            referenciaActividad.child(txtIdTarea.getText().toString()).child("status").setValue("en proceso").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    dialog.dismiss();
                                                                    Intent intent = new Intent(context,ListadoTareasActivity.class);
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
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toasty.error(activity, "Error firebase: "+error.getCode(), Toasty.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            btnSetEr.setOnClickListener(view ->{
                if (txtResponsableTarea.getText().toString().equals("Sin asignar")){
                    referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                if (Objects.requireNonNull(snapshot1.child("uid").getValue()).toString().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())){/**verificar***/
                                    referenciaActividad.child(txtIdTarea.getText().toString()).child("usuario_responsable").setValue(snapshot1.getKey()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            referenciaActividad.child(txtIdTarea.getText().toString()).child("nombre_responsable").setValue(Objects.requireNonNull(snapshot1.child("nombre").getValue()).toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    referenciaActividad.child(txtIdTarea.getText().toString()).child("fecha_inicio").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            referenciaActividad.child(txtIdTarea.getText().toString()).child("status").setValue("en revision").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    dialog.dismiss();
                                                                    Intent intent = new Intent(context,ListadoTareasActivity.class);
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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toasty.error(activity, "Error firebase: "+error.getCode(), Toasty.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            btnSetTermiando.setVisibility(View.GONE);
        }
        else if (status.equals("en proceso")){
            btnSetEp.setVisibility(View.GONE);
            btnSetEr.setOnClickListener(view ->{
                referenciaActividad.child(txtIdTarea.getText().toString()).child("fecha_inicio").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        referenciaActividad.child(txtIdTarea.getText().toString()).child("status").setValue("en revision").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                Intent intent = new Intent(context,ListadoTareasActivity.class);
                                intent.putExtra("sucursal",sucursal);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }
                        });
                    }
                });

            });
            btnSetTermiando.setOnClickListener(view ->{
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                referenciaActividad.child(txtIdTarea.getText().toString()).child("fechaTermino").setValue(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        referenciaActividad.child(txtIdTarea.getText().toString()).child("status").setValue("terminada").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
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
                                dialog.dismiss();
                                cargarNoti(sucursalTk,"Tarea terminada por : "+ txtResponsableTarea.getText().toString(),"Correctivo","gerente");
                                Intent intent = new Intent(context,ListadoTareasActivity.class);
                                intent.putExtra("sucursal",sucursal);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }
                        });
                    }
                });
            });
        }
        else if (status.equals("en revision")){
            btnSetEr.setVisibility(View.GONE);
            btnSetEp.setOnClickListener(view ->{
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                referenciaActividad.child(txtIdTarea.getText().toString()).child("fecha_inicio").setValue(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        referenciaActividad.child(txtIdTarea.getText().toString()).child("status").setValue("en proceso").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                Intent intent = new Intent(context,ListadoTareasActivity.class);
                                intent.putExtra("sucursal",sucursal);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }
                        });
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
                                        dialog.dismiss();
                                        btnSetEp.setVisibility(View.GONE);
                                        btnSetEr.setVisibility(View.GONE);
                                        btnSetTermiando.setVisibility(View.GONE);
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void dialogo2(String url, String descripcion) {
        Activity activity = (Activity)  context;
        LayoutInflater inflater = activity.getLayoutInflater();
        View vista = inflater.inflate(R.layout.dialog_descripcion_dtl_img,null);
        ImageView ivDescripciones = vista.findViewById(R.id.ivDescripcion);
        if (url.equals("")) {
            Glide.with(activity).load(context.getDrawable(R.drawable.noimagen)).into(ivDescripciones);
        }else {
            Glide.with(activity).load(url).into(ivDescripciones);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setView(vista);
        AlertDialog dialog = builder.create();
        ivDescripciones.setOnClickListener(view22 ->{
            dialog.dismiss();
            dialogo(url,descripcion);
        });
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }

    @Override
    public void onClick(View v) { }
}
