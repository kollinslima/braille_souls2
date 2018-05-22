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

import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

import static com.example.kollins.braille_souls2.MainMenu.speakText;

public class TTSManager implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;

    public TTSManager() {
    }

    public void TTSReader(String input){
        speakText(input,TextToSpeech.QUEUE_FLUSH);
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
