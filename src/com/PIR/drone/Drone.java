package com.PIR.drone;

import android.opengl.Matrix;

public class Drone {
	private static final float SQRT3 = (float) Math.sqrt(3);
	private float omega;
	private float vitesse;
	private float[] currPos;
	private float[] currAngle;
	private float[] currModel = new float[16];
	
	public Drone(){
		omega = 100.0f;
		vitesse = 5.0f;
		currPos = new float[3];
		currAngle = new float[3];
		Matrix.setIdentityM(currModel, 0);
	}
	
	public void move(float deltaX, float deltaY , float deltaZ){
		currPos[0]+=deltaX;
		currPos[1]+=deltaY;
		currPos[2]+=deltaZ;
	}
	
	public void move(){
		float v = vitesse/SQRT3;
		float[] vect = {v,v,v,0};
		float[] res = new float[4];
		Matrix.multiplyMV(res,0,currModel,0,vect,0);
		move(res[0],res[1],res[2]);
	}
	
	public void rotate(float angleX ,float angleY , float angleZ){
		currAngle[0]+=angleX;
		currAngle[1]+=angleY;
		currAngle[2]+=angleZ;
		Matrix.rotateM(currModel,0,angleX, 1.0f, 0.0f,0.0f);
		Matrix.rotateM(currModel,0,angleY, 0.0f, 1.0f,0.0f);
		Matrix.rotateM(currModel,0,angleZ, 0.0f, 0.0f,1.0f);
	}
	
	public void setOmega(float omega){
		this.omega=omega;
	}
	
	public void setVitesse(float vitesse){
		this.vitesse = vitesse;
	}
	
	public float getOmega(){
		return omega;
	}
	
	public float getVitesse(){
		return vitesse;
	}
	
	public float[] getRotationMatrix(){
		return currModel;
	}
	
	public float getPosX(){
		return currPos[0];
	}
	
	public float getPosY(){
		return currPos[1];
	}
	
	public float getPosZ(){
		return currPos[2];
	}
}
