package com.PIR.my_observable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.PIR.utils.RawResourceReader;

import android.R;
import android.content.Context;
import android.opengl.GLES20;

public class MaillageView {
	
	private Context mContext;
	private static final int POINT_BY_AXES = 100;
	private static final int COORDS_PER_POINT =3;
	private static final int TEXTURE_COORDINATE=2;
	private static final int BYTES_PER_FLOAT =4;
	
	
	private float[] mModelMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	
	private int mPositionBuffer;
	private int mMVPMatrixHandle;
	private int mPositionHandle;
	private int mColorHandle;
	private int mProgramHandle;
	
	public MaillageView(Context context){
		mContext = context;
	}
	
	public void draw( float[] mModelView , float[] mProjectionView){
		
	}
	
	public FloatBuffer getGroundBuffer(){
		final float [] groundPosition = new float[35*POINT_BY_AXES*POINT_BY_AXES*POINT_BY_AXES];
		int groundPositionDataOffset=0;
		final int segments = 2*POINT_BY_AXES -1;
		final float minPosition =-1.0f;
		final float maxPosition = 1.0f;
		final float positionRange = maxPosition - minPosition;
		for (int x=0; x< POINT_BY_AXES;x++){
			for (int z=0;z<POINT_BY_AXES ;z++){
				final float x1 = minPosition+((positionRange/segments)*(x*2));
				final float x2= minPosition +((positionRange/segments)*(x*2+1));
				final float z1= minPosition+((positionRange/segments)*(z*2));
				final float z2 = minPosition+((positionRange/segments)*(z*2+1));
				if((x+z)%2 ==0){
					float[] square ={
							x1,0.0f,z1,
							0.0f,0.0f,0.0f,1.0f,
							x2,0.0f,z1,
							0.0f,0.0f,0.0f,1.0f,
							x1,0.0f,z2,
							0.0f,0.0f,0.0f,1.0f,
							x2,0.0f,z1,
							0.0f,0.0f,0.0f,1.0f,
							x2,0.0f,z2,
							0.0f,0.0f,0.0f,1.0f,
					};
					System.arraycopy(square,0,groundPosition,groundPositionDataOffset,square.length);
					groundPositionDataOffset+=square.length;
				}else{
					float[] square ={
							x1,0.0f,z1,
							1.0f,1.0f,1.0f,1.0f,
							x2,0.0f,z1,
							1.0f,1.0f,1.0f,1.0f,
							x1,0.0f,z2,
							1.0f,1.0f,1.0f,1.0f,
							x2,0.0f,z1,
							1.0f,1.0f,1.0f,1.0f,
							x2,0.0f,z2,
							1.0f,1.0f,1.0f,1.0f,
					};
					System.arraycopy(square,0,groundPosition,groundPositionDataOffset,square.length);
					groundPositionDataOffset+=square.length;
				}
			}
		}
		final FloatBuffer maillageBuffer = ByteBuffer.allocate(groundPosition.length*BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		maillageBuffer.put(groundPosition);
		maillageBuffer.position(0);
		return maillageBuffer;
	}
	
	void initialiseGround(){
		final String vertexShader = RawResourceReader.readTextFileFromRawResource(mContext,R.raw.fragment_shader);

		
		FloatBuffer maillageBuffer = getGroundBuffer();
		final int[] buffers = new int[1];
		GLES20.glGenBuffers(1,buffers,0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, maillageBuffer.capacity()*BYTES_PER_FLOAT,maillageBuffer,GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		mPositionBuffer=buffers[0];
		maillageBuffer.limit(0);
		maillageBuffer = null;
	}
}
