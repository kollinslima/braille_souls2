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

import static com.example.kollins.braille_souls2.MainMenu.braille_database;
import static com.example.kollins.braille_souls2.MainMenu.speakText;

public class LearnMode extends AppCompatActivity implements SensiveAreaListener {

    private TextView text;
    private TouchScreenView touchView;
    private int symbolIndex;
    private Vibrator vibrator;
    private long[] pattern = {0, 100};
    private ToneGenerator toneGen;

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                speakText(getResources().getString(R.string.tts_learn_mode_instructions), TextToSpeech.QUEUE_FLUSH);
                MainMenu.tts.playSilentUtterance(500, TextToSpeech.QUEUE_ADD, null);
                speakText(getResources().getString(R.string.tts_learn_mode_touch_instructions), TextToSpeech.QUEUE_ADD);
                MainMenu.tts.playSilentUtterance(500, TextToSpeech.QUEUE_ADD, null);
                speakText(getResources().getString(R.string.tts_lets_begin), TextToSpeech.QUEUE_ADD);

                setUpNextSymbol();
            }
        }).start();
//        Toast.makeText(this, getResources().getString(R.string.tts_learn_mode_instructions), Toast.LENGTH_SHORT).show();

    }

    private void setUpNextSymbol(){

        symbolIndex += 1;

        if (symbolIndex >= braille_database.size()){
            symbolIndex -= 1;
        }

        String symbol = braille_database.get(symbolIndex).getText();
        text.setText(symbol);
        speakText(getResources().getString(R.string.tts_spell_a_symbol), TextToSpeech.QUEUE_ADD);
        speakText(symbol, TextToSpeech.QUEUE_ADD);
        touchView.cleanAllSensitive();

        int i,row,column;
        for (i = 0, row = 0, column = 0; i < braille_database.get(symbolIndex).getBraille().length(); i++){

            if (braille_database.get(symbolIndex).getBraille().charAt(i) == '1') {
                touchView.setSensitive(row, column);
            }

            column = (column + 1)%touchView.getColumns();
            if (column == 0){
                row += 1;
            }
        }
    }

    private void setUpPreviousSymbol(){

        symbolIndex -= 1;
        if (symbolIndex < 0){
            symbolIndex += 1;
        }

        text.setText(braille_database.get(symbolIndex).getText());
        String aux = getResources().getString(R.string.tts_spell_a_symbol) + braille_database.get(symbolIndex).getText();
        speakText(aux, TextToSpeech.QUEUE_ADD);

        touchView.cleanAllSensitive();

        int i,row,column;
        for (i = 0, row = 0, column = 0; i < braille_database.get(symbolIndex).getBraille().length(); i++){

            if (braille_database.get(symbolIndex).getBraille().charAt(i) == '1') {
                touchView.setSensitive(row, column);
            }

            column = (column + 1)%touchView.getColumns();
            if (column == 0){
                row += 1;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        vibrator.cancel();
        MainMenu.tts.stop();
    }

    @Override
    public void onSensiveArea() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
        }else{
            vibrator.vibrate(pattern,0);
        }
    }

    @Override
    public void onChangeArea() {
        toneGen.startTone(ToneGenerator.TONE_CDMA_PIP,100);
    }

    @Override
    public void onNonSensiveArea() {
        vibrator.cancel();
    }

    @Override
    public void onDoubleTap() {
        setUpNextSymbol();
    }

    @Override
    public void onLongPress() {
        setUpPreviousSymbol();
    }

    @Override
    public void onDoubleFingerTap() {
        finish();
    }

    @Override
    public void onTap(float posX, float posY) {

    }

}
