package com.example.lmmovilmantenimiento.ContenedorViewHolder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.lmmovilmantenimiento.Clases.MySingleton;
import com.example.lmmovilmantenimiento.ListadoTareasActivity;
import com.example.lmmovilmantenimiento.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class AdapterListadoTareasPropiasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public View vEstado;
    public TextView txtIdTarea,txtFechaAlta,txtDescMisCorrectivos;
    public String url,descripcion,status,sucursal,responsable;
    public CircleImageView ibDescripciones;
    public ImageButton ibDesplegarOpciones;
    public Context context;
    private FirebaseDatabase db;
    private final DatabaseReference referenciaActividad;

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAD89wScc:APA91bFvA5kfgDqnf2A6KJ7oAIYjcsneAyEVSUSlapvwepGb43Ts7zK0f0D37JiD0pgyFFyz_gO_a16EP6FR_PJxz8ch4-ThtbIhVx_VIKfcep62HcUqvl12TsXBiOglKFymR-dBF2eB";
    final private String contentType = "application/json";

    public AdapterListadoTareasPropiasViewHolder(@NonNull View itemView) {
        super(itemView);
        vEstado = itemView.findViewById(R.id.vEstado);
        txtIdTarea = itemView.findViewById(R.id.txtIdTarea);
        txtFechaAlta = itemView.findViewById(R.id.txtFechaAlta);
        ibDescripciones = itemView.findViewById(R.id.ibDescripciones);
        db = FirebaseDatabase.getInstance();
        referenciaActividad = db.getReference("Actividades");
        ibDesplegarOpciones = itemView.findViewById(R.id.ibDesplegarOpciones);
        txtDescMisCorrectivos = itemView.findViewById(R.id.txtDescMisCorrectivos);
        listeners();
    }

    private void listeners() {
        ibDescripciones.setOnClickListener(view ->{
            dialogo(url,descripcion);
        });
        ibDesplegarOpciones.setOnClickListener(view ->{
            dialog();
        });
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
        dialog.show();
    }

    private void dialog() {
        Activity activity = (Activity) context;
        LayoutInflater inflater = activity.getLayoutInflater();
        View vista = inflater.inflate(R.layout.dialog_acciones,null);
        Button btnSetEp,btnSetEr,btnSetTermiando,btnCerrarDialogo;
        btnSetEp = vista.findViewById(R.id.btnSetEp);
        btnSetEr = vista.findViewById(R.id.btnSetEr);
        btnSetTermiando = vista.findViewById(R.id.btnSetTerminar);
        btnCerrarDialogo = vista.findViewById(R.id.btnCerrarDialogo);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setView(vista);
        AlertDialog dialog = builder.create();
        if (status.equals("en proceso")){
            btnSetEp.setVisibility(View.GONE);
            cargarEventos(btnSetEr,btnSetTermiando,dialog);
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
                                intent.putExtra("tipo_origen","propios");
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }
                        });
                    }
                });
            });
            btnSetTermiando.setVisibility(View.GONE);
        }
        else{
            dialog.dismiss();
            Toasty.info(context,"Usted no puede liberar esta tarea",Toasty.LENGTH_LONG).show();
            btnSetEp.setVisibility(View.GONE);
            btnSetEr.setVisibility(View.GONE);
            btnSetTermiando.setVisibility(View.GONE);
        }
        btnCerrarDialogo.setOnClickListener(view ->{
            dialog.dismiss();
        });
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }

    private void cargarEventos(Button btnSetEr, Button btnSetTermiando, AlertDialog dialog) {
            btnSetEr.setOnClickListener(view -> {
                referenciaActividad.child(txtIdTarea.getText().toString()).child("fecha_inicio").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        referenciaActividad.child(txtIdTarea.getText().toString()).child("status").setValue("en revision").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                Intent intent = new Intent(context, ListadoTareasActivity.class);
                                intent.putExtra("sucursal", sucursal);
                                intent.putExtra("tipo_origen","propios");
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }
                        });
                    }
                });
            });
            btnSetTermiando.setOnClickListener(view -> {
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                referenciaActividad.child(txtIdTarea.getText().toString()).child("fechaTermino").setValue(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        referenciaActividad.child(txtIdTarea.getText().toString()).child("status").setValue("terminada").addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                cargarNoti(sucursalTk,"Tarea terminada por: " +responsable,"Correctivo");
                                Intent intent = new Intent(context, ListadoTareasActivity.class);
                                intent.putExtra("sucursal", sucursal);
                                intent.putExtra("tipo_origen","propios");
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }
                        });
                    }
                });
            });
    }

    private void cargarNoti(String tipo, String titulo, String mensaje) {
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", titulo);
            notifcationBody.put("message", mensaje);
            notifcationBody.put("tipo", "CORRECTIVO");
            notifcationBody.put("permiso", "GERENTE");

            notification.put("to", "/topics/" + tipo);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Toasty.error(context, "Error: " + e.getCause(), Toasty.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v)
    {

    }
}
