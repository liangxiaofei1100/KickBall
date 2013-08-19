package com.dreamlink.game.kickball.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.dreamlink.game.kickball.util.ArrayUtil;

public class ProtocolEncoder {

	public static byte[] encodeBallOut(float x, float speedX, float speedY) {
		byte[] typeData = ArrayUtil.int2ByteArray(Protocol.TYPE_BALL_COME);
		
		byte[] ballComeData = null;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(byteOut);
		try {
			dataOut.writeFloat(x);
			dataOut.writeFloat(speedX);
			dataOut.writeFloat(speedY);
			dataOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ballComeData = byteOut.toByteArray();
		
		try {
			byteOut.close();
			dataOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ArrayUtil.join(typeData,ballComeData);
	}

	public static byte[] encodeLoseOneGoal() {
		return ArrayUtil.int2ByteArray(Protocol.TYPE_YOU_WIN_ONE_POINT);
	}

	public static byte[] encodeQuitGame() {
		return ArrayUtil.int2ByteArray(Protocol.TYPE_THE_PLAYER_QUIT);
	}
}
