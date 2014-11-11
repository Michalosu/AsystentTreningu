package pl.edu.pwr.asystenttreningu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;


public class Main extends Activity {

    private LocationManager locationManager;
    private TextView gpsOnOff;
    private TextView gpsStatus;
    private TextView latitude;
    private TextView longitude;
    private ArrayList<PositionGPS> training;
    private boolean enableTraning;
    ProgressDialog dialog;
    private Chronometer chronometer;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitude = (TextView)findViewById(R.id.textView3);
        longitude = (TextView)findViewById(R.id.textView4);
        gpsOnOff  = (TextView)findViewById(R.id.textView6);
        gpsStatus  = (TextView)findViewById(R.id.textView11);
        chronometer = (Chronometer)findViewById(R.id.chronometer);
        training = new ArrayList<PositionGPS>();
        enableTraning = false;

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            gpsOnOff.setText("ON");
        else
            gpsOnOff.setText("OFF");

        gpsStatus.setText("Undefined");

        Button start = (Button) findViewById(R.id.button);
        Button stop = (Button) findViewById(R.id.button2);

        start.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.i("Main", "Start training");
                training.clear();
                enableTraning = true;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        3000,
                        2,
                        locationListener);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
            }
        });

        stop.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.i("Main", "Stop training");
                chronometer.stop();
                locationManager.removeUpdates(locationListener);
                enableTraning = false;
                writeJSON();
            }
        });
        //Log.i("GPS", locationManager.getgpsStatus().toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settings = new Intent(Main.this, SettingsActivity.class);
            startActivity(settings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void writeJSON() {

        if(!CheckInternetConnection.checkConnection(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Musisz mieć włączony internet!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        String login = settings.getString("settings_login", "brak");
        String password = settings.getString("settings_password", "brak");
        JSONObject jsonData = new JSONObject();
        JSONObject point;

        try {
            jsonData.put("login", login);
            jsonData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(PositionGPS p: training) {
            try {
                point = new JSONObject();
                point.put("lat", p.getLatitude());
                point.put("lng", p.getLongitude());
                point.put("time", p.getTime());
                jsonData.accumulate("points", point);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream outputStream;
        try {
            Log.d("JSON", "Try save to: " + getFilesDir() + "JSONfile.txt");
            outputStream = openFileOutput("data.json", Context.MODE_PRIVATE);
            outputStream.write(jsonData.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        dialog = ProgressDialog.show(Main.this, "", "Wysyłam dane na serwer...", true);

        new Thread(new Runnable() {
            public void run() {
                HttpClient client = new HttpClient("http://37.187.99.85:8000/upload_file/", getFilesDir() + "/data.json", Main.this);
                //HttpClient client = new HttpClient("http://192.168.1.8:8000/upload_file/", getFilesDir() + "/data.json");
                try {
                    client.connectAndSend();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(),"Nie udało się wysłać pliku! Spróbuj później!", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        }).start();
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String arg0, int status, Bundle arg2) {

            switch (status){
                case LocationProvider.OUT_OF_SERVICE:
                    gpsStatus.setText("Out of service");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    gpsStatus.setText("Temporarily unavailable");
                    break;
                case LocationProvider.AVAILABLE:
                    gpsStatus.setText("Available");
                    break;
            }

        }

        @Override
        public void onProviderEnabled(String arg0) {
            gpsOnOff.setText("ON");
        }

        @Override
        public void onProviderDisabled(String arg0) {
            gpsOnOff.setText("OFF");
        }

        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            long time = location.getTime();
            latitude.setText(String.valueOf(lat));
            longitude.setText(String.valueOf(lng));
            if(enableTraning){
                training.add(new PositionGPS(lat, lng, time));
            }

            Log.i("GPS", "GPS latitude: " + lat);
            Log.i("GPS", "GPS Longtitude: " + lng);
        }
    };

}
