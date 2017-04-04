package com.int0blivion.socketclient.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.int0blivion.socketclient.SocketClientApplication;
import com.int0blivion.socketclient.di.ActivityComponent;

/**
 * Created by Matt on 4/3/2017.
 */
public abstract class BaseDependencyInjectingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        injectClass(SocketClientApplication.getActivityComponent());
    }

    protected abstract void injectClass(@NonNull ActivityComponent component);
}
