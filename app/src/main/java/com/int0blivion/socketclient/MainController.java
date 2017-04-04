package com.int0blivion.socketclient;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.int0blivion.socketclient.connection.ConnectionCallback;
import com.int0blivion.socketclient.connection.SocketController;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.net.InetSocketAddress;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Matt on 4/2/2017.
 */
public class MainController {
    private static final int SLEEP = 0x0;
    private static final int SHUTDOWN = 0x1;
    private static final int RESTART = 0x2;

    @Bind(R.id.editText_KeyboardInput) EditText editTextKeyboardInput;
    @Bind(R.id.textView_Information) TextView textViewInfo;

    private SocketController mSocketController;
    private ConnectionCallback mConnectionCallback;

    private Drawer mDrawer;
    private GestureDetectorCompat mGestureDetector;
    private boolean wasRightClick;
    private boolean drawerOpening;
    private float mRightClickX = -1;
    private float mRightClickY = -1;
    private boolean isKeyboardOpened;

    public MainController() {
        mConnectionCallback = new SocketCallback();
        mSocketController = new SocketController(mConnectionCallback);
    }

    public void initialize(@NonNull AppCompatActivity activity, @NonNull Toolbar toolbar) {
        Preconditions.checkNotNull(activity, "activity");
        Preconditions.checkNotNull(toolbar, "toolbar");

        ButterKnife.bind(this, activity);
        buildDrawer(activity, toolbar);

        final SwipeGestureDetector detector = new SwipeGestureDetector();

        mGestureDetector = new GestureDetectorCompat(activity, detector);
        mGestureDetector.setOnDoubleTapListener(detector);
        editTextKeyboardInput.addTextChangedListener(new EditTextWatcher());
    }

    public void connect(@NonNull InetSocketAddress address) {
        Preconditions.checkNotNull(address, "address");

        disconnect();
        mSocketController.connect(address);
    }

    public void reconnect() {
        mSocketController.reconnect();
    }

    public void disconnect() {
        if(mSocketController != null) {
            mSocketController.disconnect();
        }
    }

    public boolean onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
            return true;
        }

        return false;
    }

    public boolean onTouchEvent(@NonNull MotionEvent e) {
        if (drawerOpening || isKeyboardOpened || Util.hitTest(editTextKeyboardInput, e)) {
            return false;
        }

        if (wasRightClick && e.getActionMasked() == MotionEvent.ACTION_MOVE) {
            if (mRightClickX != e.getX() || mRightClickY != e.getY()) {
                wasRightClick = false;
                mRightClickX = -1;
                mRightClickY = -1;
            }
        } else if (e.getActionIndex() > 0 && e.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            mSocketController.writeByte(PacketType.RIGHT_CLICK);

            mRightClickY = e.getY(0);
            mRightClickX = e.getX(0);

            wasRightClick = true;
        } else {
            this.mGestureDetector.onTouchEvent(e);
        }

        return false;
    }

    /**
     * Builds the Navigation drawer and adds all necessary drawer items to it
     * @param activity
     * @param toolbar
     */
    private void buildDrawer(@NonNull Activity activity, @NonNull Toolbar toolbar) {
        DrawerBuilder drawerBuilder = new DrawerBuilder();
        drawerBuilder.withActivity(activity);
        drawerBuilder.withToolbar(toolbar);
        drawerBuilder.addDrawerItems(
                new PrimaryDrawerItem().withName("Sleep").withDescription("Put Computer to Sleep"),
                new PrimaryDrawerItem().withName("Shutdown").withDescription("Shutdown Computer"),
                new PrimaryDrawerItem().withName("Restart").withDescription("Restart Computer")
        );
        drawerBuilder.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                switch (position) {
                    case SLEEP:
                        mSocketController.writeByte(PacketType.SLEEP);
                        break;

                    case SHUTDOWN:
                        mSocketController.writeByte(PacketType.SHUTDOWN);
                        break;

                    case RESTART:
                        mSocketController.writeByte(PacketType.RESTART);
                        break;
                }

                return false;
            }
        });
        drawerBuilder.withOnDrawerListener(new Drawer.OnDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                drawerOpening = true;
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                drawerOpening = false;
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                drawerOpening = true;
            }
        });
        drawerBuilder.withRootView(R.id.drawer_layout);
        drawerBuilder.withSelectedItem(-1);

        mDrawer = drawerBuilder.build();
    }

    public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mSocketController.writeBytes(PacketType.SCROLL, -1 * distanceX, -1 * distanceY);

            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(e.getPointerCount() > 1) {
                mSocketController.writeByte(PacketType.RIGHT_CLICK);
            }
            else {
                mSocketController.writeByte(PacketType.SINGLE_CLICK);
            }

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mSocketController.writeByte(PacketType.DOUBLE_CLICK);

            return true;
        }
    }

    public class EditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged (CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged (CharSequence s,int start, int before, int count) {

        }

        @Override
        public void afterTextChanged (Editable s) {
            mSocketController.writeString(s.toString());
        }
    }

    public class SocketCallback implements ConnectionCallback {

        @Override
        public void onConnected() {

        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onStatusUpdate(@Nullable String status) {

        }
    }
}
