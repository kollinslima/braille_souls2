package com.example.kollins.braille_souls2;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kollins.braille_souls2.custom_view.TouchScreenView_Test;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    public static TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tts = new TextToSpeech(this, this);
        super.onCreate(savedInstanceState);
        setContentView(new TouchScreenView_Test(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);

        tts.speak(getResources().getString(R.string.wellcome_message), TextToSpeech.QUEUE_FLUSH, null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.goToMainMenu:
                Intent intent = new Intent(this, MainMenu.class);
                startActivity(intent);
                return  true;
            case R.id.speakMenu:
                tts.speak(getResources().getString(R.string.tts_speak_menu), TextToSpeech.QUEUE_FLUSH, null);
                return true;
            case R.id.exitMenu:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void TTSReader(String input){
        tts.speak(input,TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.US);
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "Language not supported");
            }else{
                Log.e("TTS", "TTS inicialize failed");
            }
        }
    }

    @Override
    public void onDestroy() {
        if(tts != null){
            tts.speak("Shutting down Text To Speech engine", TextToSpeech.QUEUE_FLUSH, null);
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }
}
