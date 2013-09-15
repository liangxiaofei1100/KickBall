package com.dreamlink.game.kickball;

import java.util.List;
import java.util.Vector;

import com.dreamlink.communication.aidl.User;
import com.dreamlink.communication.lib.CommunicationManager;
import com.dreamlink.communication.lib.CommunicationManager.OnCommunicationListener;
import com.dreamlink.communication.lib.CommunicationManager.OnConnectionChangeListener;
import com.dreamlink.communication.lib.util.AppUtil;
import com.dreamlink.game.kickball.GameOverView.GameOverCallBack;
import com.dreamlink.game.kickball.GameView.BallCallback;
import com.dreamlink.game.kickball.net.ProtocolDecoder;
import com.dreamlink.game.kickball.net.ProtocolEncoder;
import com.dreamlink.game.kickball.util.Log;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity implements BallCallback,
		ProtocolDecoder.Callback, GameOverCallBack, OnConnectionChangeListener,
		OnCommunicationListener {
	private final static String TAG = "MainActivity";
	private Context mContext;
	private GameView mGameView;
	private GameOverView mGameOverView;
	private ProtocolDecoder mProtocolDecoder;

	private static final int MSG_GAMEOVER = 1;
	private static final int MSG_COMPETITOR_QUIT = 2;
	private static final int MSG_COMPETITOR_REPLAY = 3;
	private static final int MSG_COMPETITOR_JOIN = 4;

	private User mLocalPlayer;
	private Vector<User> mPlayers = new Vector<User>();

	/** The player number of the game. */
	private static final int PLAYER_NUMBER = 2;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GAMEOVER:
				mGameOverView.setGameOverResult(msg.arg1);
				mGameOverView.setVisibility(View.VISIBLE);
				break;

			case MSG_COMPETITOR_QUIT:
				Toast.makeText(mContext, R.string.player_quit,
						Toast.LENGTH_LONG).show();
				finish();
				break;

			case MSG_COMPETITOR_REPLAY:
				mGameOverView.setVisibility(View.GONE);
				startGame();
				break;

			case MSG_COMPETITOR_JOIN:
				User user = (User) msg.obj;
				Toast.makeText(
						mContext,
						getString(R.string.player_join, user.getUserName(),
								mPlayers.size()), Toast.LENGTH_LONG).show();

				if (mPlayers.size() >= PLAYER_NUMBER) {
					// Start game.
					startGame();
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_main);

		mCommunicationManager = new CommunicationManager(
				getApplicationContext());

		mGameView = (GameView) findViewById(R.id.gameView);
		mGameView.setBallCallback(this);

		mGameOverView = (GameOverView) findViewById(R.id.gameOverView);
		mGameOverView.setCallBack(this);

		mProtocolDecoder = new ProtocolDecoder(this);

		mAppId = AppUtil.getAppID(this);

		boolean result = mCommunicationManager.connectCommunicatonService(this,
				this, mAppId);
		if (result) {
			Log.d(TAG, "connectCommunicationService success.");
		} else {
			Toast.makeText(this, "connectCommunicationService fail.",
					Toast.LENGTH_LONG).show();
			Log.e(TAG, "bind service fail.");
		}
	}

	@Override
	protected void onDestroy() {
		mCommunicationManager.disconnectCommunicationService();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			quitGame();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGameView.onTouchEvent(event)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Reset and start game();
	 */
	private void startGame() {
		if (mPlayers.size() >= PLAYER_NUMBER) {
			boolean holdTheBall = false;
			if (mLocalPlayer.getUserID() > mPlayers.get(1).getUserID()) {
				holdTheBall = true;
			} else {
				holdTheBall = false;
			}
			mGameView.startGame(holdTheBall, true);
		} else {
			Log.e(TAG, "startGame() error, player count: " + mPlayers.size());
			mGameView.startGame(true, false);
		}
	}

	private boolean checkCommunicationConnection() {
		boolean result = true;
		List<User> users = mCommunicationManager.getAllUser();
		if (users != null && users.size() > 1) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	// Callback of Game view begin.
	@Override
	public void onBallOut(float xPercentInScreenWidth,
			float speedXPercentInScreenWidth, float speedYPercentInScreenHeight) {
		// Send message to competitor.
		// In competitor view, the speedY is in opposite direction.
		speedYPercentInScreenHeight = speedYPercentInScreenHeight * -1;
		byte[] data = ProtocolEncoder.encodeBallOut(xPercentInScreenWidth,
				speedXPercentInScreenWidth, speedYPercentInScreenHeight);
		sendMessageToAllCompetitor(data);
	}

	@Override
	public void onLoseOneGoal() {
		// Send message to competitor.
		byte[] data = ProtocolEncoder.encodeLoseOneGoal();
		sendMessageToAllCompetitor(data);
	}

	@Override
	public void onGameOver(int result) {
		Log.d(TAG, "onGameOver result = " + result);
		Message message = mHandler.obtainMessage();
		message.what = MSG_GAMEOVER;
		message.arg1 = result;
		mHandler.sendMessage(message);
	}

	// Callback of Game view end.

	private void quitGame() {
		Log.d(TAG, "quitGame");
		// Send message to competitor.
		byte[] data = ProtocolEncoder.encodeQuitGame();
		sendMessageToAllCompetitor(data);
		finish();
	}

	private void handleMessage(byte[] data, User sendUser) {
		mProtocolDecoder.decode(data, sendUser);
	}

	private void addPlayer(User player) {
		Log.d(TAG, "addPlayer(): id = " + player.getUserID() + ", name = "
				+ player.getUserName());
		boolean isAdded = false;
		for (User user : mPlayers) {
			if (user.getUserID() == player.getUserID()) {
				isAdded = true;
			}
		}
		if (!isAdded) {
			mPlayers.add(player);

			if (player != mLocalPlayer) {
				Message message = mHandler.obtainMessage();
				message.what = MSG_COMPETITOR_JOIN;
				message.obj = player;
				mHandler.sendMessage(message);
			}
		}
	}

	// Call back of GameOverView begin
	@Override
	public void onGameOverRepaly() {
		mGameOverView.setVisibility(View.GONE);
		startGame();
		byte[] data = ProtocolEncoder.encodeReplayGame();
		sendMessageToAllCompetitor(data);
	}

	@Override
	public void onGameOverQuit() {
		quitGame();
	}

	// Call back of GameOverView end

	// Call back of protocol decoder begin
	@Override
	public void onBallcome(float xPercentInScreenWidth,
			float speedXPercentInScreenWidth, float speedYPercentInScreenHeight) {
		mGameView.ballComesFromCompetitor(xPercentInScreenWidth,
				speedXPercentInScreenWidth, speedYPercentInScreenHeight);
	}

	@Override
	public void onYouWinOnePoint() {
		mGameView.winOneGoal();
	}

	@Override
	public void onPlayerQuit() {
		Message message = mHandler.obtainMessage();
		message.what = MSG_COMPETITOR_QUIT;
		mHandler.sendMessage(message);
	}

	@Override
	public void onPlayerReplay() {
		Message message = mHandler.obtainMessage();
		message.what = MSG_COMPETITOR_REPLAY;
		mHandler.sendMessage(message);
	}

	@Override
	public void onPlayerJoin(User player) {
		addPlayer(player);
	}

	@Override
	public void onSearchRequest(User sendUser) {
		// Tell the searcher we join the game.
		byte[] data = ProtocolEncoder.encodeJoinGame();
		sendMessageToSingleCompetitor(data, sendUser);
	}

	// Call back of protocol decoder end

	// Communication Service begin
	private int mAppId;
	private CommunicationManager mCommunicationManager;

	private void sendMessageToSingleCompetitor(byte[] data, User receiver) {
		mCommunicationManager.sendMessage(data, receiver);
	}

	private void sendMessageToAllCompetitor(byte[] data) {
		mCommunicationManager.sendMessageToAll(data);
	}

	@Override
	public void onReceiveMessage(byte[] msg, User sendUser) {
		handleMessage(msg, sendUser);
	}

	@Override
	public void onUserConnected(User user) {
		Log.d(TAG, "onUserConnected() " + user);
	}

	@Override
	public void onUserDisconnected(User user) {
		Log.d(TAG, "onUserDisconnected() " + user);
	}

	@Override
	public void onCommunicationConnected() {
		if (!checkCommunicationConnection()) {
			Toast.makeText(mContext, "无网络连接，请先建立连接后再启动游戏。", Toast.LENGTH_LONG)
					.show();
			return;
		}
		// Search other players.
		byte[] searchData = ProtocolEncoder.encodeSearchOtherPlayers();
		sendMessageToAllCompetitor(searchData);
		// Tell other players we join the game
		byte[] joinData = ProtocolEncoder.encodeJoinGame();
		sendMessageToAllCompetitor(joinData);

		mLocalPlayer = mCommunicationManager.getLocalUser();
		if (mLocalPlayer != null) {
			addPlayer(mLocalPlayer);
		} else {
			Log.e(TAG, "onCommunicationReady get local user fail. ");
		}
	}

	@Override
	public void onCommunicationDisconnected() {
		Log.d(TAG, "onCommunicationDisconnected");
	}
	// Communication Service end
}
