package com.example.vivek.rentalmates.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.FirstActivity;
import com.example.vivek.rentalmates.receivers.GcmBroadcastReceiver;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GcmIntentService extends IntentService {

    private static final String TAG = "GcmIntent_Debug";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of un parcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());
                String message = extras.getString("message");
                showNotification(message);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    protected void showNotification(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                createNotification(message);
            }
        });
    }

    public void createNotification(String message) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, FirstActivity.class);
        intent.putExtra("notification", true);
        intent.putExtra("newExpenseAvailable", true);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build notification
        // Actions are just fake
        Notification notification = new Notification.Builder(this)
                .setContentTitle("RentalMates")
                .setContentText(message).setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent)
                .addAction(R.drawable.ic_launcher, "Call", pIntent)
                .addAction(R.drawable.ic_launcher, "More", pIntent)
                .addAction(R.drawable.ic_launcher, "And more", pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }
}
