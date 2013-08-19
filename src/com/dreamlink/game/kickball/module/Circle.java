package com.dreamlink.game.kickball.module;

public interface Circle {
	float getSpeedX();

	float getSpeedY();

	void setSpeed(float speedX, float speedY);

	void setCenterX(float x);

	void setCenterY(float y);

	void setRadius(float r);

	float getCenterX();

	float getCenterY();

	float getRadius();
}
