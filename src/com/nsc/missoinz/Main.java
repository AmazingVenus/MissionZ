package com.nsc.missoinz;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.cengalabs.flatui.views.FlatButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class Main extends FragmentActivity {
	GoogleMap mMap;
	Marker mMarker;
	Marker[] animal = new Marker[11];
	Circle myCircle;
	LocationManager lm;
	double lat, lng;
	double distance = 0;
	PolylineOptions walkLine = new PolylineOptions();
	GoogleApiClient mLocationClient;
	ArrayList<LatLng> point_data = new ArrayList<LatLng>();
	ArrayList<MarkerOptions> animal_point = new ArrayList<MarkerOptions>();
	int animal_count = 11;
	DecimalFormat format = new DecimalFormat("#0.00");
	double mDeclination;
	TextView walk_dist;
	TextView walk_bearing;
	SensorManager sensorManager;
	Sensor sensor;
	boolean isInit;
	Button refresh_btn;
	
	IconRoundCornerProgressBar hp_bar;
	IconRoundCornerProgressBar mana_bar;
	RoundCornerProgressBar exp_bar;
	int mana;
	int hp;
	int exp;
	
	FlatButton ingame;
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		
		mLocationClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API).addConnectionCallbacks(mCallback)
				.addOnConnectionFailedListener(mListener).build();
		
		walk_dist = (TextView)findViewById(R.id.textView1);
		walk_bearing = (TextView)findViewById(R.id.textView2);
		refresh_btn = (Button)findViewById(R.id.button1);
		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		ingame = (FlatButton)findViewById(R.id.flatButton6);
		
		hp = 75;
		mana = 46;
		exp = 68;
		
		hp_bar = (IconRoundCornerProgressBar) findViewById(R.id.iconRoundCornerProgressBar1);
		hp_bar.setProgressColor(Color.rgb(249, 149, 184));
		hp_bar.setBackgroundColor(Color.DKGRAY);
		hp_bar.setHeaderColor(Color.rgb(255, 43, 112));
		hp_bar.setMax(200);
		hp_bar.setProgress(hp);
		
		mana_bar = (IconRoundCornerProgressBar) findViewById(R.id.iconRoundCornerProgressBar2);
		mana_bar.setProgressColor(Color.rgb(153, 217, 234));
		mana_bar.setBackgroundColor(Color.DKGRAY);
		mana_bar.setHeaderColor(Color.rgb(0, 162, 232));
		mana_bar.setMax(150);
		mana_bar.setProgress(mana);
		
		exp_bar = (RoundCornerProgressBar) findViewById(R.id.roundCornerProgressBar1);
		exp_bar.setProgressColor(Color.rgb(255, 201, 14));
		exp_bar.setBackgroundColor(Color.DKGRAY);
		exp_bar.setMax(100);
		exp_bar.setProgress(exp);
		
		isInit = true;
		refresh_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isInit = true;
				
			}
		});
		
		ingame.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),GamePlay.class);
				startActivity(i);
			}
		});
		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				Location source   = new Location("");
				Location dest 	  = new Location("");
				source.setLatitude(marker.getPosition().latitude);
				source.setLongitude(marker.getPosition().longitude);
				
				dest.setLatitude(lat);
				dest.setLongitude(lng);
				if(source.distanceTo(dest) <= 100){
					hp+=50;
					hp_bar.setProgress(hp);
					marker.remove();
					animal_count--;
				}
				return false;
			}
		});
		
	}

	private ConnectionCallbacks mCallback = new ConnectionCallbacks() {
		public void onConnected(Bundle bundle) {
			Toast.makeText(Main.this, "Services connected", Toast.LENGTH_SHORT)
					.show();

			LocationRequest mRequest = new LocationRequest()
					.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
					.setInterval(4000).setFastestInterval(1000);
			LocationServices.FusedLocationApi.requestLocationUpdates(
					mLocationClient, mRequest, locationListener);

			// mLocationClient.re
			// requestLocationUpdates(mRequest, locationListener);
		}

		public void onDisconnected() {
			Toast.makeText(Main.this, "Services disconnected",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onConnectionSuspended(int arg0) {
			// TODO Auto-generated method stub

		}
	};

	private OnConnectionFailedListener mListener = new OnConnectionFailedListener() {
		public void onConnectionFailed(ConnectionResult result) {
			Toast.makeText(Main.this, "Services connection failed",
					Toast.LENGTH_SHORT).show();
		}
	};

	private LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			LatLng coordinate = new LatLng(location.getLatitude(),location.getLongitude());
			if(isInit){
				mMap.clear();
				animal_point.clear();
				Log.d("Animal Position","current : " + location.getLatitude()+", "+ location.getLongitude());
				for (int i = 0; i < animal_count; i++) {
					double latt = Math.random()/300.0;
					double lngt = Math.random()/300.0;
					double a = Math.random();
					if(a < 0.5){
						latt += location.getLatitude();
					}else{
						latt = location.getLatitude() - latt;
					}
					a = Math.random();
					if(a < 0.5){
						lngt += location.getLongitude();
					}else{
						lngt = location.getLongitude() - lngt;
					}
					animal_point.add(new MarkerOptions().position(new LatLng(latt, lngt)).title("Animal "+i).icon(BitmapDescriptorFactory.fromAsset("animal.png")));
					Log.d("Animal Position","point"+i+" : " + latt+", "+ lngt);
					//mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latt, lngt)).icon(BitmapDescriptorFactory.fromAsset("map-marker-64.png")));
					
				}
				
				for (int j = 0; j < animal_point.size(); j++) {
					animal[j] = mMap.addMarker(animal_point.get(j));
				}
				isInit = false;
			}
			
			
			lat = location.getLatitude();
			lng = location.getLongitude();
			
			if(point_data.size() < 2){
				point_data.add(coordinate);
			}else{
				point_data.remove(0);
				point_data.add(coordinate);
				Location source   = new Location("");
				Location dest = new Location("");
				source.setLatitude(point_data.get(0).latitude);
				source.setLongitude(point_data.get(0).longitude);
				
				dest.setLatitude(point_data.get(1).latitude);
				dest.setLongitude(point_data.get(1).longitude);
				
				if(source.distanceTo(dest) > 7){
					distance += source.distanceTo(dest);
				}
				walk_dist.setText("Walked Distance : " + format.format(distance) + " m.");
				
				//mMarker = mMap.addMarker(new MarkerOptions().position(point_data.get(0)));
			}
			
			if (mMarker != null)
				mMarker.remove();

			//mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
			
			
			//mMarker = mMap.addMarker(new MarkerOptions().position(coordinate).icon(BitmapDescriptorFactory.fromAsset("androidpointer080814.png")));
			
			mMap.animateCamera(CameraUpdateFactory
					.newLatLngZoom(coordinate, 16));
			
			walkLine.add(coordinate).width((float) 5).color(Color.rgb(247, 141, 64));
			mMap.addPolyline(walkLine);
			CircleOptions c = new CircleOptions()
		     .center(coordinate)
		     .radius(100)
		     .strokeColor(Color.rgb(13, 162, 255))
		     .fillColor(Color.argb(30, 13, 162, 255))
		     .strokeWidth(5);
			
			
			if(myCircle != null)
				myCircle.remove();
			myCircle = mMap.addCircle(c);
			
			
			
		}

	};

	protected void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	protected void onStop() {
		super.onStop();
		mLocationClient.disconnect();
	}
	SensorEventListener magnetListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			walk_bearing.setText("X : "+format.format(event.values[0]) + ", Y : " + format.format(event.values[1]) + ", Z : "+format.format(event.values[2]));
			if (mMarker != null)
				mMarker.remove();
			mMarker = mMap.addMarker(new MarkerOptions().rotation(event.values[0]).anchor(0.5f, 0.5f).position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromAsset("direc-marker-40.png")));
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public void onResume() {
        super.onResume();
        sensorManager.registerListener(magnetListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


}
