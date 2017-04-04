package com.int0blivion.socketclient.connection;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Preconditions;
import com.int0blivion.socketclient.PacketType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Matt on 4/2/2017.
 */
public class SocketController {
    private static final int TIMEOUT = 1000;

    private final Executor mExecutor;
    private final ConnectionCallback mConnectionCallback;
    private final Socket mSocket;

    public SocketController(@NonNull ConnectionCallback callback) {
        this(callback, Executors.newSingleThreadExecutor(), new Socket());
    }

    @VisibleForTesting
    SocketController(@NonNull ConnectionCallback callback, @NonNull Executor executor, @NonNull Socket socket) {
        mConnectionCallback = Preconditions.checkNotNull(callback);
        mExecutor = Preconditions.checkNotNull(executor);
        mSocket = Preconditions.checkNotNull(socket, "socket");
    }

    public void connect(@NonNull InetSocketAddress address) {
        Preconditions.checkNotNull(address, "address");

        final ConnectionRunnable connectionRunnable = new ConnectionRunnable(mConnectionCallback, mSocket, address);
        mExecutor.execute(connectionRunnable);
    }

    public void reconnect() {
        final ConnectionRunnable connectionRunnable = new ConnectionRunnable(mConnectionCallback, mSocket, mSocket.getLocalSocketAddress());
        mExecutor.execute(connectionRunnable);
    }

    public void disconnect() {
        writeByte(PacketType.DISCONNECT);
    }

    public void writeByte(byte b) {
        final ByteOutputRunnable runnable = new ByteOutputRunnable(mConnectionCallback, mSocket, b);
        mExecutor.execute(runnable);
    }

    /**
     * Writes bytes for a scroll event
     * @param b PacketType
     * @param x - distance scrolled X
     * @param y - distance scrolled Y
     */
    public void writeBytes(byte b, float x, float y) {
        final ScrollOutputRunnable runnable = new ScrollOutputRunnable(mConnectionCallback, mSocket, b, x, y);
        mExecutor.execute(runnable);
    }

    public void writeString(@NonNull String string) {
        final StringOutputRunnable runnable = new StringOutputRunnable(mConnectionCallback, mSocket, string);
        mExecutor.execute(runnable);
    }

    /**
     * Socket Runnable to establish a connection asynchronously
     */
    private static class ConnectionRunnable extends SocketRunnable {
        private final Socket mSocket;
        private final SocketAddress mAddress;
        private final ConnectionCallback mConnectionCallback;

        public ConnectionRunnable(@NonNull ConnectionCallback callback, @NonNull Socket socket, @NonNull SocketAddress address) {
            mConnectionCallback = Preconditions.checkNotNull(callback, "callback");
            mSocket = Preconditions.checkNotNull(socket, "socket");
            mAddress = Preconditions.checkNotNull(address, "address");
        }

        @Override
        protected void sendData() throws IOException {
            mSocket.connect(mAddress);
        }

        @Override
        protected void onSuccess() {

        }

        @Override
        protected void onException(@NonNull Exception e) {
            mConnectionCallback.onStatusUpdate(e.getMessage());
        }
    }

    private static class ByteOutputRunnable extends SocketRunnable {
        private final byte mByte;
        private final Socket mSocket;
        private final ConnectionCallback mConnectionCallback;

        public ByteOutputRunnable(@NonNull ConnectionCallback callback, @NonNull Socket socket, byte b) {
            mConnectionCallback = Preconditions.checkNotNull(callback, "callback");
            mSocket = Preconditions.checkNotNull(socket);
            mByte = b;
        }

        @Override
        protected void sendData() throws IOException {
            final DataOutputStream outputStream = new DataOutputStream(mSocket.getOutputStream());

            outputStream.writeByte(mByte);
            outputStream.flush();
        }

        @Override
        protected void onSuccess() {

        }

        @Override
        protected void onException(@NonNull Exception e) {
            mConnectionCallback.onStatusUpdate(e.getMessage());
        }
    }

    private static class ScrollOutputRunnable extends SocketRunnable {
        private final byte mByte;
        private final float mX;
        private final float mY;
        private final Socket mSocket;
        private final ConnectionCallback mConnectionCallback;

        public ScrollOutputRunnable(@NonNull ConnectionCallback callback, @NonNull Socket socket, byte b, float x, float y) {
            mConnectionCallback = Preconditions.checkNotNull(callback, "callback");
            mSocket = Preconditions.checkNotNull(socket);
            mByte = b;
            mX = x;
            mY = y;
        }

        @Override
        protected void sendData() throws IOException {
            final DataOutputStream outputStream = new DataOutputStream(mSocket.getOutputStream());

            outputStream.write(ByteBuffer.allocate(9).order(ByteOrder.BIG_ENDIAN)
                    .put(mByte).putFloat(mX).putFloat(mY).array());

            outputStream.flush();
        }

        @Override
        protected void onSuccess() {

        }

        @Override
        protected void onException(@NonNull Exception e) {
            mConnectionCallback.onStatusUpdate(e.getMessage());
        }
    }

    private static class StringOutputRunnable extends SocketRunnable {
        private final String mString;
        private final Socket mSocket;
        private final ConnectionCallback mConnectionCallback;

        public StringOutputRunnable(@NonNull ConnectionCallback callback, @NonNull Socket socket, @NonNull String string) {
            mConnectionCallback = Preconditions.checkNotNull(callback, "callback");
            mSocket = Preconditions.checkNotNull(socket);
            mString = string;
        }

        @Override
        protected void sendData() throws IOException {
            final DataOutputStream outputStream = new DataOutputStream(mSocket.getOutputStream());
            final byte[] bytes = mString.getBytes();

            outputStream.write(bytes.length);
            outputStream.write(bytes);
            outputStream.flush();
        }

        @Override
        protected void onSuccess() {

        }

        @Override
        protected void onException(@NonNull Exception e) {
            mConnectionCallback.onStatusUpdate(e.getMessage());
        }
    }
}
