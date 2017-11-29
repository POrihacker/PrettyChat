package com.example.sumeet.prettychat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by sumeet on 2017-10-16.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String notificationTitle=remoteMessage.getNotification().getTitle();
        String notificationBody=remoteMessage.getNotification().getBody();
        String clickaction=remoteMessage.getNotification().getClickAction();
        String user=remoteMessage.getData().get("from_user_id");
        String name=remoteMessage.getData().get("user_name");

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_group_work_black_24dp)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationBody)
                         .setSound(defaultSoundUri);

        Intent resultIntent = new Intent(clickaction);
        resultIntent.putExtra("user_name", name);
        resultIntent.putExtra("from_user_id", user);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = (int) System.currentTimeMillis();
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        //mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        mBuilder.setAutoCancel(true);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }

}
