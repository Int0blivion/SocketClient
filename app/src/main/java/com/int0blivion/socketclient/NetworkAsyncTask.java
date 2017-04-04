package com.int0blivion.socketclient;

import android.os.AsyncTask;

/**
 * Created by Matt on 1/23/2016.
 */
public class NetworkAsyncTask extends AsyncTask <MainView, Void, SocketConnection>
{
    @Override
    protected SocketConnection doInBackground(MainView[] params)
    {
        if(params != null && params[0] != null)
        {
            SocketConnection sc = new SocketConnection(params[0]);

            return sc;
        }

        return null;
    }
}
