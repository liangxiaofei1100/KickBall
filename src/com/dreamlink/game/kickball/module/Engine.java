package com.dreamlink.game.kickball.module;

public class Engine {

	/**
	 * The resistance acceleration in pixel per second.
	 */
	public static final float RESISTANCE_ACCELERATION_PIXEL_PER_SECOND = 1;

	public static final float MAX_SPEED_PIXEL_PER_SECOND = 8;

	/**
	 * If c1 collision with c2 return true, else return false.
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static boolean detectCollision(Circle c1, Circle c2) {
		return calculateDistance(c1.getCenterX(), c1.getCenterY(),
				c2.getCenterX(), c2.getCenterY()) <= c1.getRadius()
				+ c2.getRadius();
	}

	/**
	 * When c1 push c2 to move.
	 * 
	 * @param c1
	 * @param c2
	 */
	public static void circleCollide(Circle c1, Circle c2) {
		float c1X = c1.getCenterX();
		float c1Y = c1.getCenterY();
		float c1SpeedX = c1.getSpeedX();
		float c1SpeedY = c1.getSpeedY();

		float c2X = c2.getCenterX();
		float c2Y = c2.getCenterY();
		float c2SpeedX = c2.getSpeedX();
		float c2SpeedY = c2.getSpeedY();

		c1SpeedX += c2SpeedX * -1;
		c1SpeedY += c2SpeedY * -1;

		float c2SpeedX1 = (c2X - c1X) * (c2X - c1X)
				/ ((c2X - c1X) * (c2X - c1X) + (c2Y - c1Y) * (c2Y - c1Y))
				* c1SpeedX;
		int directionY = c2Y > c1Y ? 1 : -1;
		float c2SpeedY1 = c2SpeedX1 * (c2Y - c1Y) / (c2X - c1X) * directionY;

		float c2SpeedX2 = (c2Y - c1Y) * (c2Y - c1Y)
				/ ((c2X - c1X) * (c2X - c1X) + (c2Y - c2X) * (c2Y - c2X))
				* c1SpeedY;
		float c2SpeedY2 = c2SpeedX2 * (c2X - c1X) / (c2Y - c1Y) * directionY;

		c2SpeedX = Math.min(c2SpeedX1 + c2SpeedX2, MAX_SPEED_PIXEL_PER_SECOND);
		c2SpeedY = Math.min(c2SpeedY1 + c2SpeedY2, MAX_SPEED_PIXEL_PER_SECOND);
		c2.setSpeed(c2SpeedX, c2SpeedY);
	}

	public static boolean detectPointInCircle(Circle circle, float x, float y) {
		return calculateDistance(circle.getCenterX(), circle.getCenterY(), x, y) <= circle
				.getRadius();
	}

	public static double calculateDistance(float x1, float y1, float x2,
			float y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

}
