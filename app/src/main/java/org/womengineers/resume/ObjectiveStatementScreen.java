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

public class ObjectiveStatementScreen extends AppCompatActivity {
    EditText objState;
    File objStateFile;
    ResumeInfoDb theDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objective_statement_screen);

        theDb = new ResumeInfoDb(this);

        //uses a string saved in the intent in order to obtain an ArrayList of the file names to be used
        //from the database
        final String res = this.getIntent().getExtras().getString("resNum2");
        final ArrayList<String> fileNames1 = theDb.getRow(res);
        String fileName = fileNames1.get(3);

        objState = (EditText) findViewById(R.id.editText16);

        //creates a file if one doesn't exist, otherwise opens one given a name and sets the editTextViews
        //to show what the user last entered in those sections
        objStateFile = new File(getFilesDir(), fileName);
        String fileInfo = readFile(objStateFile);
        if(fileInfo != null && !fileInfo.equals("")){
            ArrayList<String> insideTextViews = putInViews(fileInfo);

            String s1 = insideTextViews.get(0);
            if(s1.equals(" "))
                objState.setText("");
            else
                objState.setText(s1);
        }

        //enables the user to exit out of a multiline text section when they click on a part of the screen
        //that is not an editText widget
        changeFocus(objState);

        //when clicked sends the user to the contact info screen
        Button back = (Button) findViewById(R.id.button20);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                writeFile(objStateFile, whatToSave());
                Intent toContactInfo = new Intent(ObjectiveStatementScreen.this, ContactInfo.class);
                toContactInfo.putExtra("resNum", res);
                startActivity(toContactInfo);
            }
        });

        //when clicked sends the user to the education screen
        Button next = (Button) findViewById(R.id.button19);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                writeFile(objStateFile, whatToSave());
                Intent toEducation = new Intent(ObjectiveStatementScreen.this, EducationScreen.class);
                toEducation.putExtra("resNum3", res);
                startActivity(toEducation);
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
        String[] thingsToSave = {objState.getText().toString()};
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
