package com.jamesisaac.rnbackgroundtask;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;
import com.facebook.react.HeadlessJsTaskService;
import java.util.concurrent.TimeUnit;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

public class HeadlessTaskService extends HeadlessJsTaskService {
    private static final String TAG = "BackgroundTask";

    @Override
    @SuppressLint("WrongConstant")
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Context mContext = this.getApplicationContext();
            String CHANNEL_ID = "Background job";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification =
                    new Notification.Builder(mContext, CHANNEL_ID)
                            .setContentTitle("Running background job")
                            .setContentText(mContext.getPackageName())
                            .build();
            startForeground(1, notification);
        }
    }

    @Override
    protected @Nullable HeadlessJsTaskConfig getTaskConfig(Intent intent) {
        Bundle extras = intent.getExtras();
        // If extras have been passed to the intent, pass them on into the JS as taskData
        // which can be accessed as the first param.
        WritableMap data = /* extras != null ? Arguments.fromBundle(extras) : */ Arguments.createMap();

        int timeout = extras.getInt("timeout");

        Log.d(TAG, String.format("Returning HeadlessJsTaskConfig, timeout=%s ms", timeout));
        return new HeadlessJsTaskConfig(
                // The the task was registered with in JS - must match
                "BackgroundTask",
                data,
                TimeUnit.SECONDS.toMillis(timeout)
        );
    }
}