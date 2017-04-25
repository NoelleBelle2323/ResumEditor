/*
    Creates opening screen w/ list of options
        curr only one option to create resume
 */
package org.womengineers.resume;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;


public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        //allows the user to click a button to send them to the resume screen
        Button createResumeButton = (Button) findViewById(R.id.button);
        createResumeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                startActivity(new Intent(MainScreen.this, ManageResumesScreen.class));
            }
        });

        //allows the user to click a button to send them to the networking screen
        Button toNetworkingScreen = (Button) findViewById(R.id.button4);
        toNetworkingScreen.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v){
                startActivity(new Intent(MainScreen.this, NetWorkingScreen.class));
           }
        });

        //allows the user to click a button to send them to the boosting screen
        Button toBoostScreen = (Button) findViewById(R.id.button5);
        toBoostScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                startActivity(new Intent(MainScreen.this, BoostScreen.class));
            }
        });

        //allows the user to click a button to send them to the resume tips screen
        Button toResumeTips = (Button) findViewById(R.id.button6);
        toResumeTips.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                startActivity(new Intent(MainScreen.this, ResumeTips.class));
            }
        });
    }

    //determines when the user is in landscape v. portrait mode in order to change the layout accordingly
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }


}
