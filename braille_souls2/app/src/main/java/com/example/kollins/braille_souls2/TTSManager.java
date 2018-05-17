package com.example.kollins.braille_souls2;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TTSManager implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;

    public TTSManager() {
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
}
