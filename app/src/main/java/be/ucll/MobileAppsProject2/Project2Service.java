package be.ucll.MobileAppsProject2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class Project2Service extends Service {

    private Project2App app;
    private Timer timer;
    private FileIO io;

    @Override
    public void onCreate() {
        Log.d("News reader", "Service created");
        app = (Project2App) getApplication();
        io = new FileIO(getApplicationContext());
        startTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("News reader", "Service started");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("NEws reader","service bound - not used");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("News reader","serbice destroyed");
        stopTimer();
    }

    private void startTimer(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.d("News reader", "Timer task started");

                io.downloadFile();
                Log.d("News reader", "File downloaded");

                AgendaList newFeed = io.readFile();
                Log.d("News reader","File read");

                if(newFeed.getPubDateMillis()> app.getFeedMillis()){
                    Log.d("News reader","updated feed available");

                    app.setFeedMillis(newFeed.getPubDateMillis());

                    sendNotification("Select to view updated feed.");
                }
                else {
                    Log.d("News reader","");
                }
            }
        };
        timer = new Timer(true);
        int delay = 1000 * 60;
        int interval = 1000 * 60;
        timer.schedule(task, delay, interval);
    }
    private void stopTimer(){
        if(timer != null)
            timer.cancel();
    }
    private void sendNotification(String text){
        Intent notificationIntent = new Intent(this,AgendaItemActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this,0,notificationIntent,flags);

        int icon = R.mipmap.ic_launcher;
        CharSequence tickerText = "Updated new feed is available";
        CharSequence contentTitle = getText(R.string.app_name);
        CharSequence contentText = text;

        Notification notification =
                new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setTicker(tickerText)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        final int NOTIFICATION_ID = 1;
        manager.notify(NOTIFICATION_ID,notification);
    }
}
