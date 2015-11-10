package be.ucll.MobileAppsProject2;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Robin on 26/10/2015.
 */
public class Project2App extends Application {
    private long feedMillis = -1;

    public void setFeedMillis(long feedMillis){
        this.feedMillis = feedMillis;
    }
    public long getFeedMillis(){
        return feedMillis;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("News reader", "App started");

        Intent service = new Intent(this,Project2Service.class);
        startService(service);
    }
}
