package com.example.drapps;

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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.drapps.admin.RecordActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private static final String NOTIFICATION_CHANNEL_ID = "MY_NOTIFICATION_CHANNEL_ID"; //required for android 0 above
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d("MyFirebaseMessaging", "onMessageReceived called" + message.getFrom());
        //all notifications will be received here
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //get data from notification
        String notificationType = message.getData().get("notificationType");
        if (notificationType.equals("Problem")) {
            Log.d("MyFirebaseMessaging", "Problem notification received" + message.getData());
            String buyerUid = message.getData().get("userUid");
            String sellerUid = message.getData().get("adminUid");
            String orderUid = message.getData().get("problemUid");
            String notificationTitle = message.getData().get("notificationTitle");
            String notificationMessage = message.getData().get("notificationMessage");

            if(firebaseUser !=null && firebaseAuth.getUid().equals(sellerUid)) {
                //user is signed in and is same user to which notification is sent
                showNotification(orderUid, sellerUid, buyerUid, notificationTitle, notificationMessage, notificationType);
            }
        }
        if (notificationType.equals("Status")) {
            Log.d("MyFirebaseMessaging", "Status notification received" + message.getData());
            String buyerUid = message.getData().get("userUid");
            String sellerUid = message.getData().get("adminUid");
            String orderUid = message.getData().get("problemUid");
            String notificationTitle = message.getData().get("notificationTitle");
            String notificationMessage = message.getData().get("notificationMessage");

            if(firebaseUser !=null && firebaseAuth.getUid().equals(buyerUid)) {
                //seller is signed in and is same seller to which notification is sent
                showNotification(orderUid, sellerUid, buyerUid, notificationTitle, notificationMessage, notificationType);
            }
        }
        // Check if message contains a notification payload.
        if (message.getNotification() != null) {
            Log.d("TAG", "Message Notification Body: " + message.getNotification().getBody());
        }
    }

    private void showNotification(String problemUid, String adminUid, String userUid, String notificationTitle, String notificationMessage, String notificationType){
        //notification
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //id for notification, random
        int notificationID = new Random().nextInt(3000);
        //Check if android version is Oreo/0 or above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNotificationChannel(notificationManager);
            System.out.println("Setup notification ");
        }

        //handle notification click, start order activity
        Intent intent = null;
        Log.d("MyFirebaseMessaging", "Intent is null: " + (intent == null));
        if (notificationType.equals("Problem")) {
            //Open OrderDetailsSellerActivity
            intent = new Intent(this, RecordActivity.class);
            intent.putExtra("problemUid", problemUid);
            intent.putExtra("sendBy", userUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            System.out.println("Successfully sent message: " + intent);
        } else if (notificationType.equals("Status")) {
            //Open OrderDetailsUsersActivity
            intent = new Intent(this, Login.class);
            intent.putExtra("problemUid", problemUid);
            intent.putExtra("sendTo", adminUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            System.out.println("Successfully sent message: " + intent);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        System.out.println("Successfully sent message: " + intent);
        System.out.println("Pending message: " + pendingIntent);
        //Large icon
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_construction_24);
        //Sound of notification
        Uri notificationSounUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //build notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.baseline_construction_24)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setSound(notificationSounUri)
                .setAutoCancel(true)//cancel/dismiss when clicked
                .setContentIntent(pendingIntent); //add intent

        //show notification
        notificationManager.notify(notificationID, notificationBuilder.build());
        System.out.println("Noti builder message: " + notificationBuilder);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupNotificationChannel(NotificationManager notificationManager) {
        Log.d("MyFirebaseMessaging", "Setting up Notification Channel");
        CharSequence channelName = "Some Sample Text";
        String channelDescription = "Channel Description here";

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
