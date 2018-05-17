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
import android.widget.TextView;

import com.example.kollins.braille_souls2.custom_view.SensiveAreaListener;
import com.example.kollins.braille_souls2.custom_view.TouchScreenView;

import static com.example.kollins.braille_souls2.MainMenu.braille_database;

public class LearnMode extends AppCompatActivity implements SensiveAreaListener {

    private TextView text;
    private TouchScreenView touchView;
    private int symbolIndex;
    private Vibrator vibrator;
    private long[] pattern = {0, 100};
    ToneGenerator toneGen;

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

        setUpNextSymbol();
    }

    private void setUpNextSymbol(){

        symbolIndex += 1;

        if (symbolIndex >= braille_database.size()){
            symbolIndex -= 1;
        }

        text.setText(braille_database.get(symbolIndex).getText());

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