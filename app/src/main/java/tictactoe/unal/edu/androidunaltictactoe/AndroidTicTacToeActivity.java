package tictactoe.unal.edu.androidunaltictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class AndroidTicTacToeActivity extends AppCompatActivity {

    // Represents the internal state of the game
    private TicTacToeGame mGame;

    private BoardView mBoardView;

    // Listen for touches on the board
    private OnTouchListener mTouchListener;

    // Buttons making up the board
    //private Button mBoardButtons[];

    MediaPlayer mHumanMediaPlayer;

    MediaPlayer mComputerMediaPlayer;

    // Various text displayed
    private TextView mInfoTextView;

    private TextView mHumanTextView;
    private TextView mTieTextView;
    private TextView mAndroidTextView;

    private TextView mScoreHumanTextView;
    private TextView mScoreTieTextView;
    private TextView mScoreAndroidTextView;

    private int humanScore, tieScore, androidScore;
    private String menuNuevoJuego, menuResetScores;

    //static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    private int selected;//-1,0,1,2

    private boolean mGameOver;

    private boolean mSoundOn;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);

        mInfoTextView = (TextView) findViewById(R.id.information);

        mHumanTextView = (TextView) findViewById(R.id.controlHuman);
        mTieTextView = (TextView) findViewById(R.id.controlTie);
        mAndroidTextView = (TextView) findViewById(R.id.controlComputer);


        mScoreHumanTextView = (TextView) findViewById(R.id.scoreHuman);
        mScoreTieTextView = (TextView) findViewById(R.id.scoreTie);
        mScoreAndroidTextView = (TextView) findViewById(R.id.scoreComputer);

        mGame = new TicTacToeGame();
        mTouchListener = new OnTouchListener();

        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);

        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        menuNuevoJuego = getResources().getString(R.string.menu_new_game);
        menuResetScores = getResources().getString(R.string.menu_reset_scores);

        mHumanTextView.setText(R.string.human_score);
        mTieTextView.setText(R.string.tie_score);
        mAndroidTextView.setText(R.string.android_score);

        //Logica para restaurar la instancia

        //mPrefs = getSharedPreferences("ttt_prefs",MODE_PRIVATE);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);
        String difficultyLevel = mPrefs.getString("difficulty_level",
                getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);

        humanScore = mPrefs.getInt("mHumanWins",0);
        androidScore = mPrefs.getInt("mComputerWins",0);
        tieScore = mPrefs.getInt("mTies",0);
        selected = mPrefs.getInt("level",0);


        if(savedInstanceState==null){
            //resetScores();
            startNewGame();
            //selected = -1;
            mGameOver = false;
        }else{
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            //humanScore = savedInstanceState.getInt("mHumanWins");
            //androidScore = savedInstanceState.getInt("mComputerWins");
            //tieScore = savedInstanceState.getInt("mTies");
        }

        updateScores();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    private void startNewGame() {
        mGame.clearBoard();
        mBoardView.invalidate();
        mGameOver = false;

        // Reset all buttons
        /*for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }*/

        // Human goes first
        mInfoTextView.setText(R.string.first_human);

        //reset the initial scores
        //resetScores();

        //set the string values to the scores
        updateScores();
    }

    private void resetScores() {
        //Initial Scores
        humanScore = 0;
        tieScore = 0;
        androidScore = 0;
    }

    private void updateScores() {
        mScoreHumanTextView.setText(String.valueOf(humanScore));
        mScoreTieTextView.setText(String.valueOf(tieScore));
        mScoreAndroidTextView.setText(String.valueOf(androidScore));
    }

    /*private void disableButtons() {
        // Disable all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setEnabled(false);
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AndroidTicTacToe Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://tictactoe.unal.edu.androidunaltictactoe/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "AndroidTicTacToe Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://tictactoe.unal.edu.androidunaltictactoe/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();

        //Save the current instances
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins",humanScore);
        ed.putInt("mComputerWins",androidScore);
        ed.putInt("mTies",tieScore);
        ed.putInt("level",selected);
        ed.commit();
    }


    private class OnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched

            int col = (int) event.getX() / mBoardView.getBoardCellWidth();

            int row = (int) event.getY() / mBoardView.getBoardCellHeight();

            int pos = row * 3 + col;

            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {

                    //delay
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Log.v(LOG_TAG, "Hello");
                        }

                    }, 4000);



                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }

                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_human);
                } else {

                    //there is a winner
                    mGameOver = true;
                    if (winner == 1) {
                        mInfoTextView.setText(R.string.result_tie);
                        tieScore++;
                    } else if (winner == 2) {
                        humanScore++;
                        String defaultMessage = getResources().getString(R.string.result_human_wins);
                        mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                    } else if (winner == 3) {
                        mInfoTextView.setText(R.string.result_computer_wins);
                        androidScore++;
                    }

                    //disableButtons();
                    updateScores();

                }

            }
            // So we aren';t notified of continued events when finger is moved
            return false;
        }
    }



    // Handles clicks on the game board buttons
    /*
    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled()) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }

                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_human);
                } else {

                    if (winner == 1) {
                        mInfoTextView.setText(R.string.result_tie);
                        tieScore++;
                    } else if (winner == 2) {
                        mInfoTextView.setText(R.string.result_human_wins);
                        humanScore++;
                    } else if (winner == 3) {
                        mInfoTextView.setText(R.string.result_computer_wins);
                        androidScore++;
                    }

                    disableButtons();
                    updateScores();

                }

            }
        }


    }*/

    private boolean setMove(char player, int location) {

        if (mGame.setMove(player, location)) {
                if(player == TicTacToeGame.COMPUTER_PLAYER){
                    if(mSoundOn) {
                        mComputerMediaPlayer.start();
                    }

                }else if(player == TicTacToeGame.HUMAN_PLAYER){
                    if(mSoundOn) {
                        mHumanMediaPlayer.start();
                    }
                }
                mBoardView.invalidate(); // Redraw the board
                return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*
        if(item.getTitle().equals(menuNuevoJuego)){
            startNewGame();
            return true;
        }

        if(item.getTitle().equals(menuResetScores)){
            resetScores();
            updateScores();
            return true;
        }
        */

        switch (item.getItemId()) {

            case R.id.new_game:
                startNewGame();
                return true;

            case R.id.settings:
                startActivityForResult(new Intent(this, Settings.class), 0);
                return true;

            case R.id.reset:
                resetScores();
                updateScores();
                return true;

            case R.id.quit:

                showDialog(DIALOG_QUIT_ID);

                return true;

        }

        return false;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings

            mSoundOn = mPrefs.getBoolean("sound", true);

            String difficultyLevel = mPrefs.getString("difficulty_level",
                    getResources().getString(R.string.difficulty_harder));

            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {

        Dialog dialog = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        switch (id) {

            /*case DIALOG_DIFFICULTY_ID:

                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {

                        getResources().getString(R.string.difficulty_easy),

                        getResources().getString(R.string.difficulty_harder),

                        getResources().getString(R.string.difficulty_expert)};


                        // TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
                        // selected is the radio button that should be selected.
                        if(selected==-1){
                            selected = 2;//Default difficulty is Expert
                        }

                        builder.setSingleChoiceItems(levels, selected,

                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int item) {

                                        dialog.dismiss(); // Close dialog


                                        selected = item;
                                        switch (item){
                                            case 0:
                                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                                break;
                                            case 1:
                                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                                break;
                                            case 2:
                                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                                break;
                                        }

                                        // Display the selected difficulty level
                                        Toast.makeText(getApplicationContext(), levels[item],Toast.LENGTH_SHORT).show();

                                    }

                                });

                        dialog = builder.create();
             break;*/
            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                AndroidTicTacToeActivity.this.finish();

                            }

                        })

                        .setNegativeButton(R.string.no, null);

                dialog = builder.create();
             break;
        }

        return dialog;
    }

    @Override

    protected void onResume() {

        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.wip_sound);

        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.kick_sound);

    }

    @Override

    protected void onPause() {

        super.onPause();

        mHumanMediaPlayer.release();

        mComputerMediaPlayer.release();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharArray("board",mGame.getBoardState());
        outState.putBoolean("mGameOver",mGameOver);
        //outState.putInt("mHumanWins",Integer.valueOf(humanScore));
        //outState.putInt("mComputerWins",Integer.valueOf(androidScore));
        //outState.putInt("mTies",Integer.valueOf(tieScore));
        outState.putCharSequence("info",mInfoTextView.getText());
        //outState.putChar("mGoFirst",);

    }
}
