package com.example.blake.nounsonaphone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blake.nounsonaphone.data.WordContract;
import com.example.blake.nounsonaphone.data.WordDbHelper;
import com.example.blake.nounsonaphone.InputActivity;


import java.util.ArrayList;

/**
 * Created by Blake on 6/14/2017.
 */

public class RoundActivity extends AppCompatActivity {

    private ArrayList<String> wordList = new ArrayList<String>();

    /** Database helper that will provide us access to the database */
    private WordDbHelper mDbHelper;

    private int roundNumber = 1;
    private long timeRemaining = 0;

    //Score Keeping Helpers
    private boolean team1playing;
    private int team1Score;
    private int team2Score;

    public static final String MY_PREFS_NAME = "MyPrefsFile";


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity);
        //Get round number
        roundNumber = getIntent().getIntExtra("currentRound", roundNumber);

        //Get what team is playing - default is team one
        team1playing = getIntent().getBooleanExtra("team1playing", true);

        //Get and update scores
        getScores();

        //Display current scores
        updateScoreText();

        //Update round description
        TextView description = (TextView) findViewById(R.id.roundDesc);
        switch (roundNumber){
            case 1:
                description.setText(R.string.round1desc);
                break;
            case 2:
                description.setText(R.string.round2desc);
                break;
            case 3:
                description.setText(R.string.round3desc);
                break;
            default:
                description.setText("");
                break;
        }

        //If 3 rounds have been played display end game
        if (roundNumber >= 4 ){
            //End of game
            TextView roundHeader = (TextView) findViewById(R.id.roundTextView);
            roundHeader.setText(R.string.game_over);

            //Hide time remaining
            TextView time = (TextView) findViewById(R.id.timeLeftInRound);
            time.setVisibility(View.GONE);

            //Option to continue playing
            Button restart = (Button) findViewById(R.id.startRound);
            restart.setText(R.string.play_again);
            restart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Restart game from begining
                    Intent intent = new Intent(RoundActivity.this, InputActivity.class);
                    startActivity(intent);
                }
            });
        } else {

            //Display current round if less than 4
            String round = "Round " + roundNumber;
            TextView roundHeader = (TextView) findViewById(R.id.roundTextView);
            roundHeader.setText(round);

            //Get time remaining if new round started
            getTimeRemaining();

            //Populate arraylist from database
            createList();

            //Set up Roundstart button
            Button done = (Button) findViewById(R.id.startRound);
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Send to game activity
                    Intent intent = new Intent(RoundActivity.this, GameActivity.class);
                    intent.putStringArrayListExtra("Word_list", wordList);
                    intent.putExtra("timeRemaining", timeRemaining);
                    intent.putExtra("roundNumber", roundNumber);
                    intent.putExtra("team1playing", team1playing);
                    startActivity(intent);
                }
            });
        }
    }



    private void createList(){
        /** Database helper that will provide us access to the database **/
        mDbHelper = new WordDbHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                WordContract.WordEntry._ID,
                WordContract.WordEntry.COLUMN_WORD};

        Cursor cursor = db.query(
                WordContract.WordEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        //Retrieve indicies of each column
        int idColumnIndex = cursor.getColumnIndex(WordContract.WordEntry._ID);
        int wordColumnIndex = cursor.getColumnIndex(WordContract.WordEntry.COLUMN_WORD);

        //iterate through all the returned rows in the cursor
        while(cursor.moveToNext()){
            wordList.add(cursor.getString(wordColumnIndex));
        }
        cursor.close();
    }
    public void getScores(){
        SharedPreferences sharedPref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int defaultValue = 0;
        team1Score = sharedPref.getInt(getString(R.string.team1score), defaultValue);
        team2Score = sharedPref.getInt(getString(R.string.team2score), defaultValue);
    }

    public void updateScoreText(){
        TextView t1score = (TextView) findViewById(R.id.team1Score);
        TextView t2score = (TextView) findViewById(R.id.team2Score);
        t1score.append(Integer.toString(team1Score));
        t2score.append(Integer.toString(team2Score));
    }

    public void getTimeRemaining(){
        timeRemaining = getIntent().getLongExtra("timeRemaining", timeRemaining);
        TextView time = (TextView) findViewById(R.id.timeLeftInRound);
        if(timeRemaining / 1000 == 0){
            time.append("60 seconds");
        } else {
            time.setText("Seconds remaining: " + timeRemaining / 1000 );
        }
    }
}
