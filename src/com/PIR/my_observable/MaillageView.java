package com.PIR.my_observable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.PIR.utils.RawResourceReader;
import com.PIR.utils.ShaderHelper;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class MaillageView {
	
	private Context mContext;
	private static final float GROUND_POS_X =-10.0f;
	private static final float GROUND_POS_Y =0.0f;
	private static final float GROUND_POS_Z =0.0f;
	
	private static final float SCALE_X = 30.0f;
	private static final float SCALE_Y = 1.0f;
	private static final float SCALE_Z = 30.0f;
	
	
	private static final int POINT_BY_AXES = 100;
	private static final int COORDS_PER_POINT =3;
	private static final int COLOR_PER_POINT= 4;
	//private static final int TEXTURE_COORDINATE=2;
	private static final int BYTES_PER_FLOAT =4;
	final int stride =(COORDS_PER_POINT+COLOR_PER_POINT)*BYTES_PER_FLOAT;
	
	
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
	
	public void draw( float[] mMatrixView , float[] mProjectionView){
		GLES20.glUseProgram(mProgramHandle);
		
		Matrix.setIdentityM(mModelMatrix,0);
		Matrix.translateM(mModelMatrix,0, GROUND_POS_X, GROUND_POS_Y, GROUND_POS_Z);
		Matrix.scaleM(mModelMatrix,0, SCALE_X, SCALE_Y, SCALE_Z);
		Matrix.multiplyMM(mMVPMatrix,0, mMatrixView,0 , mModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix,0, mProjectionView,0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle,1,false, mMVPMatrix,0);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mPositionBuffer);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle,COORDS_PER_POINT,
				GLES20.GL_FLOAT, false,stride,0);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mPositionBuffer);
		GLES20.glEnableVertexAttribArray(mColorHandle);
		GLES20.glVertexAttribPointer(mColorHandle,COLOR_PER_POINT,GLES20.GL_FLOAT,false,
				stride,COORDS_PER_POINT);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		
		for(int i=0; i<POINT_BY_AXES*POINT_BY_AXES;i++){
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,i*5, 5);
		}
	}
	
	FloatBuffer getGroundBuffer(){
		final float [] groundPosition = new float[35*POINT_BY_AXES*POINT_BY_AXES];
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
	
	public void initialiseGround(){
		final String vertexShader = RawResourceReader.readTextFileFromRawResource(mContext,com.PIR.airpad.R.raw.vertex_shader);
		final String fragmentShader = RawResourceReader.readTextFileFromRawResource(mContext, com.PIR.airpad.R.raw.fragment_shader);
		final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER,vertexShader);
		final int fragmentShaderHandle=ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
		
		mProgramHandle= ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
				new String[]{"a_Position","a_Color"});
		
		FloatBuffer maillageBuffer = getGroundBuffer();
		final int[] buffers = new int[1];
		GLES20.glGenBuffers(1,buffers,0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, maillageBuffer.capacity()*BYTES_PER_FLOAT,maillageBuffer,GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		mPositionBuffer=buffers[0];
		maillageBuffer.limit(0);
		
		mMVPMatrixHandle=GLES20.glGetUniformLocation(mProgramHandle,"u_MVPMatrix");
		mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
		mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
	}
}
