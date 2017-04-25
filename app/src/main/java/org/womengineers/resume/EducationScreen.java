package org.womengineers.resume;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class EducationScreen extends AppCompatActivity {

    EditText university;
    EditText gradYear;
    EditText schoolLoc;
    EditText degree;

    File educationFile;

    ResumeInfoDb theDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_screen);

        theDb = new ResumeInfoDb(this);

        //uses a string saved in the intent in order to obtain an ArrayList of the file names to be used
        //from the database
        final String res = this.getIntent().getExtras().getString("resNum3");
        final ArrayList<String> fileNames1 = theDb.getRow(res);
        String fileName = fileNames1.get(4);

        university = (EditText) findViewById(R.id.editText10);
        gradYear = (EditText) findViewById(R.id.editText11);
        schoolLoc = (EditText) findViewById(R.id.editText12);
        degree = (EditText) findViewById(R.id.editText13);

        //creates a file if one doesn't exist, otherwise opens one given a name and sets the editTextViews
        //to show what the user last entered in those sections
        educationFile = new File(getFilesDir(), fileName);
        String fileInfo = readFile(educationFile);
        if(fileInfo != null && !fileInfo.equals("")){
            ArrayList<String> insideTextViews = putInViews(fileInfo);

            String s1 = insideTextViews.get(0);
            if(s1.equals(" "))
                university.setText("");
            else
                university.setText(s1);

            String s2 = insideTextViews.get(1);
            if(s2.equals(" "))
                gradYear.setText("");
            else
                gradYear.setText(s2);

            String s3 = insideTextViews.get(2);
            if(s3.equals(" "))
                schoolLoc.setText("");
            else
                schoolLoc.setText(s3);

            String s4 = insideTextViews.get(3);
            if(s4.equals(" "))
                degree.setText("");
            else
                degree.setText(s4);
        }

        //enables the user to exit out of a multiline text section when they click on a part of the screen
        //that is not an editText widget
        changeFocus(university);
        changeFocus(gradYear);
        changeFocus(schoolLoc);
        changeFocus(degree);

        //when clicked sends the user to the objective statement screen
        Button back = (Button) findViewById(R.id.button15);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                writeFile(educationFile, whatToSave());
                Intent toObjStatement = new Intent(EducationScreen.this, ObjectiveStatementScreen.class);
                toObjStatement.putExtra("resNum2", res);
                startActivity(toObjStatement);
            }
        });

        //when clicked sends the user to the work screen
        Button toWorkScreen = (Button) findViewById(R.id.button16);
        toWorkScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                writeFile(educationFile, whatToSave());
                Intent toWork = new Intent(EducationScreen.this, WorkScreen.class);
                toWork.putExtra("resNum4", res);
                startActivity(toWork);
            }
        });
    }

    //writes a file to internal storage
    public void writeFile(File file, String info){
        String filename = file.getName();
        FileOutputStream outputStream;

        try{
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(info.getBytes());
            outputStream.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //reads a file from internal storage
    public String readFile(File file){
        StringBuilder text = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while((line = br.readLine()) != null){
                text.append(line);
                text.append(" :");
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return text.toString();
    }

    //puts all of the information a user could have saved into one ArrayList
    public ArrayList<String> putInViews(String strToSplit){
        String[] savedText = strToSplit.split(":");
        ArrayList<String> strForViews = new ArrayList<>();
        for(int i = 0; i < savedText.length; i++) {
            strForViews.add(savedText[i]);
        }
        return strForViews;
    }

    //returns a String of information to save based on what a user entered in the editTextViews
    public String whatToSave(){
        String savedData = "";
        String[] thingsToSave = {university.getText().toString(), gradYear.getText().toString(),
                schoolLoc.getText().toString(), degree.getText().toString()};
        for(String s: thingsToSave){
            if(s == null)
            {
                savedData += " \n";
            }
            else
            {
                savedData += s + "\n";
            }
        }
        return savedData;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void changeFocus(View v){
        v.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }
}
