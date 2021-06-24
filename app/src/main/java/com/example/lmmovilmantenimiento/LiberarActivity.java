package com.example.lmmovilmantenimiento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lmmovilmantenimiento.Modelos.PdfModelo;
import com.example.lmmovilmantenimiento.Preventivos.ListadoPreventivos;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class LiberarActivity extends AppCompatActivity {
    Button btnReSet, btnSetLiberarActividad;
    TextView txtFechaInicio,txtFechaTermino, txtFechaLiberacion,txtDescripcion;
    SignaturePad mSgnFirmaPad;
    FirebaseDatabase db;
    DatabaseReference referenciaCorrectivos,referenciaUsuario,referenciaPdfs;
    StorageReference referenciaAlmacenajePdfs;
    FirebaseAuth auth;
    String id,fechaInicio,fechaTermino,fechaAlta,area,descripcion,fechaLiberacion,quienLibera,quienLiberaId,tipoActividad,sucursal,responsable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liberar);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        listeners();
        extras();
    }

    private void extras() {
        id = getIntent().getExtras().getString("id_tarea");
        referenciaCorrectivos.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    txtDescripcion.setText(Objects.requireNonNull(snapshot.child("descripcion").getValue()).toString());
                    txtFechaInicio.setText("Fecha inicio: "+ Objects.requireNonNull(snapshot.child("fecha_inicio").getValue()).toString());
                    txtFechaLiberacion.setText("Fecha Alta: "+ Objects.requireNonNull(snapshot.child("fechaAlta").getValue()).toString());
                    txtFechaTermino.setText("Fecha termino: "+ Objects.requireNonNull(snapshot.child("fechaTermino").getValue()).toString());
                    fechaInicio = Objects.requireNonNull(snapshot.child("fecha_inicio").getValue()).toString();
                    fechaTermino = Objects.requireNonNull(snapshot.child("fechaTermino").getValue()).toString();
                    fechaAlta = Objects.requireNonNull(snapshot.child("fechaAlta").getValue()).toString();
                    area = Objects.requireNonNull(snapshot.child("area").getValue()).toString();
                    tipoActividad = Objects.requireNonNull(snapshot.child("tipo_actividad").getValue()).toString();
                    responsable = Objects.requireNonNull(snapshot.child("nombre_responsable").getValue()).toString();
                    descripcion = Objects.requireNonNull(snapshot.child("descripcion").getValue()).toString();
                    referenciaUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                if (Objects.requireNonNull(snapshot1.child("uid").getValue()).toString().equals(Objects.requireNonNull(auth.getCurrentUser()).getUid())){
                                    quienLibera = Objects.requireNonNull(snapshot1.child("nombre").getValue()).toString();
                                    quienLiberaId = snapshot1.getKey();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toasty.error(LiberarActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(LiberarActivity.this, "Error: " + error.getCode(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        btnReSet = findViewById(R.id.btnReSet);
        btnSetLiberarActividad = findViewById(R.id.btnSetLiberarActividad);
        txtFechaInicio = findViewById(R.id.txtFechaInicio);
        txtFechaTermino = findViewById(R.id.txtFechaTermino);
        txtFechaLiberacion = findViewById(R.id.txtFechaLiberado);
        txtDescripcion = findViewById(R.id.txtDescripcionLiberar);
        mSgnFirmaPad = findViewById(R.id.sgnFirmaPad);
        db = FirebaseDatabase.getInstance();
        referenciaCorrectivos = db.getReference("Actividades");
        referenciaUsuario = db.getReference("UsuariosLm");
        auth = FirebaseAuth.getInstance();
        referenciaPdfs = db.getReference("EvidenciaLiberados");
        referenciaAlmacenajePdfs = FirebaseStorage.getInstance().getReference();
        sucursal = getIntent().getExtras().getString("sucursal");
    }

    private void listeners() {
        btnReSet.setOnClickListener(view ->{
            mSgnFirmaPad.clear();
        });
        btnSetLiberarActividad.setOnClickListener(view ->{
            generarArchivoPdf(id,fechaInicio,fechaTermino,fechaAlta,area,descripcion,quienLibera,quienLiberaId,tipoActividad,mSgnFirmaPad.getSignatureBitmap(),responsable);
        });
        mSgnFirmaPad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
            }

            @Override
            public void onSigned() {
                btnReSet.setEnabled(true);
                btnSetLiberarActividad.setEnabled(true);
            }

            @Override
            public void onClear() {
                btnReSet.setEnabled(false);
                btnSetLiberarActividad.setEnabled(false);
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void generarArchivoPdf(String id, String fechaInicio, String fechaTermino, String fechaAlta, String area, String descripcion, String quienLibera, String quienLiberaId, String tipoActividad, Bitmap toString, String responsableReal) {
        fechaLiberacion = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        PdfDocument myPDfDocument = new PdfDocument();
        Paint myPaint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(250,400,1).create();
        PdfDocument.Page myPage = myPDfDocument.startPage(pageInfo);
        Canvas canvas = myPage.getCanvas();
        myPaint.setTextAlign(Paint.Align.CENTER);
        myPaint.setTextSize(12.0f);
        canvas.drawText("La Misión",pageInfo.getPageHeight()/2,30,myPaint);

        myPaint.setTextSize(6.0f);
        myPaint.setColor(Color.rgb(122,119,119));
        canvas.drawText("Sucursal: Misión "+sucursal,pageInfo.getPageHeight()/2,40,myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(9.0f);
        myPaint.setColor(Color.rgb(122,119,119));
        canvas.drawText("Información",10,70,myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(8.0f);
        myPaint.setColor(Color.BLACK);

        int inicioXposicion = 10,
        finalXPosicion = pageInfo.getPageWidth()-10;

        canvas.drawText("Fecha de inicio: "+ fechaInicio, inicioXposicion,100,myPaint);
        canvas.drawLine(inicioXposicion,103,finalXPosicion,103,myPaint);
        canvas.drawText("Fecha de Termino: "+ fechaTermino, inicioXposicion,120,myPaint);
        canvas.drawLine(inicioXposicion,123,finalXPosicion,123,myPaint);
        canvas.drawText("Fecha de Liberación: "+ fechaLiberacion, inicioXposicion,140,myPaint);
        canvas.drawLine(inicioXposicion,143,finalXPosicion,143,myPaint);
        canvas.drawText("Fecha de Alta: "+ fechaAlta, inicioXposicion,160,myPaint);
        canvas.drawLine(inicioXposicion,163,finalXPosicion,163,myPaint);
        canvas.drawText("Area: "+ area, inicioXposicion,180,myPaint);
        canvas.drawLine(inicioXposicion,183,finalXPosicion,183,myPaint);
        canvas.drawText("Descripción: "+ descripcion, inicioXposicion,200,myPaint);
        canvas.drawLine(inicioXposicion,203,finalXPosicion,203,myPaint);
        canvas.drawText("Responsable: "+ responsableReal, inicioXposicion,220,myPaint);
        canvas.drawLine(inicioXposicion,223,finalXPosicion,223,myPaint);
        canvas.drawText("Mantenimiento "+ tipoActividad.toLowerCase(), inicioXposicion,260,myPaint);
        canvas.drawLine(inicioXposicion,263,finalXPosicion,263,myPaint);

        canvas.drawText("Firma de liberado por "+ quienLibera,10,295,myPaint);
        Bitmap scaladoBitmap = Bitmap.createScaledBitmap(toString,140,100,false);
        canvas.drawBitmap(scaladoBitmap,50,300,myPaint);

        myPDfDocument.finishPage(myPage);
        String s = String.valueOf(System.currentTimeMillis());
        File file = new File(Environment.getExternalStorageDirectory(),"/"+s+"PdfAlmacenar.pdf");

        try {
            myPDfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        myPDfDocument.close();
        subirPdf(file,id,quienLiberaId);
    }

    private void subirPdf(File file, String id, String quienLiberaId) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Archivo subiendose");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StorageReference reference = referenciaAlmacenajePdfs.child("ActividadesLiberadas").child(id+"_"+quienLiberaId+".pdf");
        Uri uri = Uri.fromFile(file.getAbsoluteFile());
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri uri = uriTask.getResult();
                PdfModelo pdfModeloS = new PdfModelo(uri.toString(),
                        quienLiberaId, fechaLiberacion,
                        tipoActividad);
                referenciaCorrectivos.child(id).child("status").setValue("liberada").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        referenciaCorrectivos.child(id).child("fechaLiberacion").setValue(fechaLiberacion).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (tipoActividad.equals("PREVENTIVO")){
                                    referenciaPdfs.child(id).setValue(pdfModeloS);
                                    Toasty.success(LiberarActivity.this, "Tarea Liberada", Toasty.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    finish();
                                    Intent intent = new Intent(LiberarActivity.this, ListadoPreventivos.class);
                                    intent.putExtra("sucursal",sucursal);
                                    startActivity(intent);
                                }else if (tipoActividad.equals("CORRECTIVO")){
                                    referenciaPdfs.child(id).setValue(pdfModeloS);
                                    Toasty.success(LiberarActivity.this, "Tarea Liberada", Toasty.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    finish();
                                    Intent intent = new Intent(LiberarActivity.this,ListadoTareasActivity.class);
                                    intent.putExtra("sucursal",sucursal);
                                    startActivity(intent);
                                }

                            }
                        });
                    }
                });
            }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                progressDialog.setMessage("Subiendo archivo..."+ (int) progress+"%");
            }
        });
    }

    @Override
    protected void onStop() {
        mSgnFirmaPad.clear();
        mSgnFirmaPad.setVisibility(View.GONE);
        super.onStop();
    }

    @Override
    protected void onPause() {
        mSgnFirmaPad.clear();
        mSgnFirmaPad.setVisibility(View.GONE);
        super.onPause();
    }

    @Override
    protected void onResume() {
        mSgnFirmaPad.setVisibility(View.VISIBLE);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        mSgnFirmaPad.setVisibility(View.VISIBLE);
        super.onRestart();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String i = getIntent().getExtras().getString("sucursal");
        Intent intent = new Intent(LiberarActivity.this,ListadoTareasActivity.class);
        intent.putExtra("sucursal",i);
        startActivity(intent);
    }
}