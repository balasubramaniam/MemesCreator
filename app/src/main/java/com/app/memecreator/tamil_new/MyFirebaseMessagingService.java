package com.app.memecreator.tamil_new;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.app.memecreator.tamil_new.activity.MainActivity;
import com.app.memecreator.tamil_new.activity.TodayMemeViewActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Bala on 19/05/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        //Calling method to generate notification
        sendNotification(remoteMessage.getData().get("url"));
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String url) {

        Bitmap remote_picture = null;

// Create the style object with BigPictureStyle subclass.
        NotificationCompat.BigPictureStyle notiStyle = new
                NotificationCompat.BigPictureStyle();
        notiStyle.setBigContentTitle("Today Trending Meme");
        notiStyle.setSummaryText("Click to share to your friends");

        try {
            remote_picture = BitmapFactory.decodeStream(
                    (InputStream) new URL(url).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

// Add the big picture to the style.
        notiStyle.bigPicture(remote_picture);

// Creates an explicit intent for an ResultActivity to receive.
        Intent resultIntent = new Intent(this, TodayMemeViewActivity.class);
        resultIntent.putExtra("url",url);

// This ensures that the back button follows the recommended
// convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

// Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(MainActivity.class);

// Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification myNotification =  new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setLargeIcon(remote_picture)
                .setContentIntent(resultPendingIntent)
                .setContentTitle("Today's Trending Meme")
                .setContentText("Click to share to your friends")
                .setStyle(notiStyle).build();


        /*Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Firebase Push Notification")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);*/

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, myNotification);
    }
}