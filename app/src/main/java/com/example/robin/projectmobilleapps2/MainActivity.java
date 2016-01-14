package com.example.robin.projectmobilleapps2;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.model.*;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
       implements AdapterView.OnItemClickListener{
    GoogleAccountCredential mCredential;

    private ListView itemList;
    private TextView titleTextView;
    private List<String> descr = new ArrayList<String>();
    private DatabaseHelper locationDB;
    private int i  = 0;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        locationDB = new DatabaseHelper(this);
        itemList = (ListView)findViewById(R.id.itemlist);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        itemList.setOnItemClickListener(this);

        Intent intent = getIntent();
        if(intent.getStringExtra("lat") != null){

            boolean status =  locationDB.updateLatLongData(intent.getStringExtra("lat"),intent.getStringExtra("lon"),intent.getStringExtra("state"));
            if(!status)
            Toast.makeText(getApplicationContext(), "data not! updated",
                    Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "data updated",
                        Toast.LENGTH_SHORT).show();

        }



        if (!locationDB.isDataInDB()){
            locationDB.insertData("home", "50.975400", "5.400657");
            locationDB.insertData("work", "50.931048", "5.395189");
            Toast.makeText(getApplicationContext(), "data added",
                    Toast.LENGTH_SHORT).show();
        }


        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings_Home) {
            Intent startH = new Intent(getApplicationContext(),SettingsHomeActivity.class);
            Cursor data = locationDB.getDataAt(1);
            data.moveToNext();
            startH.putExtra("lat", data.getString(2));
            startH.putExtra("lon", data.getString(3));
            data.close();
            startActivity(startH);
            return true;
        }
        if (id == R.id.action_settings_Work) {
            Intent startW = new Intent(getApplicationContext(),SettingsWorkActivity.class);
            Cursor data = locationDB.getDataAt(2);
            data.moveToNext();
            startW.putExtra("lat", data.getString(2));
            startW.putExtra("lon", data.getString(3));
            data.close();
            startActivity(startW);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            Toast.makeText(getApplicationContext(), "Google Play Services required: \" +\n" +
                            "                    //\"after installing, close and relaunch this app.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Account unspecified.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempt to get a set of data from the Google Calendar API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    private void refreshResults() {
        if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
                new MakeRequestTask(mCredential).execute();
            } else {
                Toast.makeText(getApplicationContext(), "No network connection available.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode,
                MainActivity.this,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!descr.isEmpty()&&descr.size()-1>=position){
            Toast.makeText(getApplicationContext(), "Going to job.",
                    Toast.LENGTH_SHORT).show();
            String[] separated = descr.get(position).toString().split(";");
            double latitude = Double.parseDouble(separated[0]);
            double longitude = Double.parseDouble(separated[1]);

            Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitude  + ","+ longitude+"");

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            descr.clear();
            this.startActivity(mapIntent);

        }else if(!descr.isEmpty()) {
            if (!locationDB.isDataInDB()){
                locationDB.insertData("home", "50.975400", "5.400657");
                locationDB.insertData("work", "50.931048", "5.395189");
                Toast.makeText(getApplicationContext(), "data added",
                        Toast.LENGTH_SHORT).show();
            }
            Cursor res =  locationDB.getDataAt(position-(descr.size()-1));
            if(res.getCount() == 0)
            {
                Toast.makeText(getApplicationContext(), "no data in db",
                        Toast.LENGTH_SHORT).show();
                res.close();
            }
            else
            {
                res.moveToNext();

                Toast.makeText(getApplicationContext(), "Going :" + res.getString(1),
                        Toast.LENGTH_SHORT).show();

                Uri gmmIntentUri = Uri.parse("google.navigation:q="+res.getString(2)  + ","+ res.getString(3)+"");

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                descr.clear();
                res.close();
                this.startActivity(mapIntent);
            }
        }
        else{

            if(locationDB.getDataAt(position) != null) {


                Cursor res = locationDB.getDataAt(position+1);
                res.moveToNext();

                Toast.makeText(getApplicationContext(), "Going :" + res.getString(1),
                        Toast.LENGTH_SHORT).show();

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + res.getString(2) + "," + res.getString(3) + "");

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                descr.clear();
                res.close();
                this.startActivity(mapIntent);
            }
            else{
                Toast.makeText(getApplicationContext(), "No destination available.",
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            //DateTime tommorrow = now+24;
            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("s52gluotjhel47gph6rmtpj29o@group.calendar.google.com")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    //.setTimeMax(now+)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();
            int i  = 0;
            for (Event event : items) {

                DateTime start = event.getStart().getDateTime();
                String descrS =  event.getDescription();
                descr.add(descrS);
                i = i + 1;
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), start));
            }
            eventStrings.add("Home");
            eventStrings.add("Work");
            return eventStrings;
        }


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(List<String> output) {
            if (output == null || output.size() == 0) {
                titleTextView.setText("No results returned.");
            } else {
                titleTextView.setText("Jobs To Do:");
                itemList.setAdapter(new ArrayAdapter<String>(com.example.robin.projectmobilleapps2.MainActivity.this, android.R.layout.simple_list_item_1, output));
            }
        }
    }
}