package com.PIR.airpad;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class SimulatorSurfaceView extends GLSurfaceView implements SensorEventListener{
	public SensorManager mSensor;
	private Sensor accelerometer;
	private DroneRender mDroneRender;
	
	public SimulatorSurfaceView(Context context) {
		super(context);
		mSensor= (SensorManager) context.getSystemService("sensor");
		accelerometer = mSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}
	
	public SimulatorSurfaceView(Context context,AttributeSet attrs){
		super(context,attrs);
		mSensor= (SensorManager) context.getSystemService("sensor");
		accelerometer = mSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void setRenderer(DroneRender renderer) {
		mDroneRender = renderer;
		super.setRenderer(renderer);
	}
	
	public void register(){
		mSensor.registerListener(this, accelerometer,SensorManager.SENSOR_DELAY_UI);
	}
	
	public void deregister(){
		mSensor.unregisterListener(this);
	}
}
