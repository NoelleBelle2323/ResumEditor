package org.womengineers.resume;


import android.content.Intent;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Map;
import java.util.TreeMap;


public class NetWorkingScreen extends AppCompatActivity {

    Map<String, String> compSciGroups = new TreeMap<>();
    Map<String, String> engGroups = new TreeMap<>();
    Map<String, String> stemGroups = new TreeMap<>();
    String compGroupsStr;
    String engGroupsStr;
    String stemGroupsStr;
    TextView showOptions;
    private ScaleGestureDetector SGD;
    private float myScale = 1f;
    GestureDetector gestureDetector;
    WebView webview;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_working_screen);

        webview = (WebView) findViewById(R.id.webView);

        //allows the user to zoom in on the text
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
                ScrollView layout =(ScrollView) findViewById(R.id.scrollView2);
                layout.startAnimation(scaleAnimation);
                return true;
            }
        });

        setMapValues();
        makeStringsToView();

        //creates the textView which will only be visible when a user selects an industry they want to see more info about
        showOptions = (TextView) findViewById(R.id.textView6);
        showOptions.setVisibility(View.INVISIBLE);


        //sets up a drop-down menu of industry options to sort networking groups
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.networking_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //when the user selects an option, a textView appears with a list of opportunities geared towards boosting a person's resume
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                //takes the user's choice in order to display the proper string in the textView
                switch(pos)
                {
                    case 0:
                        showOptions.setVisibility(View.INVISIBLE);
                        showOptions.setText("");
                        break;
                    case 1:
                        //showOptions.setText(compGroupsStr.substring(compGroupsStr.indexOf("null")+4));
                        showOptions.setText(makeStringsSpannableForLinks(compGroupsStr, compSciGroups));
                        //showOptions.setMovementMethod(LinkMovementMethod.getInstance());
                        showOptions.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        //showOptions.setText(engGroupsStr.substring(engGroupsStr.indexOf("null")+4));//do these create xml Strings?
                        showOptions.setText(makeStringsSpannableForLinks(engGroupsStr, engGroups));
                        showOptions.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        //showOptions.setText(stemGroupsStr.substring(stemGroupsStr.indexOf("null")+4));
                        showOptions.setText(makeStringsSpannableForLinks(stemGroupsStr, stemGroups));
                        showOptions.setVisibility(View.VISIBLE);
                        break;
                }

            }

            public void onNothingSelected(AdapterView<?> parent)
            {
                //necessary interface callback
                showOptions.setText("");
            }
        });

        //creates button to return to the MainScreen
        Button toHome = (Button) findViewById(R.id.button3);
        toHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(NetWorkingScreen.this, MainScreen.class));
                finish();
            }});
    }

    //creates maps with organization names as the key and organization description as the value for corresponding industries
    public void setMapValues()
    {
        compSciGroups.put("Anita Borg Institute", "Provides women with tools to develop professional skills in order to attain and retain jobs.\n\n" +
                "Website: https://anitaborg.org/");
        compSciGroups.put("Association for Computing Machinery","Contains a section geared towards women with scholarships and other opportunities to encourage more women to join computer science.\n\n" +
                "Website: http://women.acm.org/");
        compSciGroups.put("Computing Research Association","Aid in encouraging women to pursue research in computer science.\n\n" +
                "Website: http://cra.org/cra-w");
        compSciGroups.put("Women in Technology","Aimed towards advancing women in technology through career development tools and mentoring.\n\n" +
                "Website: http://www.womenintechnology.org/home");
        compSciGroups.put("National Center for Women & Information Technology", "Meant to establish a community of women in computing both during and after their college experience.\n\n" +
                "Website: https://www.ncwit.org/");

        engGroups.put("IEEE Women in Engineering","International professional organization focused on the recruitment and retention of women in technical fields.\n\n" +
                "Website: http://wie.ieee.org/");
        engGroups.put("Society of Women Engineers", "Provide support for women pursuing engineering fields.\n\n" +
                "Website: http://societyofwomenengineers.swe.org/");

        stemGroups.put("MentorNet","An online mentoring service for women in STEM.\n\n" +
                "Website: http://www.mentornet.net/");
        stemGroups.put("Association for Women in Science", "Organization that supports career development and general advocacy for women in STEM.\n\n" +
                "Website: http://www.awis.org/");
        stemGroups.put("Scientista", "Community of women pursuing STEM degrees.\n\n" +
                "Website: http://www.scientistafoundation.com/");
        stemGroups.put("The National Academies Of Sciences, Engineering and Medicine", "Provide resources for career development, and encouraging women to pursue STEM careers.\n\n" +
                "Website: http://sites.nationalacademies.org/PGA/cwsem/index.htm");

    }

    //takes each key/value pair from the corresponding map and converts it into a string to later be displayed
    public void makeStringsToView()
    {
        for(String s: compSciGroups.keySet())
        {
                compGroupsStr += s + "\n";
                compGroupsStr += "\t" + compSciGroups.get(s) +"\n\n\n";
        }

        for(String s: engGroups.keySet())
        {
                engGroupsStr += s + "\n";
                engGroupsStr += "\t" + engGroups.get(s) +"\n\n\n";
        }

        for(String s: stemGroups.keySet())
        {
                stemGroupsStr += s + "\n";
                stemGroupsStr += "\t" + stemGroups.get(s) +"\n\n\n";
        }
    }

    //returns a spannable string of the text that appears to the user in order to create links to
    //the websites of provided organizations
    public SpannableString makeStringsSpannableForLinks(String str, Map<String, String> map){
        final SpannableString ss = new SpannableString(str.substring(str.indexOf("null")+4));
        int index = 0;
        final String finder = ss.toString();

        for(String s: map.keySet()){
            final int start = finder.indexOf("Website:", index) +8;
            final int stop = finder.indexOf("\n", start);
            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Log.i("TAG", "Span Clicked");
                    webview.setVisibility(View.VISIBLE);
                    webview.getSettings().setJavaScriptEnabled(true);
                    webview.getSettings().setDomStorageEnabled(true);
                    webview.loadUrl(finder.substring(start, stop));
                }
            }, start,stop, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            index += stop;
        }

        return ss;
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
