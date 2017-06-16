package com.example.blake.nounsonaphone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Blake on 6/15/2017.
 */

public class GameActivity extends AppCompatActivity {

    private long timeRemaining = 0;
    private long timerLength = 5000;
    private CountDownTimer cdTimer;
    private long milliSecondsRemaining = 0;
    private ArrayList<String> wordList;
    private int currentRound =0;

    //Score Keeping Helpers
    private boolean team1playing;
    private int team1Score;
    private int team2Score;

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    //For back button
    private String lastWord;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save word list, scores, timeremaining, and current word
        outState.putStringArrayList("wordList", wordList);
        outState.putInt(getString(R.string.team1score), team1Score);
        outState.putInt(getString(R.string.team2score), team2Score);
        timeRemaining = milliSecondsRemaining;
        outState.putLong("timeRemaining", timeRemaining);
        outState.putBoolean("team1playing", team1playing);
        outState.putInt("currentRound", currentRound);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Retrieve word list from Round activity and time left (for 2nd and 3rd rounds)

        //Checks if saved instance state (eg rotation)
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            wordList = savedInstanceState.getStringArrayList("wordList");
            team1Score = savedInstanceState.getInt(getString(R.string.team1score));
            team2Score = savedInstanceState.getInt(getString(R.string.team2score));
            timeRemaining = savedInstanceState.getLong("timeRemaining");
            team1playing = savedInstanceState.getBoolean("team1playing");
            currentRound = savedInstanceState.getInt("currentRound");

        } else {
            // Probably initialize members with default values for a new instance
            wordList = getIntent().getStringArrayListExtra("Word_list");
            //Get which team is playing
            team1playing = getIntent().getBooleanExtra("team1playing", true);
            timeRemaining = getIntent().getLongExtra("timeRemaining", timeRemaining);
            //Get and increment the round number
            currentRound = getIntent().getIntExtra("roundNumber", 0);
            currentRound++;

            //Get scores
            getScores();

            //Randomizes words
            Collections.shuffle(wordList);
        }



        //Cycle through until all words have been used then send back to Round Activity
        playRound();
    }

    private void playRound(){
        //Begin asnyc timer
        //If time remaining has been saved as not 0 use that otherwise use full timer
        final long t;
        if (timeRemaining == 0){
            t = timerLength;
        } else {
            t = timeRemaining;
        }

        //
        //Start timer
        //
        //Update seconds remaining
        cdTimer = new CountDownTimer(t, 1000) {
            public void onTick(long millisUntilFinished) {
                //update timerLength with the remaining time left
                milliSecondsRemaining = millisUntilFinished;
                TextView secondsLeft = (TextView) findViewById(R.id.timeRemaining);
                secondsLeft.setText("Seconds remaining: " + millisUntilFinished / 1000 );

            }
            public void onFinish() {
                //set timeremaining to 0
                timeRemaining = 0;

                //set layout to pass xml
                setContentView(R.layout.pass_activity);

                //set scores
                updateScoresPass();

                //randomizeds words
                Collections.shuffle(wordList);

                //Switch Teams
                toggleteam();

                //Set button to switch teams
                Button next = (Button) findViewById(R.id.nextPlayerStart);
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playRound();
                    }
                });

                //Toast that time is up can be deleted
                //Toast.makeText(GameActivity.this, "Time is up", Toast.LENGTH_SHORT).show();
            }
        }.start();


        //Show word from database
        setContentView(R.layout.game_activity);
        TextView displayView = (TextView)findViewById(R.id.currentWord);
        RelativeLayout gameView = (RelativeLayout)findViewById(R.id.game);
        //show current scores
        updateScoresGame();
        //Pull up 1st word
        displayView.setText(wordList.get(0));

        //set click of word to move to next word
        gameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastWord = wordList.get(0);
                wordList.remove(0);

                //Hide game instructions
                TextView instructions = (TextView) findViewById(R.id.game_instructions);
                instructions.setVisibility(View.GONE);

                //increment team score
                if (team1playing){
                    team1Score++;
                    //Toast.makeText(GameActivity.this, "team1score is: " + team1Score, Toast.LENGTH_SHORT).show();
                } else {
                    team2Score++;
                    //Toast.makeText(GameActivity.this, "team2score is: " + team2Score, Toast.LENGTH_SHORT).show();
                }

                //set text to show the next word in list has another item
                if (wordList.size()< 1) {
                    //Save time remaining
                    timeRemaining = milliSecondsRemaining;
                    //Cancel current timer.
                    cdTimer.cancel();
                    //Toast.makeText(GameActivity.this, "timeRemaining is: " + timeRemaining, Toast.LENGTH_SHORT).show();

                    saveScores();
                    // send to next round
                    Intent intent = new Intent(GameActivity.this, RoundActivity.class);
                    intent.putExtra("timeRemaining", timeRemaining);
                    intent.putExtra("currentRound", currentRound);
                    intent.putExtra("team1playing", team1playing);

                    startActivity(intent);

                } else {
                    updateScoresGame();
                    TextView displayView = (TextView) findViewById(R.id.currentWord);
                    displayView.setText(wordList.get(0));
                }
            }

        });

    }

    public void toggleteam(){
        this.team1playing = !this.team1playing;
    }

    public void saveScores(){
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.team1score), team1Score);
        editor.putInt(getString(R.string.team2score), team2Score);

        editor.apply();

    }
    public void getScores(){
        SharedPreferences sharedPref = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int defaultValue = 0;
        team1Score = sharedPref.getInt(getString(R.string.team1score), defaultValue);
        team2Score = sharedPref.getInt(getString(R.string.team2score), defaultValue);
    }

    public void updateScoresPass(){
        TextView t1score = (TextView) findViewById(R.id.team1ScorePass);
        TextView t2score = (TextView) findViewById(R.id.team2ScorePass);
        t1score.append(Integer.toString(team1Score));
        t2score.append(Integer.toString(team2Score));
    }
    public void updateScoresGame(){
        TextView t1score = (TextView) findViewById(R.id.team1ScoreGame);
        TextView t2score = (TextView) findViewById(R.id.team2ScoreGame);
        t1score.setText("Score: " + Integer.toString(team1Score));
        t2score.setText("Score: " + Integer.toString(team2Score));
    }
}
