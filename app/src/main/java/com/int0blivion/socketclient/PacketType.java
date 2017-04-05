package com.int0blivion.socketclient;

/**
 * Created by Matt on 1/24/2016.
 */
public enum PacketType {
    SINGLE_CLICK((byte) 0),
    DOUBLE_CLICK((byte) 1),
    SCROLL((byte) 2),
    RIGHT_CLICK((byte) 3),
    START_KEYBOARD((byte) 4),
    END_KEYBOARD((byte) 5),
    SLEEP((byte) 252),
    RESTART((byte) 253),
    SHUTDOWN((byte) 254),
    DISCONNECT((byte) 255);
    
    private final byte mValue;
    
    PacketType(byte b) {
        mValue = b;    
    }
    
    public byte value() {
        return mValue;
    }
}
