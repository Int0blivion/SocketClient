package com.int0blivion.socketclient.connection;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.io.IOException;

/**
 * Created by Matt on 4/2/2017.
 */
public abstract class SocketRunnable implements Runnable {
    @Override
    public void run() {
        try {
            sendData();
            onSuccess();
        } catch (Exception e) {
            onException(e);
        }
    }

    protected abstract void sendData() throws IOException;

    protected abstract void onSuccess();

    protected abstract void onException(@NonNull Exception e);
}
