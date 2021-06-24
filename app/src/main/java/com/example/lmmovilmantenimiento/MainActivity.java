package com.example.lmmovilmantenimiento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText etUsuarioReal, etPassReal;
    private Button btnInicioSesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        validarInicio();
    }
    private void validarInicio() {
        if (auth.getCurrentUser() != null){
            Intent intent = new Intent(MainActivity.this,InicioActivity.class);
            intent.putExtra("redir","");
            startActivity(intent);
            finish();
        }else {
            listeners();
        }
    }
    private void listeners() {
        btnInicioSesion.setOnClickListener(view ->{
            if (validarConexion()){
                if (etUsuarioReal.getText().toString().equals("") && etPassReal.getText().toString().equals("")){
                    etUsuarioReal.setError("¡!");
                    etPassReal.setError("¡!");
                }else {
                    auth.signInWithEmailAndPassword(etUsuarioReal.getText().toString(),etPassReal.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(MainActivity.this,InicioActivity.class);
                                intent.putExtra("redir","");
                                startActivity(intent);
                                finish();
                            }else{
                                Toasty.error(MainActivity.this, "Error de inicio de sesión", Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }else{
                Toasty.warning(this, "No cuenta con conexión a internet.", Toasty.LENGTH_LONG).show();
            }
        });
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
    private void init() {
        etUsuarioReal = findViewById(R.id.etUsuarioReal);
        etPassReal = findViewById(R.id.etPassReal);
        btnInicioSesion = findViewById(R.id.btnInicioSesion);
        /**Info Firebase**/
        auth = FirebaseAuth.getInstance();
    }
}