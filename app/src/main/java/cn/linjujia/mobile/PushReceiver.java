package cn.linjujia.mobile;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Log;

import cn.linjujia.mobile.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PushReceiver extends BroadcastReceiver {

    public static String LOG_TAG = PushReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationTitle = "";
        String notificationText = "";

        if (intent.getStringExtra("title") != null) {
            notificationTitle = intent.getStringExtra("title");
        }

        if (intent.getStringExtra("message") != null) {
            notificationText = intent.getStringExtra("message");
        }

        Log.d(LOG_TAG, notificationTitle);
        Log.d(LOG_TAG, notificationText);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        String channelId = "linjujia";
        NotificationChannel mChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, Notification.AUDIO_ATTRIBUTES_DEFAULT);

        notificationManager.createNotificationChannel(mChannel);
        Notification notification = new Notification.Builder(context, channelId).setContentTitle("Title")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(Const.id, notification);
        Const.id += 1;

        Message messages = new Message();
        messages.setTitle(notificationTitle);
        messages.setContent(notificationText);
        messages.setType(Message.MESSAGE_TYPE_IS_NOTICE);
        messages.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        messages.save();

    }
}
