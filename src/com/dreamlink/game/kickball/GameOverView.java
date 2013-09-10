package com.dreamlink.game.kickball;

import com.dreamlink.communication.lib.util.DisplayUtil;
import com.dreamlink.game.kickball.util.DrawTextUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameOverView extends SurfaceView implements SurfaceHolder.Callback {
	@SuppressWarnings("unused")
	private static final String TAG = "GameOverView";

	private GameOverCallBack mCallBack;

	// GameOver
	private Bitmap mGameOverWinBitmap = BitmapFactory.decodeResource(
			getResources(), R.drawable.you_win);
	private Bitmap mGameOverLoseBitmap = BitmapFactory.decodeResource(
			getResources(), R.drawable.you_lose);
	// Button background
	private Bitmap mButtonBackground = BitmapFactory.decodeResource(
			getResources(), R.drawable.button_bg);
	// Game over string
	private String mRetryString;
	private String mQuitString;

	private SurfaceHolder mSurfaceHolder;
	private float mScreenWidth;
	private float mScreenHeight;

	public static final int GAME_OVER_WIN = 1;
	public static final int GAME_OVER_LOSE = 2;
	private int mGameOverResult = 0;
	// Bitmap paint
	private Paint mPaint;
	// Text paint
	private Paint mTextPaint;
	// Win or lose picture position
	private RectF mGameResultRectF;
	// Retry and quit buttons
	private RectF mRetryButtonRectF;
	private RectF mQuitButtonRectF;
	private boolean mIsRetryButtonPressed = false;
	private boolean mIsQuitButtonPressed = false;
	private static final int COLOR_TEXT_PRESSED = Color.WHITE;
	private static final int COLOR_TEXT_NORMAL = Color.BLACK;

	public GameOverView(Context context) {
		super(context);
		init(context);
	}

	public GameOverView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		// init surface
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		setZOrderOnTop(true);
		// init paint
		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setFilterBitmap(true);
		mTextPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
		mTextPaint.setTextSize(mButtonBackground.getHeight() * 2 / 3);
		mTextPaint.setTextAlign(Paint.Align.CENTER);

		setFocusable(true);

		mScreenWidth = DisplayUtil.getScreenWidth(context);
		mScreenHeight = DisplayUtil.getScreenHeight(context);

		mRetryString = context.getString(R.string.retry);
		mQuitString = context.getString(R.string.quit);

		mGameResultRectF = new RectF(0, mScreenHeight / 2
				- mGameOverWinBitmap.getHeight() - 20, mScreenWidth,
				mScreenHeight / 2 - 20);
		mRetryButtonRectF = new RectF(0, mScreenHeight / 2, mScreenWidth,
				mScreenHeight / 2 + mButtonBackground.getHeight());
		mQuitButtonRectF = new RectF(0, mRetryButtonRectF.bottom + 10,
				mScreenWidth, mRetryButtonRectF.bottom + 10
						+ mButtonBackground.getHeight());
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mScreenWidth = width;
		mScreenHeight = height;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceHolder = holder;
		doDraw();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mSurfaceHolder = null;
	}

	public void setGameOverResult(int result) {
		mGameOverResult = result;
		doDraw();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			updateButtonStatus(event.getX(), event.getY());
			return true;
		case MotionEvent.ACTION_MOVE:
			updateButtonStatus(event.getX(), event.getY());
			return true;
		case MotionEvent.ACTION_UP:
			if (mIsRetryButtonPressed) {
				onRetryButtonClick();
			} else if (mIsQuitButtonPressed) {
				onQuitButtonClick();
			}
			mIsQuitButtonPressed = false;
			mIsRetryButtonPressed = false;
			return true;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	private void onQuitButtonClick() {
		if (mCallBack != null) {
			mCallBack.onGameOverQuit();
		}
	}

	private void onRetryButtonClick() {
		if (mCallBack != null) {
			mCallBack.onGameOverRepaly();
		}
	}

	private void updateButtonStatus(float x, float y) {
		if (mRetryButtonRectF.contains(x, y) && !mIsRetryButtonPressed) {
			mIsRetryButtonPressed = true;
			mIsQuitButtonPressed = false;
			doDraw();
		} else if (mQuitButtonRectF.contains(x, y) && !mIsQuitButtonPressed) {
			mIsRetryButtonPressed = false;
			mIsQuitButtonPressed = true;
			doDraw();
		} else if (!mRetryButtonRectF.contains(x, y)
				&& !mQuitButtonRectF.contains(x, y)
				&& (mIsRetryButtonPressed || mIsQuitButtonPressed)) {
			mIsRetryButtonPressed = false;
			mIsQuitButtonPressed = false;
			doDraw();
		}

	}

	public void doDraw() {
		if (mSurfaceHolder != null) {
			Canvas canvas = mSurfaceHolder.lockCanvas();
			if (canvas == null) {
				Log.e(TAG, "doDraw() error, canvas is null.");
				return;
			}
			drawBackground(canvas);
			drawGameOver(canvas);
			mSurfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	private void drawBackground(Canvas canvas) {
		// Clear the canvas.
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
	}

	private void drawGameOver(Canvas canvas) {
		// Draw game result.
		switch (mGameOverResult) {
		case GAME_OVER_WIN:
			canvas.drawBitmap(mGameOverWinBitmap, null, mGameResultRectF,
					mPaint);
			break;
		case GAME_OVER_LOSE:
			canvas.drawBitmap(mGameOverLoseBitmap, null, mGameResultRectF,
					mPaint);
			break;
		default:
			break;
		}
		// Draw button background.
		canvas.drawBitmap(mButtonBackground, null, mRetryButtonRectF, mPaint);
		canvas.drawBitmap(mButtonBackground, null, mQuitButtonRectF, mPaint);
		Paint.FontMetrics fm = mTextPaint.getFontMetrics();
		// Draw retry button text
		if (mIsRetryButtonPressed) {
			mTextPaint.setColor(COLOR_TEXT_PRESSED);
		} else {
			mTextPaint.setColor(COLOR_TEXT_NORMAL);
		}
		canvas.drawText(
				mRetryString,
				mRetryButtonRectF.centerX(),
				mRetryButtonRectF.centerY()
						+ DrawTextUtil.getFontHeight(mTextPaint) / 2
						- fm.descent, mTextPaint);
		// Draw quit button text
		if (mIsQuitButtonPressed) {
			mTextPaint.setColor(COLOR_TEXT_PRESSED);
		} else {
			mTextPaint.setColor(COLOR_TEXT_NORMAL);
		}
		canvas.drawText(
				mQuitString,
				mQuitButtonRectF.centerX(),
				mQuitButtonRectF.centerY()
						+ DrawTextUtil.getFontHeight(mTextPaint) / 2
						- fm.descent, mTextPaint);
	}

	public void setCallBack(GameOverCallBack callBack) {
		mCallBack = callBack;
	}

	public interface GameOverCallBack {
		void onGameOverRepaly();

		void onGameOverQuit();
	}

}
