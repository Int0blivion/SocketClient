package com.int0blivion.socketclient;

import android.app.Application;

import com.int0blivion.socketclient.di.ActivityComponent;
import com.int0blivion.socketclient.di.AppModule;
import com.int0blivion.socketclient.di.ControllerModule;
import com.int0blivion.socketclient.di.DaggerActivityComponent;

/**
 * Created by Matt on 4/3/2017.
 */
public class SocketClientApplication extends Application {
    private static ActivityComponent mActivityComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mActivityComponent = DaggerActivityComponent.builder()
                // list of modules that are part of this component need to be created here too
                .appModule(new AppModule(this))
                .controllerModule(new ControllerModule())
                .build();
    }

    /**
     * Get the activity component used for injection
     *
     * We are assuming that if anything is able to call this, an instance of DGSCApplication
     *  must exist.
     * @return
     */
    public static ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }
}
