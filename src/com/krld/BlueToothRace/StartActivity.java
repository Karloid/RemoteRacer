package com.krld.BlueToothRace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by 3 on 02.02.14.
 */
public class StartActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        Button startServer = (Button) findViewById(R.id.startServerButton);
        startServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(StartActivity.this, ServerActivity.class);
                myIntent.putExtra("key", "value");
                StartActivity.this.startActivity(myIntent);
            }
        });

        Button startClient = (Button) findViewById(R.id.startClientButton);
        startClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(StartActivity.this, ClientActivity.class);
                myIntent.putExtra("key", "value");
                StartActivity.this.startActivity(myIntent);
            }
        });
    }
}
