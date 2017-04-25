package org.womengineers.resume;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Noelle on 4/24/2017.
 */

public class ResumeInfoDb extends SQLiteOpenHelper{

        private static final int DATABASE_VERSION = 2;
        private static final String DATABASE_NAME = "ResumeHandler.db";
        private static final String TABLE_NAME = "ResumeHandler_table";
        private static final String ID = "ID";
        private static final String PDF_NAME = "PDF_Name";
        private static final String FILE_NAME_1 = "File_Name_1";
        private static final String FILE_NAME_2 = "File_Name_2";
        private static final String FILE_NAME_3 = "File_Name_3";
        private static final String FILE_NAME_4 = "File_Name_4";


        private static final String DATABASE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PDF_NAME + " TEXT, " +
                        FILE_NAME_1 + " TEXT, " +
                        FILE_NAME_2 + " TEXT, " +
                        FILE_NAME_3 + " TEXT, " +
                        FILE_NAME_4 + " TEXT);";

        ResumeInfoDb(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        //adds rows to the table
        public boolean insertData(String id, String pdf, String file1, String file2, String file3, String file4){
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(ID, id);
            contentValues.put(PDF_NAME, pdf);
            contentValues.put(FILE_NAME_1, file1);
            contentValues.put(FILE_NAME_2, file2);
            contentValues.put(FILE_NAME_3, file3);
            contentValues.put(FILE_NAME_4, file4);

            long result = db.insert(TABLE_NAME, null, contentValues);

            db.close();

            return(result == -1);
        }

        //returns an ArrayList of a single row of the table given the table's ID
        public ArrayList<String> getRow(String id){
            SQLiteDatabase db = this.getWritableDatabase();
            ArrayList<String> theList = new ArrayList<>();

            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID = " + id + " Limit 1", null);
            if(cursor.moveToFirst()){
                String[] columnNames = cursor.getColumnNames();
                for(int i = 0; i < columnNames.length; i++){
                    theList.add(cursor.getString(cursor.getColumnIndex(columnNames[i])));

                }
            }
            cursor.close();
            db.close();

            return theList;
        }

}
