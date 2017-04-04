package com.int0blivion.socketclient;

/**
 * Created by Matt on 1/24/2016.
 */
public class PacketType
{
    public static final byte SINGLE_CLICK = 0b0;

    public static final byte DOUBLE_CLICK = 0b1;

    public static final byte SCROLL = 0b10;

    public static final byte RIGHT_CLICK = 0b11;

    public static final byte START_KEYBOARD = (byte) 4;

    public static final byte END_KEYBOARD = (byte) 5;

    public static final byte SLEEP = (byte) 252;

    public static final byte RESTART = (byte) 253;

    public static final byte SHUTDOWN = (byte) 254;

    public static final byte DISCONNECT = (byte) 255;
}
