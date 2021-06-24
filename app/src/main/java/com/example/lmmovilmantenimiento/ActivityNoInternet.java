package com.example.lmmovilmantenimiento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class ActivityNoInternet extends AppCompatActivity {

    private Button btnReintentar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        listeners();
    }

    private void listeners() {
        String origen = getIntent().getExtras().getString("origen");
        btnReintentar.setOnClickListener(view ->{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo internetActivo = cm.getActiveNetworkInfo();
            if (internetActivo != null){
                switch (origen){
                    case "principal":
                        Intent intent = new Intent(ActivityNoInternet.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case "altaCorrectivos":
                        Intent intent1 = new Intent(ActivityNoInternet.this,ActivityAltaCorrectivos.class);
                        startActivity(intent1);
                        break;
                }
                finish();
                Animatoo.animateSlideRight(ActivityNoInternet.this);
            }
        });
    }

    private void init() {
        btnReintentar = findViewById(R.id.btnReintentar);
    }
}