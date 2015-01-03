package net.afnf.and.notiftest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class NotifService extends Service {

    private final static int NOTIF_ID = 2;
    private final static int NOTIF_ID_HEADSUP = 3;
    private NotificationCompat.Builder builder;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        builder = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getStringExtra("start") != null) {
            final NotificationManager nman
                    = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);

            // Android4.4以下
            if (Build.VERSION.SDK_INT <= 20) {
                // 強制再表示させるためにフォアグラウンドをキャンセル
                stopForeground(true);
                startForeground(NOTIF_ID, createNotification(false));
            }
            // Android5.0以上
            else {
                // 初回の場合
                if (builder == null) {
                    startForeground(NOTIF_ID, createNotification(false));
                }
                // 内容の更新
                else {
                    nman.notify(NOTIF_ID, createNotification(false));
                }
                // Heads Up Notificationを表示
                nman.notify(NOTIF_ID_HEADSUP, createNotification(true));
                // 2.5秒後にキャンセル
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nman.cancel(NOTIF_ID_HEADSUP);
                    }
                }, 2500);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private Notification createNotification(boolean headsUp) {

        NotificationCompat.Builder b = null;

        if (builder != null) {
            b = builder;
        }
        else {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent
                    = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            b = new NotificationCompat.Builder(getApplicationContext());
            b.setContentIntent(contentIntent);
            b.setSmallIcon(android.R.drawable.ic_dialog_info);
            b.setContentTitle(getString(R.string.app_name));
            b.setWhen(System.currentTimeMillis());
            builder = b;
        }

        if (headsUp) {
            // Heads Up Notification
            b.setCategory(Notification.CATEGORY_SERVICE);
            b.setPriority(Notification.PRIORITY_HIGH);
            // 実行されないように遅延されたバイブレーション
            b.setVibrate(new long[]{60000, 100});
        }
        else {
            b.setCategory(null);
            b.setPriority(Notification.PRIORITY_DEFAULT);
            b.setVibrate(null);
        }

        String text = "notification from service";
        b.setTicker(text);
        b.setContentText(text);
        b.setTicker(text);
        b.setContentText(text);

        return b.build();
    }
}
