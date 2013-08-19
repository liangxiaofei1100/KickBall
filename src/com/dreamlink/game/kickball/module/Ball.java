package com.dreamlink.game.kickball.module;

import android.graphics.Bitmap;
import android.util.Log;

public class Ball implements Circle {
	private float mCenterX;
	private float mCenterY;
	private float mRadius;
	// BackgroundPicture
	private Bitmap mPicture;
	// Speed in X axis.
	private float mSpeedX;
	// Speed in Y axis.
	private float mSpeedY;

	public Ball(float x, float y, float r, Bitmap picture) {
		mCenterX = x;
		mCenterY = y;
		mRadius = r;
		mPicture = picture;
		mSpeedX = 0;
		mSpeedY = 0;
	}

	public Bitmap getPicture() {
		return mPicture;
	}

	public void setPicture(Bitmap picture) {
		mPicture = picture;
	}

	@Override
	public float getSpeedX() {
		return mSpeedX;
	}

	@Override
	public float getSpeedY() {
		return mSpeedY;
	}

	@Override
	public void setSpeed(float speedX, float speedY) {
		mSpeedX = speedX;
		mSpeedY = speedY;
		Log.d("111", "SetSpeed x = " + speedX + ", y= " + speedY);
	}

	@Override
	public float getCenterX() {
		return mCenterX;
	}

	@Override
	public float getCenterY() {
		return mCenterY;
	}

	@Override
	public float getRadius() {
		return mRadius;
	}

	@Override
	public void setCenterX(float x) {
		mCenterX = x;
	}

	@Override
	public void setCenterY(float y) {
		mCenterY = y;
	}

	@Override
	public void setRadius(float r) {
		mRadius = r;
	}
}
