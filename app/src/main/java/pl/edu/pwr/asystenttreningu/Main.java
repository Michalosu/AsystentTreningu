package pl.edu.pwr.asystenttreningu;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class Main extends Activity {

    private LocationManager locationManager;
    private TextView Provider;
    private TextView Latitude;
    private TextView Longtitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Latitude = (TextView)findViewById(R.id.textView3);
        Longtitude = (TextView)findViewById(R.id.textView4);
        Provider  = (TextView)findViewById(R.id.textView6);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Provider.setText("OK");
        else
            Provider.setText("Not ok");
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000,
                2,
                locationListener);
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
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String arg0) {
            Provider.setText("OK");
        }

        @Override
        public void onProviderDisabled(String arg0) {
            Provider.setText("Not ok");
        }

        @Override
        public void onLocationChanged(Location location) {
            Latitude.setText(String.valueOf(location.getLatitude()));
            Longtitude.setText(String.valueOf(location.getLongitude()));
            Log.i("GPS", "GPS Latitude: " + Latitude);
            Log.i("GPS", "GPS Longtitude: " + Longtitude);
        }
    };
}
