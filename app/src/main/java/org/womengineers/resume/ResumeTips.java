package org.womengineers.resume;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import static android.R.attr.value;

public class ResumeTips extends AppCompatActivity {
    private ScaleGestureDetector SGD;
    private float myScale = 1f;
    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_tips);

        //allows the user to zoom in on text
        gestureDetector = new GestureDetector(this, new GestureListener());
        SGD = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener(){
            public boolean onScale(ScaleGestureDetector detector)
            {
                float scale = 1 - detector.getScaleFactor();
                float prevScale = myScale;
                myScale += scale;

                if(myScale < 0.1f) //min condition
                    myScale = 0.1f;
                if(myScale > 1f) //max condition
                    myScale = 1f;

                ScaleAnimation scaleAnimation = new ScaleAnimation(1f / prevScale, 1f / myScale, 1f / prevScale, 1f / myScale, detector.getFocusX(), detector.getFocusY());
                scaleAnimation.setDuration(0);
                scaleAnimation.setFillAfter(true);
                ScrollView layout =(ScrollView) findViewById(R.id.scrollView);
                layout.startAnimation(scaleAnimation);
                return true;
            }
        });

        TextView layout = (TextView) findViewById(R.id.textView16);
        layout.setText(R.string.layout);

        TextView objStatement = (TextView) findViewById(R.id.textView18);
        objStatement.setText(R.string.obj_statement_underline);

        TextView grammar = (TextView) findViewById(R.id.textView20);
        grammar.setText(R.string.grammar);

        TextView content = (TextView) findViewById(R.id.textView22);
        content.setText(R.string.content);

        //when clicked sends the user back to the main screen
        Button backToMain = (Button) findViewById(R.id.button8);
        backToMain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                startActivity(new Intent(ResumeTips.this, MainScreen.class));
                finish();
            }
        });
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        SGD.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // double tap fired.
            return true;
        }
    }
}
