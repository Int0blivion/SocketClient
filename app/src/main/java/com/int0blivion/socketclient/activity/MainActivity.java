package com.int0blivion.socketclient.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.int0blivion.socketclient.MainController;
import com.int0blivion.socketclient.R;
import com.int0blivion.socketclient.di.ActivityComponent;
import com.mikepenz.materialdrawer.Drawer;

import java.net.InetSocketAddress;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseDependencyInjectingActivity {
    @Inject
    MainController mMainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMainController.initialize(this, toolbar);
        mMainController.connect(new InetSocketAddress("192.168.50.192", 11000));
    }

    @Override
    protected void injectClass(@NonNull ActivityComponent component) {
        component.inject(this);
    }

    @Override
    public void onBackPressed() {
        if (!mMainController.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mMainController.disconnect();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mMainController.onTouchEvent(e)) {
            return true;
        }

        return super.onTouchEvent(e);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_disconnect:
            {
                mMainController.disconnect();
                return true;
            }

            case R.id.action_reconnect:
            {
                mMainController.reconnect();
                return true;
            }

            case R.id.action_toggle_keyboard:
            {
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }
}
