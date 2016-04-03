package com.PIR.airpad;

import android.app.Activity;
import android.os.Bundle;

import com.PIR.drone.Drone;
import com.PIR.my_observable.DroneObservable;
import com.PIR.my_observable.MaillageView;

public class Airpad_main extends Activity {
	
	private DroneRender droneRender;
	private MaillageView maillageView;
	private SimulatorSurfaceView surfView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		surfView = new SimulatorSurfaceView(this);
		surfView.setEGLContextClientVersion(2);
		maillageView = new MaillageView(this);
		droneRender = new DroneRender(new DroneObservable(this,new Drone()),maillageView);
		
		surfView.setRenderer(droneRender);
		setContentView(surfView);
		surfView.register();
		setContentView(R.layout.activity_airpad_main);
	}
	@Override
	public void onPause(){
		super.onPause();
	    this.surfView.onPause();
	    this.surfView.deregister();
	}
	
	@Override
	public void onResume(){
		super.onResume();
	    this.surfView.onResume();
	    this.surfView.register();
	}
	
}
