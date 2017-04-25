package org.womengineers.resume;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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

public class WorkScreen extends AppCompatActivity {

    EditText company;
    EditText position;
    EditText years;
    EditText responsibilities;

    File workExperienceFile;

    Boolean isMinimalist;

    ResumeInfoDb theDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_screen);

        theDb = new ResumeInfoDb(this);

        //uses a string saved in the intent in order to obtain an ArrayList of the file names to be used
        //from the database
        final String res = this.getIntent().getExtras().getString("resNum4");
        final ArrayList<String> fileNames1 = theDb.getRow(res);
        String fileName = fileNames1.get(5);

        company = (EditText) findViewById(R.id.editText14);
        position = (EditText) findViewById(R.id.editText15);
        years = (EditText) findViewById(R.id.editText17);
        responsibilities = (EditText) findViewById(R.id.editText18);

        //creates a file if one doesn't exist, otherwise opens one given a name and sets the editTextViews
        //to show what the user last entered in those sections
        workExperienceFile = new File(getFilesDir(), fileName);
        String fileInfo = readFile(workExperienceFile);
        if(fileInfo != null && !fileInfo.equals("")){ //need to change && to ||
            ArrayList<String> insideTextViews = putInViews(fileInfo);

            String s1 = insideTextViews.get(0);
            if(s1.equals(" "))
                company.setText("");
            else
                company.setText(s1);

            String s2 = insideTextViews.get(1);
            if(s2.equals(" "))
                position.setText("");
            else
                position.setText(s2);

            String s3 = insideTextViews.get(2);
            if(s3.equals(" "))
                years.setText("");
            else
                years.setText(s3);

            String s4 = insideTextViews.get(3);
            if(s4.equals(" "))
                responsibilities.setText("");
            else
                responsibilities.setText(s4);
        }

        //enables the user to exit out of a multiline text section when they click on a part of the screen
        //that is not an editText widget
        changeFocus(company);
        changeFocus(position);
        changeFocus(years);
        changeFocus(responsibilities);

        ChooseTemplateAlert();

        //when clicked sends the user to the education screen
        Button back = (Button) findViewById(R.id.button17);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                writeFile(workExperienceFile, whatToSave());
                Intent toEducation = new Intent(WorkScreen.this, EducationScreen.class);
                toEducation.putExtra("resNum3", res);
                startActivity(toEducation);
            }
        });

        //when clicked sends the user to an Activity where they can preview their resume
        Button toResumePreview = (Button) findViewById(R.id.button18);
        toResumePreview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                writeFile(workExperienceFile, whatToSave());
                Intent toResume = new Intent(WorkScreen.this, PreviewResume.class);
                toResume.putExtra("isMin", isMinimalist);
                toResume.putExtra("resNum5", res);
                startActivity(toResume);
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
        String[] thingsToSave = {company.getText().toString(), position.getText().toString(),
                years.getText().toString(), responsibilities.getText().toString()};
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

    //prompts the user to choose between the "minimalist" and "overachiever" templates for displaying their resume
    public void ChooseTemplateAlert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Choose Your Template");
        alert.setPositiveButton("Minimalist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isMinimalist = true;
            }
        });
        alert.setNegativeButton("Overachiever", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isMinimalist = false;
            }
        });

        alert.create().show();
    }
}
