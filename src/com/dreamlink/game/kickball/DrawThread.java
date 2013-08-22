package com.dreamlink.game.kickball;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class DrawThread extends Thread {
	private static final String TAG = "DrawThread";
	private GameView mGameView;
	private SurfaceHolder mSurfaceHolder;
	private boolean mDraw;
	private static final int SLEEP_TIME = 33;

	public DrawThread(GameView gameView, SurfaceHolder surfaceHolder) {
		setName("Kick ball DrawThread");
		mGameView = gameView;
		mSurfaceHolder = surfaceHolder;
	}

	public void startDraw(boolean draw) {
		mDraw = draw;
	}

	@Override
	public void run() {
		long startTime = 0;
		Canvas canvas = null;
		while (mDraw) {
			startTime = System.currentTimeMillis();
			try {
				canvas = mSurfaceHolder.lockCanvas();
				mGameView.drawGame(canvas);
				Thread.sleep(Math.max(0,
						SLEEP_TIME - System.currentTimeMillis() - startTime));
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			} finally {
				if (canvas != null) {
					mSurfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}

	}
}
