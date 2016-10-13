package tictactoe.unal.edu.androidunaltictactoe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by joan on 27/09/16.
 */
public class BoardView extends View {

    // Width of the board grid lines

    public static final int GRID_WIDTH = 6;
    private Paint mPaint;
    // Represents the internal state of the game
    private TicTacToeGame mGame;

    public BoardView(Context context) {
        super(context);
        initialize();
    }

    public BoardView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        initialize();

    }

    public BoardView(Context context, AttributeSet attrs) {

        super(context, attrs);
        initialize();
    }

    private Bitmap mHumanBitmap;

    private Bitmap mComputerBitmap;

    public void initialize() {

        mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.letter_x);

        mComputerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.letter_o);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mGame = new TicTacToeGame();
    }

    @Override

    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        // Determine the width and height of the View

        int boardWidth = getWidth();

        int boardHeight = getHeight();


        // Make thick, light gray lines

        mPaint.setColor(Color.BLACK);

        mPaint.setStrokeWidth(GRID_WIDTH);


        // Draw the two vertical board lines

        int cellWidth = boardWidth / 3;

        canvas.drawLine(cellWidth, 0, cellWidth, boardHeight, mPaint);

        canvas.drawLine(cellWidth * 2, 0, cellWidth * 2, boardHeight, mPaint);

        // Draw the two horizontal board lines

        canvas.drawLine(0, cellWidth,boardWidth , cellWidth, mPaint);

        canvas.drawLine(0, cellWidth *2 ,boardWidth , cellWidth *2, mPaint);


        // Draw all the X and O images

        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {

            int col = i % 3;

            int row = i / 3;

        // Define the boundaries of a destination rectangle for the image
            switch (col){
                case 0:
                    left = 0;
                    right = cellWidth;
                    break;
                case 1:
                    left = cellWidth;
                    right = cellWidth*2;
                    break;
                case 2:
                    left = cellWidth*2;
                    right = cellWidth*3;
                    break;
            }

            switch (row){
                case 0:
                    top = 0;
                    bottom = cellWidth;
                    break;
                case 1:
                    top = cellWidth;
                    bottom = cellWidth*2;
                    break;
                case 2:
                    top = cellWidth*2;
                    bottom = cellWidth*3;
                    break;
            }



            if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER) {

                canvas.drawBitmap(mHumanBitmap,

                        null, // src

                        new Rect(left, top, right, bottom), // dest

                        null);

            }

            else if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.COMPUTER_PLAYER) {

                canvas.drawBitmap(mComputerBitmap,

                        null, // src

                        new Rect(left, top, right, bottom), // dest

                        null);

            }

        }

    }

    public int getBoardCellWidth() {

        return getWidth() / 3;

    }

    public int getBoardCellHeight() {

        return getHeight() / 3;

    }

    public void setGame(TicTacToeGame game) {

        mGame = game;

    }
}
