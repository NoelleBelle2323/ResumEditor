/*
    Establishes a screen for editing and saving a single resume
 */

package com.example.noelle.mvp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;


public class ResumeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_screen);


        //Creates a save button to create a single file to store on user's device
        Button saveButton = (Button)findViewById(R.id.button2);

        saveButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String filename = "myResume";
                File file = new File(getFilesDir(), filename);
            }
        });

    }


}
