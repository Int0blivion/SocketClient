package com.int0blivion.socketclient;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Matt on 1/23/2016.
 */
@SuppressWarnings("unused")
public class SocketConnection extends AsyncTask<Void, Void, Exception>
{
    private String _host;
    private int _port;
    private final int TIMEOUT = 1000;

    private MainView controller;
    private Socket socket = null;

    public SocketConnection(MainView controller)
    {
        _host = "192.168.50.192";
        _port = 11000;
        this.controller = controller;
    }

    public SocketConnection(String host, int port, MainView controller)
    {
        _host = host;
        _port = port;
        this.controller = controller;
    }

    @Override
    protected Exception doInBackground(Void... params)
    {
        try
        {
            if(socket == null)
            {
                socket = new Socket();
                InetSocketAddress socketAddress = new InetSocketAddress(_host, _port);

                socket.connect(socketAddress, TIMEOUT);
            }

            controller.displayText("Attempting connection to: " + socket.getRemoteSocketAddress().toString());
        }
        catch (Exception e)
        {
            return e;
        }

        return null;
    }

    /**
     * Display message indicating whether connection was successful
     *
     * @param param
     */
    protected void onPostExecute(Exception param)
    {
        if(param != null)
        {
            if(param instanceof SocketTimeoutException)
            {
                param.printStackTrace();

                controller.appendText(Util.formatErrorText("Error: Connection Timed Out"));
            }
            else
            {
                controller.appendText("Connection Failed");
            }
        }
        else
        {
            controller.appendText("Connection Successful");
        }
    }

    public void disconnectSocket()
    {
        try
        {
            writeByte(PacketType.DISCONNECT);
//            socket.shutdownInput();
//            socket.shutdownOutput();

            controller.appendText("Socket Disconnected");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            controller.displayText("Error: View Logs");
        }
    }

    public void writeByte(byte b)
    {
        try
        {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.writeByte(b);
            outputStream.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            controller.displayText("Error: View Logs");
        }
    }

    /**
     * Writes bytes for a scroll event
     * @param b PacketType
     * @param x - distance scrolled X
     * @param y - distance scrolled Y
     */
    public void writeBytes(byte b, float x, float y)
    {
        try
        {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.write(ByteBuffer.allocate(9).order(ByteOrder.BIG_ENDIAN)
                    .put(b).putFloat(x).putFloat(y).array());

            outputStream.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            controller.displayText("Error: View Logs");
        }
    }

    public void writeString(String s)
    {
        try
        {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            byte[] bytes = s.getBytes();

            outputStream.write(bytes.length);
            outputStream.write(bytes);
            outputStream.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            controller.displayText("Error: View Logs");
        }
    }
}
