package com.example.kollins.braille_souls2;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.kollins.braille_souls2.custom_view.SensiveAreaListener;
import com.example.kollins.braille_souls2.custom_view.TouchScreenView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.kollins.braille_souls2.MainMenu.braille_database;

public class PracticeMode extends AppCompatActivity implements SensiveAreaListener {

    public enum Direction {
        RIGHT,
        LEFT,
        UP,
        DOWN,
        LEFT_UP,
        LEFT_DOWN,
        RIGHT_UP,
        RIGHT_DOWN,
        LONG_DOWN,
        LONG_UP,
        LONG_LEFT_UP,
        LONG_LEFT_DOWN,
        LONG_RIGHT_UP,
        LONG_RIGHT_DOWN
    }

    private static double SIN_30 = Math.sin(Math.toRadians(30));
    private static double SIN_60 = Math.sin(Math.toRadians(60));

    private static int TIME_ANSWER = 5000;//ms
    private Timer timer;

    private TextView text;
    private TouchScreenView touchView;
    private Random random;

    private ArrayList<Point> points;
    private int[][] braille_matrix;
    private int symbolIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_and_touch);

        text = (TextView) findViewById(R.id.text);
        touchView = (TouchScreenView) findViewById(R.id.touchView);
        touchView.setSensiveAreaListener(this);
        random = new Random();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerAnswer(), TIME_ANSWER, TIME_ANSWER);

        points = new ArrayList<>();
        braille_matrix = new int[3][2];

        setUpRandomSymbol();
    }

    private void setUpRandomSymbol() {
        symbolIndex = random.nextInt(braille_database.size());
        text.setText(braille_database.get(symbolIndex).getText());
    }

    private void fitAnswer() {

        Point p1, p2;
        int posMatrixRow, posMatrixColum;

        if (points.size() == 0) {
            //Wrong answer
        } else if (points.size() < 6) {
            p1 = points.get(0);

            clearBrailleMatrix();

            posMatrixRow = 0;
            posMatrixColum = 0;
            braille_matrix[posMatrixRow][posMatrixColum] = 1;

            for (int i = 1; i < points.size(); i++) {
                p2 = points.get(i);
                Log.d("Position", "POS: " + checkPosition(p1, p2));
                switch (checkPosition(p1, p2)) {
                    case UP:
                        if (posMatrixRow == 0) {
                            moveAllElementsDown();
                        } else {
                            posMatrixRow -= 1;
                        }
                        break;
                    case DOWN:
                        if (posMatrixRow == 2) {
                            //Wrong Answer
                        } else {
                            posMatrixRow += 1;
                        }
                        break;
                    case LEFT:
                        if (posMatrixColum == 0) {
                            moveAllElementsRight();
                        } else {
                            posMatrixColum -= 1;
                        }
                        break;
                    case RIGHT:
                        if (posMatrixColum == 1) {
                            //Wrong Answer
                        } else {
                            posMatrixColum += 1;
                        }
                        break;
                    case LEFT_UP:
                        if (posMatrixColum == 0) {
                            moveAllElementsRight();
                        } else {
                            posMatrixColum -= 1;
                        }
                        if (posMatrixRow == 0) {
                            moveAllElementsDown();
                        } else {
                            posMatrixRow -= 1;
                        }
                        break;
                    case RIGHT_UP:
                        if (posMatrixColum == 1) {
                            //Wrong Answer
                        } else {
                            posMatrixColum += 1;
                        }
                        if (posMatrixRow == 0) {
                            moveAllElementsDown();
                        } else {
                            posMatrixRow -= 1;
                        }
                        break;
                    case LEFT_DOWN:
                        if (posMatrixColum == 0) {
                            moveAllElementsRight();
                        } else {
                            posMatrixColum -= 1;
                        }
                        if (posMatrixRow == 2) {
                            //Wrong Answer
                        } else {
                            posMatrixRow += 1;
                        }
                        break;
                    case RIGHT_DOWN:
                        if (posMatrixColum == 1) {
                            //Wrong Answer
                        } else {
                            posMatrixColum += 1;
                        }
                        if (posMatrixRow == 2) {
                            //Wrong Answer
                        } else {
                            posMatrixRow += 1;
                        }
                        break;
                    case LONG_UP:
                        if (posMatrixRow == 0) {
                            moveAllElementsDown();
                            moveAllElementsDown();
                        } else {
                            posMatrixRow -= 1;
                        }
                        break;
                    case LONG_DOWN:
                        if (posMatrixRow == 2) {
                            //Wrong Answer
                        } else {
                            posMatrixRow += 2;
                        }
                        break;
                    default:
                        break;
                }

                braille_matrix[posMatrixRow][posMatrixColum] = 1;
                p1 = p2;
            }

        } else {
            //Wrong answer
        }

        checkAnswer();
        points.clear();
    }

    private void checkAnswer() {

        for (int i = 0; i < 3; i++) {
            Log.d("MATRIX", String.format("%d %d", braille_matrix[i][0], braille_matrix[i][1]));
        }

        String brailleAnswer = braille_database.get(symbolIndex).getBraille();
        int[][] matrixAnswer = new int[3][2];
        int numDots = 0;

        boolean isRight = true;

        for (int i = 0, row = 0, column = 0; i < brailleAnswer.length(); i++) {

            matrixAnswer[row][column] = Integer.parseInt(String.valueOf(brailleAnswer.charAt(i)));

            if (matrixAnswer[row][column] == 1) {
                numDots += 1;
            }

            column = (column + 1) % 2;
            if (column == 0) {
                row += 1;
            }
        }

        Log.d("Convol", "Dots: " + numDots);
        //Convolution
        int auxNumDots = 0;
        for (int i = 0; i < 3; i++) {
            isRight = true;
            auxNumDots = 0;
            for (int k = 0, m = i; m < 3; k++, m++) {
                for (int l = 0; l < 2; l++) {
                    Log.d("Convol", "Answer: " + matrixAnswer[m][l]);
                    Log.d("Convol", "My: " + braille_matrix[k][l]);
                    if (matrixAnswer[m][l] != braille_matrix[k][l]) {
                        isRight = false;
                    } else if (matrixAnswer[m][l] == 1) {
                        auxNumDots += 1;
                    }
                }
            }
            Log.d("Convol", "Result: " + isRight);
            Log.d("Convol", "AuxDots: " + auxNumDots);
            Log.d("Convol", "Next Convol");
            if (isRight && (auxNumDots == numDots)) {
                break;
            }

        }


        if (isRight && (auxNumDots == numDots)) {
            Log.d("Answer", "Correto");
        } else {
            Log.d("Answer", "Errado");
        }

        setUpRandomSymbol();
    }

    private void moveAllElementsDown() {
//        int[] aux = new int[2];
//        for (int i = 0; i < 2; i++) {
//            for (int j = 0; j < 2; j++) {
//                aux[j] = braille_matrix[i + 1][j];
//                braille_matrix[i + 1][j] = braille_matrix[i][j];
//                braille_matrix[i][j] = 0;
//            }
//        }
    }

    private void moveAllElementsRight() {
        for (int i = 0; i < 3; i++) {
            braille_matrix[i][1] = braille_matrix[i][0];
            braille_matrix[i][0] = 0;
        }
    }

    private Direction checkPosition(Point p1, Point p2) {
        float catX = Math.abs(p1.getPosX() - p2.getPosX());
        float catY = Math.abs(p1.getPosY() - p2.getPosY());

        double hip = Math.sqrt(Math.pow(catX, 2) + Math.pow(catY, 2));

        double sin = catY / hip;

        if (sin < SIN_30) {
            if ((p1.getPosX() - p2.getPosX()) < 0) {
                return Direction.RIGHT;
            } else {
                return Direction.LEFT;
            }
        } else if (sin < SIN_60) {

            float posX = p1.getPosX() - p2.getPosX();
            float posY = p1.getPosY() - p2.getPosY();
            boolean longDistance = (Math.abs(touchView.getScreenHeight() / posY) < 2) ? true : false;

            if (posX > 0 && posY > 0) {
                return longDistance ? Direction.LONG_LEFT_UP : Direction.LEFT_UP;
            } else if (posX > 0 && posY < 0) {
                return longDistance ? Direction.LONG_LEFT_DOWN : Direction.LEFT_DOWN;
            } else if (posX < 0 && posY > 0) {
                return longDistance ? Direction.LONG_RIGHT_UP : Direction.RIGHT_UP;
            } else {
                return longDistance ? Direction.LONG_RIGHT_DOWN : Direction.RIGHT_DOWN;
            }

        } else {
            float posY = p1.getPosY() - p2.getPosY();

            boolean longDistance = (Math.abs(touchView.getScreenHeight() / posY) < 2) ? true : false;

            if (posY < 0) {
                return longDistance ? Direction.LONG_DOWN : Direction.DOWN;
            } else {
                return longDistance ? Direction.LONG_UP : Direction.UP;

            }
        }
    }

    private void clearBrailleMatrix() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                braille_matrix[i][j] = 0;
            }
        }
    }

    @Override
    public void onSensiveArea() {

    }

    @Override
    public void onNonSensiveArea() {

    }

    @Override
    public void onChangeArea() {

    }

    @Override
    public void onDoubleTap() {
    }

    @Override
    public void onLongPress() {

    }

    @Override
    public void onDoubleFingerTap() {

    }

    @Override
    public void onTap(float posX, float posY) {
        points.add(new Point(posX, posY));
    }

    private class TimerAnswer extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fitAnswer();
                }
            });
        }
    }
}
