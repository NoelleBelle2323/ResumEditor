package org.womengineers.resume;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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

public class BoostScreen extends AppCompatActivity {
    Map<String, String> compSciGroups = new TreeMap<>();
    Map<String, String> engGroups = new TreeMap<>();
    Map<String, String> stemGroups = new TreeMap<>();
    String compGroupsStr;
    String engGroupsStr;
    String stemGroupsStr;
    TextView showOptions;
    WebView webView;
    GestureDetector gestureDetector;
    private ScaleGestureDetector SGD;
    private float myScale = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boost_screen);

        webView = (WebView) findViewById(R.id.webView2);

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
                ScrollView layout =(ScrollView) findViewById(R.id.scrollView8);
                layout.startAnimation(scaleAnimation);
                return true;
            }
        });

        makeMaps();
        makeStringsToView();
        showOptions = (TextView) findViewById(R.id.textView13);

        //creates a drop-down menu for the user to sort resume boosting activities by industry
        Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.networking_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //when the user selects an option, a textView appears with a list of opportunities geared towards boosting a person's resume
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                //indicates movement should be recognized
                showOptions.setMovementMethod(LinkMovementMethod.getInstance());

                //get item thru parent.getItemAtPosition(pos)
                switch(pos)
                {
                    case 0:
                        showOptions.setVisibility(View.INVISIBLE);
                        showOptions.setText("");
                        break;
                    case 1:
                        showOptions.setText(makeStringsSpannableForLinks(compGroupsStr, compSciGroups));
                        showOptions.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        showOptions.setText(makeStringsSpannableForLinks(engGroupsStr, engGroups));
                        showOptions.setVisibility(View.VISIBLE);
                        break;
                    case 3:
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

        //sends the user back to the MainScreen when clicked
        Button backToMain = (Button) findViewById(R.id.button7);
        backToMain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                startActivity(new Intent(BoostScreen.this, MainScreen.class));
                finish();
            }
        });
    }

    //creates maps with the key being the name of the opportunity and the value being a description of what is offered
    public void makeMaps()
    {
        compSciGroups.put("Girls Who Code Internship", "Year-round, semester, and summer programs for college-aged students.\n\n" +
                "Website: https://girlswhocode.com/gwc-is-looking-for-awesome-interns/");
        compSciGroups.put("Michigan Tech Summer Youth Programs", "Summer program for high schoolers entering grades 9-11 who are " +
                "interested in computer science.\n\n" +
                "Website: http://www.syp.mtu.edu/courses-scholarship.php#Women_in_Computer_Science");
        compSciGroups.put("University of Washington ALVA Big Data Program", "Summer program for rising high school seniors. This includes " +
                "research experiences and an introduction to programming.\n\n" +
                "Website: http://depts.washington.edu/genomics/hsprog/eScience%20HS%20ALVA.shtml");

        engGroups.put("Society of Women Engineers", "Under their careers section, search for internships and job opportunities based on " +
                "your state. Most intern opportunities are 3-6 months long.\n\n" +
                "Website: http://careers.swe.org/");
        engGroups.put("Michigan Tech Summer Youth Programs", "Several summer opportunities for high school graduates or younger are offered" +
                "at Michigan Tech. These opportunities span from women in engineering to women in robotics.\n\n" +
                "Website: http://www.syp.mtu.edu/courses-scholarship.php#Engineering_Scholars_Program");

        stemGroups.put("National Science Foundation", "Their website has a search engine with various research opportunities based on" +
                "areas of study and your location.\n\n" +
                "Website: https://www.nsf.gov/crssprgm/reu/");
        stemGroups.put("Naval Research Enterprise Program", "10-week summer research program with the Navy for science and engineering " +
                "undergraduates and graduates. Applications open September 2017.\n\n" +
                "Website: http://nreip.asee.org/");
        stemGroups.put("Science and Engineering Apprenticeship Program", "Summer research opportunity for high school students at the Department of the Navy.\n\n" +
                "Website: http://seap.asee.org/");
        stemGroups.put("APS/IBM Research Internship for Undergraduate Women and Underrepresented Minorities", "The American Physical Society and IBM co-sponsor " +
                "two undergraduate research internship programs which strive to encourage women and underrepresented minorities to pursue" +
                "graduate studies in science and engineering.\n\n" +
                "Website: https://www.aps.org/programs/women/scholarships/ibm/");
        stemGroups.put("University of Washington ALVA GenOM Project", "9-week summer research experience for rising freshmen at the " +
                "University of Washington. Open to underrepresented minorities in the sciences.\n\n" +
                "Website: http://depts.washington.edu/genomics/hsprog/alva.shtml");
    }

    //converts key/value pairs of each map into a suitable string to be later applied to a textView
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
        final SpannableString spanStr = new SpannableString(str.substring(str.indexOf("null")+4));
        int index = 0;
        final String find = spanStr.toString();

        for(String s: map.keySet()){
            final int starter = find.indexOf("Website:", index) +8;
            final int stopper = find.indexOf("\n", starter);
            spanStr.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    Log.i("TAG", "Span Clicked");
                    //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(find.substring(starter, stopper)));
                    //startActivity(browserIntent);
                    webView.setVisibility(View.VISIBLE);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.getSettings().setDomStorageEnabled(true);
                    webView.loadUrl(find.substring(starter, stopper));
                }

                /*
                can change the underline and color appearance of clickable sections
                public void updateDrawState(TextPaint ds){
                    ds.setColor(94e497);
                }
                */
            }, starter,stopper, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            index += stopper;
        }

        return spanStr;
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
