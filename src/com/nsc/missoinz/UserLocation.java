package com.nsc.missoinz;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

public class UserLocation extends Activity {
    LocationManager lm;
    TextView textLatitude, textLongitude;
            
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_location);

        textLatitude = (TextView)findViewById(R.id.textLatitude);
        textLongitude = (TextView)findViewById(R.id.textLongitude);
        
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        if(lm == null) {
            Toast.makeText(this, "Device isn't support location", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    public void onResume() {
        super.onResume();
        
        setup();
    }
    
    public void onStart() {
        super.onStart();
        
        boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!gpsEnabled && !networkEnabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }
    
    public void onStop() {
        super.onStop();
        if(lm != null)
            lm.removeUpdates(listener);
    }
    
    public void setup() {
        lm.removeUpdates(listener);
        String latitude = "Unknown";
        String longitude = "Unknown";
        
        Location networkLocation = requestProvider(LocationManager.NETWORK_PROVIDER, "Network not supported");
        if(networkLocation != null) {
            latitude = String.format("%.7f", networkLocation.getLatitude());
            longitude = String.format("%.7f", networkLocation.getLongitude());
        }
        
        Location gpsLocation = requestProvider(LocationManager.GPS_PROVIDER, "GPS not supported");
        if(gpsLocation != null) {
            latitude = String.format("%.7f", gpsLocation.getLatitude());
            longitude = String.format("%.7f", gpsLocation.getLongitude());
        }
        
        textLatitude.setText(latitude);
        textLongitude.setText(longitude);
    }
    
    public Location requestProvider(final String provider, String error) {
        Location location = null;
        if (lm.isProviderEnabled(provider)) {
            lm.requestLocationUpdates(provider, 1000, 10, listener);
            location = lm.getLastKnownLocation(provider);
        } else {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
        return location;
    }
    
    public final LocationListener listener = new LocationListener() {
        public void onLocationChanged(Location location) {
            textLatitude.setText(String.format("%.7f", location.getLatitude()));
            textLongitude.setText(String.format("%.7f",location.getLongitude()));
        }

        public void onProviderDisabled(String provider) { }
        public void onProviderEnabled(String provider) { }
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    };
}