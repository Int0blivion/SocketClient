package com.int0blivion.socketclient.connection;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.io.IOException;

/**
 * Created by Matt on 4/2/2017.
 */
public abstract class SocketRunnable implements Runnable {

    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    protected final ConnectionCallback mConnectionCallback;

    public SocketRunnable(@NonNull ConnectionCallback callback) {
        mConnectionCallback = Preconditions.checkNotNull(callback, "callback");
    }

    @Override
    public void run() {
        try {
            sendData();

            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    onSuccess();
                }
            });
        } catch (final Exception e) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    onException(e);
                }
            });
        }
    }

    protected abstract void sendData() throws IOException;

    /**
     * Success callback executed after a successful run
     *
     * Note: this method is executed on the main thread of the application
     */
    protected abstract void onSuccess();

    /**
     * Callback executed if an exception occurs during the execution of the runnable
     *
     * Note: this method is executed on the main thread of the application
     */
    protected void onException(@NonNull Exception e) {
        mConnectionCallback.onStatusUpdate(e.toString());
    }
}
