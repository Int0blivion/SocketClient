package com.int0blivion.socketclient.di;

import com.int0blivion.socketclient.MainController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Matt on 4/3/2017.
 */
@Module
public class ControllerModule {

    @Provides
    @Singleton
    MainController provideMainController() {
        return new MainController();
    }
}
