package com.int0blivion.socketclient.connection;

import android.support.annotation.Nullable;

/**
 * Created by Matt on 4/2/2017.
 */
public interface ConnectionCallbacks {

    void onConnected();

    void onDisconnected();

    void onStatusUpdate(@Nullable String status);
}
