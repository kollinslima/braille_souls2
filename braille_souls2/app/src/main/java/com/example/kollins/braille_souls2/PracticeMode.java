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

import java.util.ArrayList;
import java.util.Random;

import static com.example.kollins.braille_souls2.MainMenu.braille_database;

public class PracticeMode extends AppCompatActivity implements SensiveAreaListener{

    private TextView text;
    private TouchScreenView touchView;
    private Random random;

    private ArrayList<Point> points;
    private int [][] braille_matrix;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_and_touch);

        text = (TextView) findViewById(R.id.text);
        touchView = (TouchScreenView) findViewById(R.id.touchView);
        random = new Random();

        points = new ArrayList<>();
        braille_matrix = new int[3][2];

        setUpRandomSymbol();
    }

    private void setUpRandomSymbol() {

        int symbolIndex = random.nextInt(braille_database.size());

        text.setText(braille_database.get(symbolIndex).getText());

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

    private void fitAnswer(){

        Point p1, p2;

        //Remove last two because two fingers
        //are used to confirm answer.
        points.remove(points.size()-1);
        points.remove(points.size()-1);
        if (points.size() < 6){
            p1 = points.get(0);

            clearBrailleMatrix();

            braille_matrix[0][0] = 1;

            for (int i = 1; i < 6; i++){
                p2 = points.get(i);

//                checkHorizontalPosition(p1, p2);
            }

        }
        points.clear();
    }

    private void clearBrailleMatrix(){
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 2; j++){
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
        fitAnswer();
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
}
