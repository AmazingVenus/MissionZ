package com.nsc.missoinz;

import java.util.List;

import com.cengalabs.flatui.views.FlatTextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class GamePlay extends Activity implements SurfaceHolder.Callback {
	
	Camera mCamera;
    SurfaceView mPreview;
    SensorManager sensorManager;
	Sensor sensor;
	FlatTextView x_axis;
	FlatTextView y_axis;
	FlatTextView z_axis;
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN 
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.ingame);
        
        mPreview = (SurfaceView)findViewById(R.id.surfaceView1);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		x_axis = (FlatTextView)findViewById(R.id.flatTextView1);
		y_axis = (FlatTextView)findViewById(R.id.flatTextView2);
		z_axis = (FlatTextView)findViewById(R.id.flatTextView3);
		
    }
    
    public void onResume() {
        Log.d("System","onResume");
        super.onResume();
        mCamera = Camera.open();
        sensorManager.registerListener(AccListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    public void onPause() {
        Log.d("System","onPause");
        super.onPause();
        mCamera.release();
    }

    public void surfaceChanged(SurfaceHolder arg0
            , int arg1, int arg2, int arg3) {
        Log.d("CameraSystem","surfaceChanged");
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> previewSize = params.getSupportedPreviewSizes();
        List<Camera.Size> pictureSize = params.getSupportedPictureSizes();
        params.setPictureSize(pictureSize.get(0).width,pictureSize.get(0).height);
        params.setPreviewSize(previewSize.get(0).width,previewSize.get(0).height);
        params.setJpegQuality(100);
        mCamera.setParameters(params);
        mCamera.setDisplayOrientation(90);
        
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        Log.d("CameraSystem","surfaceCreated");
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder arg0) { }
    
    SensorEventListener AccListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			x_axis.setText("X : "+event.values[0]);
			y_axis.setText("Y : "+event.values[1]);
			z_axis.setText("Z : "+event.values[2]);
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
}
