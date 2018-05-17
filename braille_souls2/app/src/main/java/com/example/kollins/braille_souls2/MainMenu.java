package com.example.kollins.braille_souls2;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kollins.braille_souls2.custom_view.TouchScreenView_Test;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        MainActivity.tts.speak("Main Menu Openned", TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public void onDestroy() {
        MainActivity.tts.speak("Main menu closed", TextToSpeech.QUEUE_ADD, null);
        super.onDestroy();
    }
}
