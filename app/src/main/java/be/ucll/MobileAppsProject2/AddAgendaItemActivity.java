package be.ucll.MobileAppsProject2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Robin on 16/11/2015.
 */
public class AddAgendaItemActivity extends AppCompatActivity
        implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        // get references to widgets
        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        TextView startDateTextView = (TextView) findViewById(R.id.startDateTextView);
        TextView endDateTextView = (TextView) findViewById(R.id.endDateTextView);
        Button btnedit = (Button)findViewById(R.id.btnEdit);

        // get the intent
        Intent intent = getIntent();

        // get data from the intent
        String startDate = intent.getStringExtra("startdate");
        String endDate = intent.getStringExtra("enddate");
        String title = intent.getStringExtra("title");

        // display data on the widgets
        startDateTextView.setText(startDate);
        endDateTextView.setText(endDate);
        titleTextView.setText(title);

        // set listener
        btnedit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}