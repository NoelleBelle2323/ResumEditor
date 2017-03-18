/*
    Creates opening screen w/ list of options
        curr only one option to create resume
 */
package com.example.noelle.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

import static com.example.noelle.mvp.R.styleable.View;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Button createResumeButton = (Button) findViewById(R.id.button);

        createResumeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                startActivity(new Intent(MainScreen.this, ResumeScreen.class));
            }
        });
    }


}
