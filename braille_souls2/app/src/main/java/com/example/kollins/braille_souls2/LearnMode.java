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
import android.media.MediaActionSound;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kollins.braille_souls2.custom_view.SensiveAreaListener;
import com.example.kollins.braille_souls2.custom_view.TouchScreenView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.kollins.braille_souls2.MainMenu.braille_database;
import static com.example.kollins.braille_souls2.MainMenu.speakText;
import static com.example.kollins.braille_souls2.MainMenu.user;

public class LearnMode extends AppCompatActivity implements SensiveAreaListener {

    private TextView text;
    private TouchScreenView touchView;
    private int symbolIndex;
    private Vibrator vibrator;
    private long[] pattern = {0, 100};
    private ToneGenerator toneGen;

    private ReentrantLock lock;

    private Timer mTimer;
    private short timerCount;
    public static final short INTERVAL_LEARN = 5; //s

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_and_touch);

        text = (TextView) findViewById(R.id.text);
        touchView = (TouchScreenView) findViewById(R.id.touchView);
        touchView.setSensiveAreaListener(this);
        symbolIndex = -1;

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);

        lock = new ReentrantLock();

        new Thread(new Runnable() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setUpNextSymbol();
                    }
                });

                if (!MainMenu.user.isTutorialEnd()) {
                    while (!MainMenu.user.isFirstBeep()) {
                        speakTutorial(getResources().getString(R.string.tts_learn_mode_instructions1), TextToSpeech.QUEUE_FLUSH);
                    }
                    while (!MainMenu.user.isFirstVibration()) {
                        speakTutorial(getResources().getString(R.string.tts_learn_mode_instructions2), TextToSpeech.QUEUE_ADD);
                    }
                    while (!MainMenu.user.isFirstDoubleTap()) {
                        speakTutorial(getResources().getString(R.string.tts_learn_mode_touch_instructions1), TextToSpeech.QUEUE_ADD);
                    }
                    while (!MainMenu.user.isFirstLongTap()) {
                        speakTutorial(getResources().getString(R.string.tts_learn_mode_touch_instructions2), TextToSpeech.QUEUE_ADD);
                    }
                    while (!MainMenu.user.isFirstTwoFingers()) {
                        speakTutorial(getResources().getString(R.string.tts_learn_mode_touch_instructions3), TextToSpeech.QUEUE_ADD);
                    }
                    speakText(getResources().getString(R.string.tts_lets_begin), TextToSpeech.QUEUE_ADD);
                    MainMenu.user.setTutorialEnd(true);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setUpPreviousSymbol();  //Repeat A
                        }
                    });
                }
            }
        }).start();

    }

    private void speakTutorial(String message, int type) {
        speakText(message, type);
        try {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTutorialLearn(), MainMenu.TIME_TUTORIAL, MainMenu.TIME_TUTORIAL);
            synchronized (lock) {
                lock.wait();
            }
            mTimer.cancel();
            timerCount = 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setUpNextSymbol() {

        symbolIndex += 1;

        if (symbolIndex >= braille_database.size()) {
            symbolIndex -= 1;
        }

        String symbol = braille_database.get(symbolIndex).getText();
        text.setText(symbol);

        if (MainMenu.user.isTutorialEnd()) {
            speakText(getResources().getString(R.string.tts_spell_a_symbol), TextToSpeech.QUEUE_ADD);
            speakText(symbol, TextToSpeech.QUEUE_ADD);
        }

        touchView.cleanAllSensitive();

        int i, row, column;
        for (i = 0, row = 0, column = 0; i < braille_database.get(symbolIndex).getBraille().length(); i++) {

            if (braille_database.get(symbolIndex).getBraille().charAt(i) == '1') {
                touchView.setSensitive(row, column);
            }

            column = (column + 1) % touchView.getColumns();
            if (column == 0) {
                row += 1;
            }
        }
    }

    private void setUpPreviousSymbol() {

        symbolIndex -= 1;
        if (symbolIndex < 0) {
            symbolIndex += 1;
        }

        text.setText(braille_database.get(symbolIndex).getText());

        if (MainMenu.user.isTutorialEnd()) {
            String aux = getResources().getString(R.string.tts_spell_a_symbol) + braille_database.get(symbolIndex).getText();
            speakText(aux, TextToSpeech.QUEUE_ADD);
        }

        touchView.cleanAllSensitive();

        int i, row, column;
        for (i = 0, row = 0, column = 0; i < braille_database.get(symbolIndex).getBraille().length(); i++) {

            if (braille_database.get(symbolIndex).getBraille().charAt(i) == '1') {
                touchView.setSensitive(row, column);
            }

            column = (column + 1) % touchView.getColumns();
            if (column == 0) {
                row += 1;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        vibrator.cancel();
        MainMenu.tts.stop();
        MainMenu.user.saveData();
    }

    @Override
    public void onSensiveArea() {
        if (!MainMenu.user.isFirstVibration()) {
            synchronized (lock) {
                lock.notify();
            }
        }
        MainMenu.user.setFirstVibration(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
        } else {
            vibrator.vibrate(pattern, 0);
        }
    }

    @Override
    public void onChangeArea() {
        if (!MainMenu.user.isFirstBeep()) {
            synchronized (lock) {
                lock.notify();
            }
        }
        MainMenu.user.setFirstBeep(true);
        toneGen.startTone(ToneGenerator.TONE_CDMA_PIP, 100);
    }

    @Override
    public void onNonSensiveArea() {
        vibrator.cancel();
    }

    @Override
    public void onDoubleTap() {
        if (!MainMenu.user.isFirstDoubleTap()) {
            synchronized (lock) {
                lock.notify();
            }
        }
        MainMenu.user.setFirstDoubleTap(true);
        setUpNextSymbol();
    }

    @Override
    public void onLongPress() {
        if (MainMenu.user.isFirstDoubleTap()) {
            if (!MainMenu.user.isFirstLongTap()) {
                synchronized (lock) {
                    lock.notify();
                }
            }
            MainMenu.user.setFirstLongTap(true);
        }
        setUpPreviousSymbol();
    }

    @Override
    public void onDoubleFingerTap() {
        if (!MainMenu.user.isFirstTwoFingers()) {
            synchronized (lock) {
                lock.notify();
            }
            MainMenu.user.setFirstTwoFingers(true);
        } else {
            finish();
        }
    }

    @Override
    public void onTap(float posX, float posY) {

    }

    public class TimerTutorialLearn extends TimerTask {

        @Override
        public void run() {
            timerCount += 1;
            if (MainMenu.tts.isSpeaking()) {
                timerCount = 0;
            } else if (timerCount == INTERVAL_LEARN) {
                synchronized (lock) {
                    lock.notify();
                }

                timerCount = 0;
            }
        }
    }

}
