package org.womengineers.resume;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PreviewResume extends AppCompatActivity {
    private ImageView pdfImage;
    private int currPage = 0;
    private Button next, prev;
    File showPdf;
    Boolean isMinimalist;

    ArrayList<String> contactInfo;
    ArrayList<String> educationInfo;
    ArrayList<String> workInfo;
    ArrayList<String> objectiveStatementInfo;

    Font sectionHeader = new Font(Font.FontFamily.COURIER, 14f, Font.UNDERLINE);
    Font innerText = new Font(Font.FontFamily.COURIER, 12f);

    Font sectionHeader2 = new Font(Font.FontFamily.TIMES_ROMAN, 14f, Font.UNDERLINE);
    Font innerText2 = new Font(Font.FontFamily.TIMES_ROMAN, 12f);

    ResumeInfoDb theDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_resume);

        theDb = new ResumeInfoDb(this);

        //uses a string saved in the intent in order to obtain an ArrayList of the file names to be used
        //from the database
        final String res = this.getIntent().getExtras().getString("resNum5");
        final ArrayList<String> nameOfFiles = theDb.getRow(res);
        isMinimalist = getIntent().getExtras().getBoolean("isMin");

         contactInfo = readAndOrganizeFiles(new File(getFilesDir(), nameOfFiles.get(2)));
         objectiveStatementInfo = readAndOrganizeFiles(new File(getFilesDir(), nameOfFiles.get(3)));
         educationInfo = readAndOrganizeFiles(new File(getFilesDir(), nameOfFiles.get(4)));
         workInfo = readAndOrganizeFiles(new File(getFilesDir(), nameOfFiles.get(5)));

        createPdf(nameOfFiles.get(1));


        next = (Button) findViewById(R.id.next);
        prev = (Button) findViewById(R.id.previous);

        //allows the user to see the next page of their resume if it extends beyond 1 page in length
        next.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                currPage++;
                render();
            }
        });

        //allows the user to see the previous page of their resume if it extends beyond 1 page in length
        prev.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                currPage--;
                render();
            }
        });

        render();

        //when clicked allows the user to email a pdf of their resume to themselves
        Button email = (Button) findViewById(R.id.email);
        email.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                emailPdf();
            }
        });

        //when clicked allows the user to return to the main screen
        Button toHomeScreen = (Button) findViewById(R.id.home);
        toHomeScreen.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(PreviewResume.this, MainScreen.class));
                finish();
            }
        });

    }

    //given the name of the pdf to be created, constructs a Pdf and saves it to internal storage
    public void createPdf(String fileName) {

        Rectangle rect1 = new Rectangle(PageSize.A4);
        rect1.setBackgroundColor(new BaseColor(255,255,255));
        Document doc = new Document(rect1);

        File path = new File(getFilesDir(), "document");
        path.mkdirs();
        showPdf = new File(path, fileName);
/*
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/Dir");
        //if(!dir.exists())
        dir.mkdir(); //saying that the result of this is ignored for some reason

        File file = new File(dir, "newFile.pdf");
*/
        try {

                FileOutputStream fOut = new FileOutputStream(showPdf);

                PdfWriter.getInstance(doc, fOut);


                //open the document
                doc.open();

                //add paragraph to document
                contactStuff(contactInfo, doc, isMinimalist);
                doc.add(Chunk.NEWLINE);
                objStatementStuff(objectiveStatementInfo, doc, isMinimalist);
                doc.add(Chunk.NEWLINE);
                educationStuff(educationInfo, doc, isMinimalist);
                doc.add(Chunk.NEWLINE);
                workStuff(workInfo, doc, isMinimalist);



        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        }
        finally {
            doc.close();
        }
    }

    //renders the pdf and shows a preview of the pdf in an ImageView
    private void render(){
        if(!showPdf.exists())
            Log.i("File exists", "The file was not found");
        else if(showPdf.exists())
            Log.i("File exists", "The file was found as intended");

        try{
            pdfImage = (ImageView) findViewById(R.id.myImage);
            //int width = pdfImage.getWidth();
            //int height = pdfImage.getHeight();
            int width = 500;
            int height = 700;

            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(showPdf, ParcelFileDescriptor.MODE_READ_ONLY));
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

            if(currPage < 0){
                currPage = 0;
            }
            else if(currPage > renderer.getPageCount()){
                currPage = renderer.getPageCount()-1;
            }

            Matrix matrix = pdfImage.getImageMatrix();
            Rect rect = new Rect(0,0,width,height);
            renderer.openPage(currPage).render(bitmap, rect, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            pdfImage.setImageMatrix(matrix);
            pdfImage.setImageBitmap(bitmap);
            pdfImage.invalidate();
            renderer.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //constructs the contact info part of the resume based on if the user is using the "minimalist" or "overachiever" template
    private void contactStuff(ArrayList<String> arr, Document doc, boolean isStandardTemplate)throws DocumentException{
        Paragraph p = new Paragraph("");
        String info = "";

            String name = arr.get(0);
            if(!name.equals(" ")){
                Font f;
                if(isStandardTemplate){
                    f = new Font(Font.FontFamily.COURIER, 16f);
                    p.setFont(f);

                }
                else{
                    f = new Font(Font.FontFamily.TIMES_ROMAN, 16f);

                }
                p.setFont(f);
                p.add(name.trim());

                //info += "\n";
            }

            String address = arr.get(1);
            if(address.equals(" "))
                info += "\n";
            else
                info += address.trim();
                if(isStandardTemplate)
                    info += "\n";

            String email = arr.get(2);
            if(!email.equals(" "))
                if(!isStandardTemplate)
                    info += "|" + email.trim();
                else
                    info += email.trim() + "   ";



            String phoneNum = arr.get(3);
            if(!phoneNum.equals(" "))
                if(!isStandardTemplate)
                    info += "|";
                info += phoneNum.trim();


        //p = new Paragraph(info);
        Paragraph p2 = new Paragraph(info);

        if(isStandardTemplate)
            p2.setFont(innerText);
        else
            p2.setFont(innerText2);

        p2.setAlignment(Paragraph.ALIGN_CENTER);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        doc.add(p);
        doc.add(p2);

    }

    //constructs the objective of the resume based on if the user is using the "minimalist" or "overachiever" template
    private void objStatementStuff(ArrayList<String> arr, Document doc, boolean isStandardTemplate) throws DocumentException{
        Paragraph p = new Paragraph("Objective Statement");
        p.setFont(sectionHeader);
        Paragraph p2;
        String parText = "";

            String objState = arr.get(0);
            if(!objState.equals(" "))
                parText += objState.trim();

            p2 = new Paragraph(parText);

            if(isStandardTemplate)
                p2.setFont(innerText);
            else
                p2.setFont(innerText2);


        p.setAlignment(Paragraph.ALIGN_LEFT);
        p2.setAlignment(Paragraph.ALIGN_LEFT);

        doc.add(p);
        doc.add(p2);

    }

    //constructs the education info part of the resume based on if the user is using the "minimalist" or "overachiever" template
    private void educationStuff(ArrayList<String> arr, Document doc, boolean isStandardTemplate) throws DocumentException {
        Paragraph p;
        Paragraph p2 = new Paragraph("Education");
        p2.setFont(sectionHeader);
        String parText = "";

        if(isStandardTemplate){
            String university = arr.get(0);
            if(!university.equals(" "))
                parText += university.trim();

            String gradYr = arr.get(1);
            if(!gradYr.equals(" "))
                parText += "\t\t           Graduation Year: " +gradYr.trim();

            String loc = arr.get(2);
            if(!loc.equals(" "))
                parText += "\n" +loc.trim();
        }

        else{
            String university = arr.get(0);
            if(!university.equals(" "))
                parText += university.trim();

            String loc = arr.get(2);
            if(!loc.equals(" "))
                parText += ", " +loc.trim();

            String gradYr = arr.get(1);
            if(!gradYr.equals(" "))
                parText += "\t\t           Graduation Year: " +gradYr.trim();

        }


            String degree = arr.get(3);
            if(!degree.equals(" "))
                parText += "\n\tDegree earned:\n     " + degree.trim();

            p = new Paragraph(parText);

            if(isStandardTemplate)
                p.setFont(innerText);
            else
                p.setFont(innerText2);



        p.setAlignment(Paragraph.ALIGN_LEFT);
        p2.setAlignment(Paragraph.ALIGN_LEFT);

        doc.add(p2);
        doc.add(p);

    }

    //constructs the work experience part of the resume based on if the user is using the "minimalist" or "overachiever" template
    private void workStuff(ArrayList<String> arr, Document doc, boolean isStandardTemplate) throws DocumentException{
        Paragraph p;
        Paragraph p2 = new Paragraph("Work Experience");
        Paragraph jobDesc;

        if(isStandardTemplate)
            p2.setFont(sectionHeader);
        else
            p2.setFont(sectionHeader2);

        String jobTitle = "";
        String desc = "";

            String position = arr.get(1);
            if(!position.equals(" "))
                jobTitle += position.trim();

            String company = arr.get(0);
            if(!company.equals(" "))
                jobTitle += " at " + company.trim();

            String years = arr.get(2);
            if(!years.equals(" "))
                jobTitle += "     " + years.trim();

            String responsibilities = arr.get(3);
            if(!responsibilities.equals(" "))
                desc += responsibilities.trim();

            p = new Paragraph(jobTitle);
            if(isStandardTemplate)
                p.setFont(new Font(Font.FontFamily.COURIER, 13F));
            else
                p.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 13F));

            jobDesc = new Paragraph(desc);

            if(isStandardTemplate)
                jobDesc.setFont(innerText);
            else
                jobDesc.setFont(innerText2);

            jobDesc.setFirstLineIndent(17);


        p.setAlignment(Paragraph.ALIGN_LEFT);
        p2.setAlignment(Paragraph.ALIGN_LEFT);
        jobDesc.setAlignment(Paragraph.ALIGN_LEFT);

        doc.add(p2);
        doc.add(p);
        doc.add(jobDesc);

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

    //puts all of the information a user could have saved on a single File into one ArrayList
    public ArrayList<String> putInViews(String strToSplit){
        String[] savedText = strToSplit.split(":");
        ArrayList<String> strForViews = new ArrayList<>();
        for(int i = 0; i < savedText.length; i++) {
            strForViews.add(savedText[i]);
        }
        return strForViews;
    }

    //uses putInViews and readFile to create an ArrayList of saved information from a given file to be written to the PDF
    public ArrayList<String> readAndOrganizeFiles(File file){
        String str = readFile(file);
        return putInViews(str);
    }

    //enables the user to email a pdf copy of their resume to themselves
    //works best when sending the email from a gmail account
    private void emailPdf()
    {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT, "Resume Pdf");
        email.putExtra(Intent.EXTRA_TEXT, "Your completed resume courtesy of ResumEditor!");
        Uri uri = FileProvider.getUriForFile(PreviewResume.this, "com.WomEngineers.ResumEditor.fileprovider", showPdf);
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("message/rfc822");
        startActivity(email);
    }

}
