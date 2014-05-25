package com.krld.BlueToothRace.gdx;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.krld.BlueToothRace.R;
import com.krld.BlueToothRace.activitys.FindServerActivity;
import com.krld.BlueToothRace.activitys.StartActivity;

import java.net.Socket;

/**
 * Created by Andrey on 5/25/2014.
 */
public class GdxClientLauncher extends AndroidApplication {
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);
        Socket socket = null;
        if (getIntent().getStringExtra("from").equals("FindServerActivity")) {
            socket = FindServerActivity.connectedSocket;
        } else if (getIntent().getStringExtra("from").equals("StartActivity")) {
            socket = StartActivity.connectedSocket;
        }
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        GdxGameView gameView = GdxGameManager.getNewGameView();
        gameView.setSocket(socket);
        gameView.setActivity(this);
        initialize(gameView, config);
    }
}
