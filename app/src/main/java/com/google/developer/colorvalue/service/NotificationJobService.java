package com.google.developer.colorvalue.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.developer.colorvalue.MainActivity;
import com.google.developer.colorvalue.R;

public class NotificationJobService extends JobService {

    private static final int NOTIFICATION_ID = 18;
    private static final int PENDING_INTENT_ID = 4321;

    AsyncTask mBackgroundTask;
        @Override
        public boolean onStartJob(final JobParameters params) {
            mBackgroundTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    makeNotification();

                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {

                    jobFinished(params, false);
                }
            };

            mBackgroundTask.execute();
            return true;

    }

    private void makeNotification() {
        Context context = NotificationJobService.this;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_dialog_info)
//                            .setLargeIcon(R.drawable.ic_delete)
                .setContentTitle(context.getString(R.string.time_to_practice))
                .setContentText(context.getString(R.string.it_is_time_to_practice))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        //If the build version is greater than JELLY_BEAN, set the notification's priority
        // to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }

    private  static PendingIntent contentIntent(Context context){
        Intent startActivity = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(context,
                PENDING_INTENT_ID,
                startActivity,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }

}