package com.dreamlink.game.kickball.net;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import com.dreamlink.communication.aidl.User;
import com.dreamlink.communication.lib.util.ArrayUtil;

import android.util.Log;

public class ProtocolDecoder {
	private static final String TAG = "ProtocolDecoder";
	private Callback mCallback;

	public ProtocolDecoder(Callback callback) {
		mCallback = callback;
	}

	public void decode(byte[] data, User sendUser) {
		if (data.length < Protocol.TYPE_LENGTH) {
			Log.e(TAG, "decode() data length error." + data.length);
			return;
		}
		int msgType = ArrayUtil.byteArray2Int(Arrays.copyOfRange(data, 0,
				Protocol.TYPE_LENGTH));
		data = Arrays.copyOfRange(data, Protocol.TYPE_LENGTH, data.length);

		switch (msgType) {
		case Protocol.TYPE_BALL_COME:
			Log.d(TAG, "TYPE_BALL_COME");
			handleMessageBallCome(data);
			break;
		case Protocol.TYPE_YOU_WIN_ONE_POINT:
			Log.d(TAG, "TYPE_YOU_WIN_ONE_POINT");
			handleMessageYouWinOnePoint(data);
			break;
		case Protocol.TYPE_THE_PLAYER_QUIT:
			Log.d(TAG, "TYPE_THE_PLAYER_QUIT");
			handleMessageQuit(data);
			break;
		case Protocol.TYPE_THE_PLAYER_REPLAY:
			Log.d(TAG, "TYPE_THE_PLAYER_REPLAY");
			handMessageReplay(data);
		case Protocol.TYPE_JOIN_GAME:
			Log.d(TAG, "TYPE_JOIN_GAME");
			handMessageJoin(data, sendUser);
			break;
		case Protocol.TYPE_SEARCH_OTHER_PLAYERS:
			Log.d(TAG, "TYPE_JOIN_GAME");
			handMessageSearchPlayers(data, sendUser);
			break;
		default:
			Log.d(TAG, "Unkown message type: " + msgType);
			break;
		}

	}

	private void handMessageSearchPlayers(byte[] data, User sendUser) {
		if (mCallback != null) {
			mCallback.onSearchRequest(sendUser);
		}
	}

	private void handMessageJoin(byte[] data, User sendUser) {
		if (mCallback != null) {
			mCallback.onPlayerJoin(sendUser);
		}
	}

	private void handMessageReplay(byte[] data) {
		if (mCallback != null) {
			mCallback.onPlayerReplay();
		}
	}

	private void handleMessageQuit(byte[] data) {
		if (mCallback != null) {
			mCallback.onPlayerQuit();
		}
	}

	private void handleMessageYouWinOnePoint(byte[] data) {
		if (mCallback != null) {
			mCallback.onYouWinOnePoint();
		}
	}

	private void handleMessageBallCome(byte[] data) {
		float x = 0;
		float speedX = 0;
		float speedY = 0;

		ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
		DataInputStream dataIn = new DataInputStream(byteIn);
		try {
			x = dataIn.readFloat();
			speedX = dataIn.readFloat();
			speedY = dataIn.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			byteIn.close();
			dataIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (mCallback != null) {
			mCallback.onBallcome(x, speedX, speedY);
		}
	}

	public interface Callback {
		void onBallcome(float xPercentInScreenWidth,
				float speedXPercentInScreenWidth,
				float speedYPercentInScreenHeight);

		void onYouWinOnePoint();

		void onPlayerQuit();

		void onPlayerReplay();

		void onPlayerJoin(User player);

		void onSearchRequest(User sendUser);
	}
}
