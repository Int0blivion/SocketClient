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

    private Socket mSocket;
    private SocketAddress mAddress;

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
        mAddress = Preconditions.checkNotNull(address, "address");

        final ConnectionRunnable connectionRunnable = new ConnectionRunnable(mConnectionCallback, mSocket, mAddress, TIMEOUT);
        mExecutor.execute(connectionRunnable);
    }

    public void reconnect() {
        // TODO: Is this safe to do on the main thread?
         try {
            mSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSocket = new Socket();
        final ConnectionRunnable connectionRunnable = new ConnectionRunnable(mConnectionCallback, mSocket, mAddress, TIMEOUT);
        mExecutor.execute(connectionRunnable);
    }

    public void disconnect() {
        final DisconnectRunnable runnable = new DisconnectRunnable(mConnectionCallback, mSocket);
        mExecutor.execute(runnable);
    }

    public void sendPacket(PacketType packet) {
        final ByteOutputRunnable runnable = new ByteOutputRunnable(mConnectionCallback, mSocket, packet);
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
        private final int mTimeout;

        public ConnectionRunnable(@NonNull ConnectionCallback callback, @NonNull Socket socket, @NonNull SocketAddress address, int timeout) {
            super(callback);

            mSocket = Preconditions.checkNotNull(socket, "socket");
            mAddress = Preconditions.checkNotNull(address, "address");
            mTimeout = timeout;
        }

        @Override
        protected void sendData() throws IOException {
            mSocket.connect(mAddress, mTimeout);
        }

        @Override
        protected void onSuccess() {
            mConnectionCallback.onConnected();
        }
    }

    /**
     * Socket runnable to asynchronously disconnect a given socket
     */
    private static class DisconnectRunnable extends ByteOutputRunnable {
        public DisconnectRunnable(@NonNull ConnectionCallback callback, @NonNull Socket socket) {
            super(callback, socket, PacketType.DISCONNECT);
        }

        @Override
        protected void onSuccess() {
            mConnectionCallback.onDisconnected();
        }
    }

    /**
     * Socket runnable to send a single byte of data asynchronously
     */
    private static class ByteOutputRunnable extends SocketRunnable {
        private final PacketType mPacket;
        private final Socket mSocket;

        public ByteOutputRunnable(@NonNull ConnectionCallback callback, @NonNull Socket socket, PacketType packet) {
            super(callback);

            mSocket = Preconditions.checkNotNull(socket);
            mPacket = packet;
        }

        @Override
        protected void sendData() throws IOException {
            final DataOutputStream outputStream = new DataOutputStream(mSocket.getOutputStream());

            outputStream.writeByte(mPacket.value());
            outputStream.flush();
        }

        @Override
        protected void onSuccess() {
            mConnectionCallback.onStatusUpdate(String.format("Successfully sent packet: %s", mPacket.name()));
        }
    }

    /**
     * Socket runnable to send a scroll event asynchronously
     */
    private static class ScrollOutputRunnable extends SocketRunnable {
        private final byte mByte;
        private final float mX;
        private final float mY;
        private final Socket mSocket;

        public ScrollOutputRunnable(@NonNull ConnectionCallback callback, @NonNull Socket socket, byte b, float x, float y) {
            super(callback);

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
            mConnectionCallback.onStatusUpdate("Successfully sent scroll event");
        }
    }

    /**
     * Socket runnable to send a string of data asynchronously
     */
    private static class StringOutputRunnable extends SocketRunnable {
        private final String mString;
        private final Socket mSocket;

        public StringOutputRunnable(@NonNull ConnectionCallback callback, @NonNull Socket socket, @NonNull String string) {
            super(callback);

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
            mConnectionCallback.onStatusUpdate(String.format("Successfully sent string %s", mString));
        }
    }
}
