package com.example.blake.nounsonaphone;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blake.nounsonaphone.data.WordContract.WordEntry;
import com.example.blake.nounsonaphone.data.WordDbHelper;

public class InputActivity extends AppCompatActivity {

    private EditText mWordEditText;

    private int numWords = 0;

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_activity);

        //Clears database on open, to be removed mabye
        clearDatabase();

        //resets scores
        resetScores();

        //Find relevant views
        mWordEditText = (EditText) findViewById(R.id.textInput);
        final TextView wordCount = (TextView) findViewById(R.id.savedWordCount);

        //Set up save button
        Button save = (Button) findViewById(R.id.saveWord);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Adds word to database
                insertWord();
                //Clears the edittext
                mWordEditText.setText("");
                wordCount.setText(Integer.toString(numWords) + " words saved");
            }
        });
        //Set up done button
        Button startGame = (Button) findViewById(R.id.beginGame);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Moves to the round activity
                Intent intent = new Intent(InputActivity.this, RoundActivity.class);
                startActivity(intent);
            }
        });


    }

    private void insertWord(){
        //instatiate SQLiteOpenHelper and pass context
        WordDbHelper mDbHelper = new WordDbHelper(this);

        //Open database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Pull text from edit text
        String wordString = mWordEditText.getText().toString().trim();

        //Check if word is blank and if so do not add it
        if(wordString.length()==0){
            Toast.makeText(this, "Cannot save a blank word", Toast.LENGTH_SHORT).show();
        } else {
            //Create Content Values
            ContentValues values = new ContentValues();
            values.put(WordEntry.COLUMN_WORD, wordString);

            //Add text to database
            long newRowId = db.insert(WordEntry.TABLE_NAME, null, values);
            numWords = (int) newRowId;

            //Toast on whether addition was successful
            if (newRowId == -1) {
                Toast.makeText(this, "Error saving word", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "Word saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void resetScores(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply(); // commit changes
    }

    public void clearDatabase(){
        //Clears database on open, to be removed mabye
        WordDbHelper mDbHelper = new WordDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDbHelper.onUpgrade(db, 1, 1);
    }
}
