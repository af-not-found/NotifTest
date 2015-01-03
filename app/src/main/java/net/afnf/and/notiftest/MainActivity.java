package net.afnf.and.notiftest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class MainActivity extends ActionBarActivity {

    private final static int NOTIF_ID = 1;
    private NotificationCompat.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, NotifService.class);
        stopService(intent);
        final NotificationManager nman = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nman.cancel(NOTIF_ID);
        super.onDestroy();
    }

    public void onButton1(View view) {
        doNotify();
    }

    public void onButton2(View view) {
        Intent intent = new Intent(this, NotifService.class);
        intent.putExtra("start", "true");
        startService(intent);
    }

    public void onButton3(View view) {
        Intent intent = new Intent(this, NotifService.class);
        intent.putExtra("start", "false");
        startService(intent);
    }

    public void onButton4(View view) {
        Intent intent = new Intent(this, NotifService.class);
        stopService(intent);
    }

    public void doNotify() {
        final NotificationManager nman
           = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent
           = new Intent(this, MainActivity.class);
        PendingIntent contentIntent
           = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        if (builder == null) {
            builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setContentIntent(contentIntent);
            builder.setSmallIcon(android.R.drawable.ic_dialog_info);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setWhen(System.currentTimeMillis());
        }

        String text = "notification from action";
        builder.setTicker(text);
        builder.setContentText(text);

        // Android5.0以上
        if (Build.VERSION.SDK_INT > 20) {
            // Heads Up Notification
            builder.setCategory(Notification.CATEGORY_SERVICE);
            builder.setPriority(Notification.PRIORITY_HIGH);
            // 実行されないように遅延されたバイブレーション
            builder.setVibrate(new long[]{60000, 100});

            // 2.5秒後に通常のNotificationに変更
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    builder.setPriority(Notification.PRIORITY_DEFAULT);
                    builder.setVibrate(null);
                    nman.notify(NOTIF_ID, builder.build());
                }
            }, 2500);
        }

        // 強制再表示させるために一旦キャンセル
        nman.cancel(NOTIF_ID);
        nman.notify(NOTIF_ID, builder.build());
    }
}
