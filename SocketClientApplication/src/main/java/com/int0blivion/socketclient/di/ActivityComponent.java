package com.int0blivion.socketclient.di;

import com.int0blivion.socketclient.MainController;
import com.int0blivion.socketclient.activity.BaseDependencyInjectingActivity;
import com.int0blivion.socketclient.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Matt on 4/3/2017.
 */
@Singleton
@Component(modules = {AppModule.class, ControllerModule.class})
public interface ActivityComponent {

    void inject(BaseDependencyInjectingActivity activity);

    void inject(MainActivity activity);

    void inject(MainController controller);
}

