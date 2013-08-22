package com.dreamlink.game.kickball.util;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

public class DrawTextUtil {

	/**
	 * Get the font height.
	 * 
	 * @param paint
	 * @return
	 */
	public static float getFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return fm.descent - fm.ascent;
	}
}
