package be.ucll.MobileAppsProject2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class AgendaItemsActivity extends AppCompatActivity
implements OnItemClickListener {

    private AgendaList list;
    private FileIO io;

    private Project2App app;

    private TextView titleTextView;
    private ListView itemsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        app = (Project2App) getApplication();
        io = new FileIO(getApplicationContext());

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        itemsListView = (ListView) findViewById(R.id.itemsListView);

        itemsListView.setOnItemClickListener(this);

    }

    class ReadFeed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            list = io.readFile();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("MA", "list read");


            // update the display for the activity
            AgendaItemsActivity.this.updateDisplay();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        if(list == null)
            new ReadFeed().execute();
        else
            updateDisplay();
    }

    public void updateDisplay()
    {
        if (list == null) {
            titleTextView.setText("Unable to get list");
            return;
        }

        // set the title for the list
        titleTextView.setText(list.getTitle());

        // get the items for the list
        ArrayList<AgendaItem> items = list.getAllItems();

        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data =
                new ArrayList<HashMap<String, String>>();
        for (AgendaItem item : items) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("startdate", item.getStartDateFormatted());
            map.put("enddate", item.getEndDateFormatted());
            map.put("title", item.getTitle());
            data.add(map);
        }

        // create the resource, from, and to variables
        int resource = R.layout.listview_item;
        String[] from = {"startdate","enddate", "title"};
        int[] to = {R.id.startDateTextView,R.id.endDateTextView, R.id.titleTextView};

        // create and set the adapter
        SimpleAdapter adapter =
                new SimpleAdapter(this, data, resource, from, to);
        itemsListView.setAdapter(adapter);

        Log.d("MA", "list displayed");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id) {

        // get the item at the specified position
        AgendaItem item = list.getItem(position);

        // create an intent
        Intent intent = new Intent(this, AgendaItemActivity.class);

        intent.putExtra("startdate", item.getStartDate());
        intent.putExtra("enddate", item.getEndDate());
        intent.putExtra("title", item.getTitle());


        this.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_AddItem) {
            Intent intent = new Intent(this, AddAgendaItemActivity.class);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
