package com.int0blivion.socketclient;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.int0blivion.socketclient.connection.SocketClient;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnFocusChange;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.editText_KeyboardInput) EditText editTextKeyboardInput;
    @Bind(R.id.textView_Information) TextView textViewInfo;

    @Inject
    MainController mMainController;

    private GestureDetectorCompat  mGestureDetector;
    private Drawer mDrawer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMainController.initialize(this, toolbar);
        mMainController.initializeConnection();
    }

//    @OnFocusChange(R.id.editText_KeyboardInput)
//    public void onFocusChanged(boolean hasFocus)
//    {
//        if(hasFocus)
//        {
//            isKeyboardOpened = true;
//            mSocketClient.writeByte(PacketType.START_KEYBOARD);
//        }
//        else
//        {
//            Util.hideSoftKeyboard(this, editTextKeyboardInput);
//            isKeyboardOpened = false;
//
//            mSocketClient.writeByte(PacketType.END_KEYBOARD);
//        }
//    }

    @Override
    public void onBackPressed()
    {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (mDrawer != null && mDrawer.isDrawerOpen())
        {
            mDrawer.closeDrawer();
        }
        //TODO: else if (isKeyboardOpened ??)
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        mMainController.disconnect();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        if (mMainController.onTouchEvent(e)) {
            return true;
        }

        return super.onTouchEvent(e);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }
}
