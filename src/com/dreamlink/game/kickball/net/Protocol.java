package com.dreamlink.game.kickball.net;

/**
 * Protocol format: 4 bytes + n bytes.</br>
 * 
 * 4 bytes means message type.</br>
 * 
 * n bytes means message content.</br>
 * 
 */
public class Protocol {
	/** Length of message type (bytes) */
	public static final int TYPE_LENGTH = 4;

	/** Ball comes from the other player. */
	public static final int TYPE_BALL_COME = 100;
	/** You win one point. */
	public static final int TYPE_YOU_WIN_ONE_POINT = 101;
	/** The other player quit the game. */
	public static final int TYPE_THE_PLAYER_QUIT = 102;

}
