package com.PIR.my_observable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.PIR.airpad.R;
import com.PIR.drone.Drone;
import com.PIR.utils.RawResourceReader;
import com.PIR.utils.ShaderHelper;

public class DroneObservable {
	private Context mContext;
	private Drone mDrone;
	
	private final float CENTER_X = 0.75f;
	private final float CENTER_Y =0.0f;
	private final float CENTER_Z = 0.75f;
	private final float HAUTEUR_SOCLE =0.2f;
	private final float SCALE_FACTOR =0.5f;
	private final int COORDS_PER_POINT=3;
	private final int BYTES_PER_FLOAT =4;
	
	private float[] mMVPMatrix= new float[16];
	private float[] mModelView = new float[16];
	private float[] mCurrentRotation =new float[16];
	private float[] mCurrentTranslation = new float[16];
	
	private float[] backgroundColor ={0.0f,0.5f,1.0f,1.0f};
	private int mHeliceBuffer[] = new int[1];
	
	private int mColorHandle;
	private int mMVPMatrixHandle;
	private int mPositionHandle;
	
	private int mProgramHandle;
	
	private final long rotationTIME =1000;
	
	
	
	public DroneObservable(Context context , Drone drone){
		mContext = context;
		mDrone = drone;
	}
	
	public void draw(float[] mMatrixView , float[] mProjectionView){
		long l = SystemClock.uptimeMillis() % this.rotationTIME;
	    float f = mDrone.getOmega()/ (float)this.rotationTIME * (int) l;
	    
	    Matrix.setIdentityM(mCurrentTranslation,0);
	    Matrix.translateM(mCurrentTranslation, 0, mDrone.getPosX(), mDrone.getPosY(), mDrone.getPosZ());
	    
	    GLES20.glUseProgram(mProgramHandle);
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mHeliceBuffer[0]);
	    GLES20.glEnableVertexAttribArray(mPositionHandle);
	    GLES20.glUniform4fv(mColorHandle, 1, backgroundColor, 0);
	    GLES20.glVertexAttribPointer(mPositionHandle,COORDS_PER_POINT, 
	    		GLES20.GL_FLOAT, false, BYTES_PER_FLOAT*COORDS_PER_POINT, 0);
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	    
	    drawAux(mMatrixView,mProjectionView,f,-1,1,-1);
	    for(int i=0;i<4 ; i++){
	    	GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, i*5, 5);
	    }
	    drawAux(mMatrixView,mProjectionView,f,1,1,-1);
	    for(int i=4;i<8;i++){
	    	GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, i*5, 5);
	    }
	    drawAux(mMatrixView,mProjectionView,f,-1,1,1);
	    for(int i=8;i<12;i++){
	    	GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, i*5, 5);
	    }
	    drawAux(mMatrixView,mProjectionView,f,1,1,1);
	    for(int i=12;i<16;i++){
	    	GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, i*5, 5);
	    }
	    
	    Matrix.setIdentityM(mModelView, 0);
        Matrix.scaleM(mModelView, 0, SCALE_FACTOR, SCALE_FACTOR, SCALE_FACTOR);
        Matrix.multiplyMM(mCurrentRotation, 0, mDrone.getRotationMatrix(), 0,mModelView, 0);
        Matrix.multiplyMM(mModelView, 0, mCurrentTranslation, 0, mCurrentRotation, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mMatrixView, 0, mModelView, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionView, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 80, 3);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 83, 3);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 86, 3);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 89, 3);
	}
	
	public void initDrone(){
		float[] modelHelice={
				0.0f, 0.0f, 0.0f, 
				-0.5f, 0.0f, -0.375f,
				-0.5f, 0.0f, -0.5f, 
				0.0f, 0.0f, 0.0f,
				-0.375f, 0.0f, -0.5f, 
				0.0f, 0.0f, 0.0f,
				0.5f, 0.0f, -0.375f,
				0.5f, 0.0f, -0.5f,
				0.0f, 0.0f, 0.0f,
				0.375f, 0.0f, -0.5f,
				0.0f, 0.0f, 0.0f,
				-0.5f, 0.0f, 0.375f,
				-0.5f, 0.0f, 0.5f,
				0.0f, 0.0f, 0.0f,
				-0.375f, 0.0f, 0.5f,
				0.0f, 0.0f, 0.0f,
				0.375f, 0.0f, 0.5f,
				0.5f, 0.0f, 0.5f,
				0.0f, 0.0f, 0.0f,
				0.5f, 0.0f, 0.375f 
		};
		float[] modelSocle={
				 -CENTER_X, 0.0f, -CENTER_Z,
				 -CENTER_X, -HAUTEUR_SOCLE, -CENTER_Z,
				 0.0f, -HAUTEUR_SOCLE, 0.0f,
				 CENTER_X, 0.0f, -CENTER_Z,
				 CENTER_X, -HAUTEUR_SOCLE, -CENTER_Z,
				 0.0f, -HAUTEUR_SOCLE, 0.0f,
				 -CENTER_X, 0.0f, CENTER_Z,
				 -CENTER_X, -HAUTEUR_SOCLE, CENTER_Z,
				 0.0f, -HAUTEUR_SOCLE, 0.0f,
				 CENTER_X, 0.0f, CENTER_Z,
				 CENTER_X, -HAUTEUR_SOCLE, CENTER_Z,
				 0.0f, -HAUTEUR_SOCLE, 0.0f 
		};
		final float[] heliceData = new float[4*(9+modelHelice.length)];
		System.arraycopy(modelHelice,0,heliceData,0,modelHelice.length);
		System.arraycopy(modelHelice,0,heliceData,modelHelice.length,modelHelice.length);
		System.arraycopy(modelHelice,0,heliceData,modelHelice.length*2,modelHelice.length);
		System.arraycopy(modelHelice,0,heliceData,modelHelice.length*3,modelHelice.length);
		System.arraycopy(modelHelice,0,heliceData,modelHelice.length*4,modelHelice.length);
		for(int i=0;i<modelSocle.length;i++){
			heliceData[heliceData.length-i-1]=modelSocle[modelSocle.length-i-1];
		}
		final FloatBuffer droneBuffer =ByteBuffer.allocateDirect(BYTES_PER_FLOAT*heliceData.length)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		droneBuffer.put(heliceData).position(0);
		
		GLES20.glGenBuffers(1, mHeliceBuffer, 0);
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mHeliceBuffer[0]);
	    GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, BYTES_PER_FLOAT* droneBuffer.capacity(),droneBuffer,
	    		GLES20.GL_STATIC_DRAW);
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	    droneBuffer.limit(0);
	    String fragmentShader = RawResourceReader.readTextFileFromRawResource(mContext,R.raw.helice_fragment_shader);
	    String vertexShader = RawResourceReader.readTextFileFromRawResource(mContext, R.raw.helice_vertex_shader);
	    mProgramHandle = ShaderHelper.createAndLinkProgram(ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader),
	    		ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader), 
	    		new String[] { "a_Position" });
	    
	    this.mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
	    this.mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
	    this.mColorHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Color");
	    
	}
	
	public void drawAux(float[] mMatrixView , float[] mProjectionView, float angle,
			int x , int y , int z){
		Matrix.setIdentityM(mModelView, 0);
	    Matrix.translateM(mModelView, 0,x*CENTER_X,y*CENTER_Y,z*CENTER_Z);
	    Matrix.multiplyMM(mCurrentRotation, 0,mModelView,0,mDrone.getRotationMatrix(),0);
	    Matrix.rotateM(mCurrentRotation,0, angle, 0.0f, 1.0f, 0.0f);
	    System.arraycopy(mCurrentRotation,0,mModelView,0, 16);
	    Matrix.scaleM(mModelView,0,SCALE_FACTOR,1.0f, SCALE_FACTOR);
	    
	    Matrix.multiplyMM(mCurrentRotation, 0, mCurrentTranslation, 0, mModelView, 0);
	    System.arraycopy(mCurrentRotation,0, mModelView, 0, 16);
	    Matrix.multiplyMM(mMVPMatrix,0, mMatrixView, 0,mModelView,0);
	    Matrix.multiplyMM(mMVPMatrix,0, mProjectionView, 0, mMVPMatrix, 0);
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle,1, false, mMVPMatrix, 0);
	}
}
