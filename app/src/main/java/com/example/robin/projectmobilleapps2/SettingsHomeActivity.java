package com.example.robin.projectmobilleapps2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingsHomeActivity extends AppCompatActivity
        implements View.OnClickListener{

    private TextView LatTextView;
    private TextView LongTextView;
    private Button update;
    private String lat;
    private String lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_settings);

        Intent intent = getIntent();

        lat = intent.getStringExtra("lat");
        lon = intent.getStringExtra("lon");


        LatTextView = (TextView) findViewById(R.id.txtLat);
        LongTextView = (TextView) findViewById(R.id.txtLong);
        update = (Button) findViewById(R.id.btnUpdate);
        update.setOnClickListener(this);

        setDataToViews(lat,lon);

    }

    private void setDataToViews(String lat, String lon){
        LatTextView.setText(lat);
        LongTextView.setText(lon);
    }

    @Override
    public void onClick(View v) {
        Intent back = new Intent(getApplicationContext(),MainActivity.class);
        back.putExtra("lat", LatTextView.getText().toString());
        back.putExtra("lon", LongTextView.getText().toString());
        back.putExtra("state","1");
        startActivity(back);
        this.finish();
    }
}
