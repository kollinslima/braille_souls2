/*
 * Copyright 2018
 * Kollins Lima (kollins.lima@gmail.com)
 * Otávio Sumi (otaviosumi@hotmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kollins.braille_souls2;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kollins.braille_souls2.custom_view.SensiveAreaListener;
import com.example.kollins.braille_souls2.custom_view.TouchScreenView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.kollins.braille_souls2.MainMenu.braille_database;
import static com.example.kollins.braille_souls2.MainMenu.speakText;

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

    private static double SIN_15 = Math.sin(Math.toRadians(15));
    private static double SIN_75 = Math.sin(Math.toRadians(75));

    private final long VIBRATE_TIME = 500; //ms
    private int TIME_ANSWER = 10000;//ms
    private Timer timer;

    private Handler handler;

    private TextView text;
    private TouchScreenView touchView;
    private Random random;

    private ArrayList<Point> points;
    private int[][] braille_matrix;
    private int symbolIndex;
    private Vibrator vibrator;

    private ToneGenerator toneGen;
    private ProgressHandler ph;

    private Timer mTimer;
    private short timerCount;
    public static final short INTERVAL_PRACTICE = 5; //s

    private ReentrantLock lock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_and_touch);

        text = (TextView) findViewById(R.id.text);
        touchView = (TouchScreenView) findViewById(R.id.touchView);
        touchView.setSensiveAreaListener(this);
        random = new Random();

        handler = new Handler(callback);

        lock = new ReentrantLock();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);

        points = new ArrayList<>();
        braille_matrix = new int[3][2];

        ph = new ProgressHandler();

    }

    public void timeAlert(int timeAnswer) {
        speakText(getResources().getString(R.string.practice_mode_time_alert) + timeAnswer/1000 + getResources().getString(R.string.practice_mode_time_alert_sec), TextToSpeech.QUEUE_ADD);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                speakText(getResources().getString(R.string.practice_mode_instructions), TextToSpeech.QUEUE_FLUSH);
                MainMenu.tts.playSilentUtterance(500, TextToSpeech.QUEUE_ADD, null);
                speakText(getResources().getString(R.string.practice_mode_touch_instructions), TextToSpeech.QUEUE_ADD);
                MainMenu.tts.playSilentUtterance(500, TextToSpeech.QUEUE_ADD, null);
                timeAlert(TIME_ANSWER);
                MainMenu.tts.playSilentUtterance(500, TextToSpeech.QUEUE_ADD, null);

                setUpRandomSymbol();
            }
        }).start();
    }

    private void speakTutorial(String message, int type) {
        speakText(message, type);
        try {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTutorialPractice(), MainMenu.TIME_TUTORIAL, MainMenu.TIME_TUTORIAL);
            synchronized (lock) {
                lock.wait();
            }
            mTimer.cancel();
            timerCount = 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            timer.cancel();
        } catch (NullPointerException e){
            Log.e("Timer", "Timer not running yet.",e);
        }
        MainMenu.tts.stop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            timer.cancel();
        } catch (NullPointerException e){
            Log.e("Timer", "Timer not running yet.",e);
        }
    }

    private void setUpRandomSymbol() {
        symbolIndex = random.nextInt(braille_database.size());

        String symbol = braille_database.get(symbolIndex).getText();
        text.setText(symbol);

        speakText(symbol, TextToSpeech.QUEUE_ADD);

        //Just waiting
        while(MainMenu.tts.isSpeaking()){
            Log.d("Speak", "I'm waiting...");
        }

        timer = new Timer();
        timer.schedule(new TimerAnswer(), TIME_ANSWER, TIME_ANSWER);
    }

    private void fitAnswer() {

        Point p1, p2;
        int posMatrixRow, posMatrixColum;

        clearBrailleMatrix();

        if ((points.size() > 0) && (points.size() <= 6)) {
            p1 = points.get(0);

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
                        if (posMatrixRow < 2) {
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
                        if (posMatrixColum < 1) {
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
                        if (posMatrixColum < 1) {
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
                        if (posMatrixRow < 2) {
                            posMatrixRow += 1;
                        }
                        break;
                    case RIGHT_DOWN:
                        if (posMatrixColum < 1) {
                            posMatrixColum += 1;
                        }
                        if (posMatrixRow < 2) {
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
                        if (posMatrixRow == 0) {
                            posMatrixRow += 2;
                        } else if (posMatrixRow < 2){
                            posMatrixRow += 1;
                        }
                        break;
                    case LONG_LEFT_UP:
                        if (posMatrixColum == 0) {
                            moveAllElementsRight();
                        } else {
                            posMatrixColum -= 1;
                        }
                        if (posMatrixRow == 0) {
                            moveAllElementsDown();
                            moveAllElementsDown();
                        } else {
                            posMatrixRow -= 1;
                        }
                        break;
                    case LONG_RIGHT_UP:
                        if (posMatrixColum < 1) {
                            posMatrixColum += 1;
                        }
                        if (posMatrixRow == 0) {
                            moveAllElementsDown();
                            moveAllElementsDown();
                        } else {
                            posMatrixRow -= 1;
                        }
                        break;
                    case LONG_LEFT_DOWN:
                        if (posMatrixColum == 0) {
                            moveAllElementsRight();
                        } else {
                            posMatrixColum -= 1;
                        }
                        if (posMatrixRow == 0) {
                            posMatrixRow += 2;
                        } else if (posMatrixRow < 2){
                            posMatrixRow += 1;
                        }
                        break;
                    case LONG_RIGHT_DOWN:
                        if (posMatrixColum < 1) {
                            posMatrixColum += 1;
                        }
                        if (posMatrixRow == 0) {
                            posMatrixRow += 2;
                        } else if (posMatrixRow < 2){
                            posMatrixRow += 1;
                        }
                        break;
                    default:
                        break;
                }

                braille_matrix[posMatrixRow][posMatrixColum] = 1;
                p1 = p2;
            }

        }

        points.clear();

        checkAnswer();
        setUpRandomSymbol();
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
        boolean continueConvol = true;
        for (int i = 0; (i < 3) && continueConvol; i++) {
            for (int j = 0; (j < 2) && continueConvol; j++) {
                isRight = true;
                auxNumDots = 0;
                for (int k = 0, m = i; (m < 3) && isRight; k++, m++) {
                    for (int l = 0, n = j; (n < 2) && isRight; l++, n++) {
                        Log.d("Convol", "Answer: " + matrixAnswer[m][n]);
                        Log.d("Convol", "My: " + braille_matrix[k][l]);
                        if (matrixAnswer[m][n] != braille_matrix[k][l]) {
                            isRight = false;
                        } else if (matrixAnswer[m][n] == 1) {
                            auxNumDots += 1;
                        }
                    }
                }
                Log.d("Convol", "Result: " + isRight);
                Log.d("Convol", "AuxDots: " + auxNumDots);
                Log.d("Convol", "Next Convol");
                if (isRight && (auxNumDots == numDots)) {
                    continueConvol = false;
                }

            }
        }

        Log.d("Convol", "End Convol");

        if (isRight && (auxNumDots == numDots)) {
            ph.addHit();
            speakText(getResources().getString(R.string.correct), TextToSpeech.QUEUE_FLUSH);
//            Toast.makeText(this, "Right Answer", Toast.LENGTH_SHORT).show();
            toneGen.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK,200);
        } else {
            ph.takeHit();
            speakText(getResources().getString(R.string.wrong), TextToSpeech.QUEUE_FLUSH);
//            Toast.makeText(this, "Wrong Answer", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(VIBRATE_TIME, VibrationEffect.DEFAULT_AMPLITUDE));
            }else{
                vibrator.vibrate(500);
            }
        }
    }

    private void moveAllElementsDown() {
        int[] auxRow = new int[2];
        int auxElement;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                auxElement = braille_matrix[i][j];
                braille_matrix[i][j] = auxRow[j];
                auxRow[j] = auxElement;
            }
        }
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

        if (sin < SIN_15) {
            if ((p1.getPosX() - p2.getPosX()) < 0) {
                return Direction.RIGHT;
            } else {
                return Direction.LEFT;
            }
        } else if (sin < SIN_75) {

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
        try {
            points.remove(points.size() - 1);
            timer.cancel();
            fitAnswer();
        } catch (NullPointerException e){
            Log.e("Timer", "Timer not running yet.",e);
            points.clear();
        }
    }

    @Override
    public void onDoubleFingerTap() {
        finish();
    }

    @Override
    public void onTap(float posX, float posY) {
        points.add(new Point(posX, posY));
    }

    Handler.Callback callback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            fitAnswer();
            return true;
        }
    };

    private class TimerAnswer extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
            timer.cancel();
        }
    }

    public class TimerTutorialPractice extends TimerTask {

        @Override
        public void run() {
            timerCount += 1;
            if (MainMenu.tts.isSpeaking()) {
                timerCount = 0;
            } else if (timerCount == INTERVAL_PRACTICE) {
                synchronized (lock) {
                    lock.notify();
                }

                timerCount = 0;
            }
        }
    }
}
