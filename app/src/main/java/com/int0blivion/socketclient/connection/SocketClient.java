package com.int0blivion.socketclient.connection;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Preconditions;
import com.int0blivion.socketclient.PacketType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Matt on 4/2/2017.
 */
public class SocketClient {
    private static final int TIMEOUT = 1000;

    private final String mHost;
    private final Executor mExecutor;
    private final int mPort;

    private Socket mSocket;

    public SocketClient(@NonNull String host, int port) {
        this(host, port, Executors.newSingleThreadExecutor());
    }

    @VisibleForTesting
    SocketClient (@NonNull String host, int port, @NonNull Executor executor) {
        mHost = Preconditions.checkNotNull(host);
        mExecutor = Preconditions.checkNotNull(executor);
        mPort = port;
    }

    public void connect() {

        mSocket = new Socket();
        final InetSocketAddress socketAddress = new InetSocketAddress(mHost, mPort);

        final ConnectionRunnable connectionRunnable = new ConnectionRunnable(mSocket, socketAddress);
        mExecutor.execute(connectionRunnable);
    }

    public void disconnect()
    {
        writeByte(PacketType.DISCONNECT);
    }

    public void writeByte(byte b)
    {
        final ByteOutputRunnable runnable = new ByteOutputRunnable(mSocket, b);
        mExecutor.execute(runnable);
    }

    /**
     * Writes bytes for a scroll event
     * @param b PacketType
     * @param x - distance scrolled X
     * @param y - distance scrolled Y
     */
    public void writeBytes(byte b, float x, float y)
    {
       final ScrollOutputRunnable runnable = new ScrollOutputRunnable(mSocket, b, x, y);
        mExecutor.execute(runnable);
    }

    public void writeString(@NonNull String string)
    {
       final StringOutputRunnable runnable = new StringOutputRunnable(mSocket, string);
        mExecutor.execute(runnable);
    }

    private static class ConnectionRunnable extends SocketRunnable {
        private final Socket mSocket;
        private final InetSocketAddress mAddress;

        public ConnectionRunnable(@NonNull Socket socket, @NonNull InetSocketAddress address) {
            mSocket = Preconditions.checkNotNull(socket, "socket");
            mAddress = Preconditions.checkNotNull(address, "address");
        }

        @Override
        protected void sendData() throws IOException {
            mSocket.connect(mAddress);
        }

        @Override
        protected void onException(@NonNull Exception e) {

        }
    }

    private static class ByteOutputRunnable extends SocketRunnable {
        private final byte mByte;
        private final Socket mSocket;

        public ByteOutputRunnable(@NonNull Socket socket, byte b) {
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
        protected void onException(@NonNull Exception e) {

        }
    }

    private static class ScrollOutputRunnable extends SocketRunnable {
        private final byte mByte;
        private final float mX;
        private final float mY;
        private final Socket mSocket;

        public ScrollOutputRunnable(@NonNull Socket socket, byte b, float x, float y) {
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
        protected  void onException(@NonNull Exception e) {

        }
    }

    private static class StringOutputRunnable extends SocketRunnable {
        private final String mString;
        private final Socket mSocket;

        public StringOutputRunnable(@NonNull Socket socket, @NonNull String string) {
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
        protected void onException(@NonNull Exception e) {

        }
    }
}
