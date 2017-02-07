package com.sedsoftware.udacity.popularmovies.utils;


import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.sedsoftware.udacity.popularmovies.R;

public class NotificationUtils {

    private static final int NOTIFICATION_ID = 12345;

    /**
     * Notifies user about local movies database update.
     *
     * @param context Current context.
     */
    public static void notifyUserAboutUpdate(Context context) {

        Bitmap largeIcon = BitmapFactory.decodeResource(
                context.getResources(),
                R.mipmap.ic_launcher);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_cached_black_24dp)
                .setLargeIcon(largeIcon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.notification_update))
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
