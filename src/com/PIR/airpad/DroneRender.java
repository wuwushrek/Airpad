package com.PIR.airpad;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

import com.PIR.my_observable.DroneObservable;
import com.PIR.my_observable.MaillageView;

public class DroneRender implements Renderer{
	private DroneObservable drone;
	private MaillageView maillage;
	
	private float[] mProjectionMatrix= new float[16];
	private float[] mViewMatrix = new float[16];
	
	public DroneRender(DroneObservable drone, MaillageView maillage){
		this.drone=drone;
		this.maillage=maillage;
	}

	@Override
	public void onDrawFrame(GL10 arg0) {
		// TODO Auto-generated method stub
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		maillage.draw(mViewMatrix, mProjectionMatrix);
		drone.draw(mViewMatrix, mProjectionMatrix);
	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int width, int height) {
		// TODO Auto-generated method stub
		// Set the OpenGL viewport to the same size as the surface.
				GLES20.glViewport(0, 0, width, height);
				// Create a new perspective projection matrix. The height will stay the same
				// while the width will vary as per aspect ratio.
				final float ratio = (float) width / height;
				final float left = -ratio;
				final float right = ratio;
				final float bottom = -1.0f;
				final float top = 1.0f;
				final float near = 1.0f;
				final float far = 500.0f;
				Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		// TODO Auto-generated method stub 
	    Log.d("ON SURFACE CREATED", "CREATION DE SURFACE");
	    Matrix.setLookAtM(this.mViewMatrix, 0, 0.0F, 0.7F, 2.0F, 0.0F, 0.0F, -5.0F, 0.0F, 1.0F, 0.0F);
	    maillage.initialiseGround();
	    drone.initDrone();
	}

}
