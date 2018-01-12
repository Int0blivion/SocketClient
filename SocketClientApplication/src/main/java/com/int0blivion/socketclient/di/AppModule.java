package com.int0blivion.socketclient.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Matt on 4/3/2017.
 */
@Module
public class AppModule {
    private Context mApplicationContext;

    public AppModule(Context context) {
        mApplicationContext = context;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return mApplicationContext;
    }
}