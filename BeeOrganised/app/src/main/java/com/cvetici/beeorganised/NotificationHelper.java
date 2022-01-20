package com.cvetici.beeorganised;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


class NotificationHelper extends ContextWrapper {
    private NotificationManager mManager;
    public static final String channel1ID = "channel1ID";
    public  static final String channel1Name = "Channel 1";
    public static final String channel2ID = "channel2ID";
    public  static final String channel2Name = "Channel 2";
    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            createChannels();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels(){
        NotificationChannel channel1 = new NotificationChannel(channel1ID,channel1Name, NotificationManager.IMPORTANCE_HIGH);
        channel1.enableLights(true);
        channel1.enableVibration(true);
        channel1.setLightColor(getResources().getColor(R.color.BeeText));
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel1);

        NotificationChannel channel2 = new NotificationChannel(channel2ID,channel2Name, NotificationManager.IMPORTANCE_DEFAULT);
        channel2.enableLights(true);
        channel2.enableVibration(true);
        channel2.setLightColor(getResources().getColor(R.color.BeeText));
        channel2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel2);

    }
    public NotificationManager getManager(){
        if(mManager==null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
    public NotificationCompat.Builder getChannel1Notification(String title,String message){
        return new NotificationCompat.Builder(getApplicationContext(),channel1ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_icon);

    }
    public NotificationCompat.Builder getChannel2Notification(String title,String message){
        return new NotificationCompat.Builder(getApplicationContext(),channel2ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_icon);

    }

    public NotificationCompat.Builder getChannelNotification() {
        return new NotificationCompat.Builder(getApplicationContext(),channel1ID)
                .setSmallIcon(R.drawable.ic_icon);
    }
}
