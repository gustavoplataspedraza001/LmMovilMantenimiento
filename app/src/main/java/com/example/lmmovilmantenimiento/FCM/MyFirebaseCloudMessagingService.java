package com.example.lmmovilmantenimiento.FCM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.lmmovilmantenimiento.InicioActivity;
import com.example.lmmovilmantenimiento.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;
import java.util.Random;

public class MyFirebaseCloudMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Intent intent = new Intent();
        if (Objects.requireNonNull(remoteMessage.getData().get("tipo")).equals("CORRECTIVO")){
            if (Objects.requireNonNull(remoteMessage.getData().get("permiso")).equals("GERENTE")) {
                intent = new Intent(this, InicioActivity.class);
                intent.putExtra("redir", "CORRECTIVO_GERENTE");
            }else if (Objects.requireNonNull(remoteMessage.getData().get("permiso")).equals("USUARIO")){
                intent = new Intent(this, InicioActivity.class);
                intent.putExtra("redir", "CORRECTIVO_USUARIO");
                intent.putExtra("sucursal",remoteMessage.getData().get("sucursal"));
            }
        }
        else if (Objects.requireNonNull(remoteMessage.getData().get("tipo")).equals("CORRECTIVO_ALTA")
                && !Objects.requireNonNull(remoteMessage.getData().get("sucursal")).equals("")){
            intent = new Intent(this, InicioActivity.class);
            intent.putExtra("redir","CORRECTIVO_ALTA");
            intent.putExtra("sucursal",remoteMessage.getData().get("sucursal"));
        }
        else if (Objects.requireNonNull(remoteMessage.getData().get("tipo")).equals("PREVENTIVO_ALTA")
                && !Objects.requireNonNull(remoteMessage.getData().get("sucursal")).equals("")){
            intent = new Intent(this, InicioActivity.class);
            intent.putExtra("redir","PREVENTIVO_ALTA");
            intent.putExtra("sucursal",remoteMessage.getData().get("sucursal"));
        }
        else if (Objects.requireNonNull(remoteMessage.getData().get("tipo")).equals("PREVENTIVO")){
            if (Objects.requireNonNull(remoteMessage.getData().get("permiso")).equals("GERENTE")){
                intent = new Intent(this, InicioActivity.class);
                intent.putExtra("redir", "PREVENTIVO_GERENTE");
            }else if (Objects.requireNonNull(remoteMessage.getData().get("permiso")).equals("USUARIO")){
                intent = new Intent(this, InicioActivity.class);
                intent.putExtra("redir", "PREVENTIVO_USUARIO");
                intent.putExtra("sucursal",remoteMessage.getData().get("sucursal"));
            }
        }
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = new Random().nextInt(3000);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels(notificationManager);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_grupal);

        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "Mi_id")
                .setSmallIcon(R.drawable.ic_grupal)
                .setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true)
                .setPriority(2)
                .setSound(notificationSoundUri)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setColor(getResources().getColor(R.color.rojo));
        }
        notificationManager.notify(notificationID, notificationBuilder.build());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager){
        CharSequence adminChannelName = "Nueva notificación";
        String adminChannelDescription = "";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel("Mi_id", adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
}
