package pl.edu.pwr.asystenttreningu;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitude = (TextView)findViewById(R.id.textView3);
        longitude = (TextView)findViewById(R.id.textView4);
        gpsOnOff  = (TextView)findViewById(R.id.textView6);
        gpsStatus  = (TextView)findViewById(R.id.textView11);
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
                enableTraning = true;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        3000,
                        2,
                        locationListener);
            }
        });

        stop.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
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
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void writeJSON() {
        Toast.makeText(getApplicationContext(), "Try save to: " + getFilesDir() + "/JSONfile.txt",
                Toast.LENGTH_LONG);
        JSONObject jsonData = new JSONObject();
        for(PositionGPS p: training) {
            try {
                jsonData.put("point1", p.getLatitude());
                jsonData.put("point2", p.getLongitude());
                jsonData.put("time", p.getTime());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream outputStream;
        try {
            Log.d("JSON", "Try save to: " + getFilesDir() + "JSONfile.txt");
            outputStream = openFileOutput("JSONfile.txt", Context.MODE_PRIVATE);
            outputStream.write(jsonData.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
