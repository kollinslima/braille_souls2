/*
 * Copyright 2018
 * Kollins Lima (kollins.lima@gmail.com)
 * Otávio Sumi (otaviosumi@hotmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.kollins.braille_souls2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.TestLooperManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.kollins.braille_souls2.database.DBKey;
import com.example.sumi.brailler.database.DataBaseHelper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainMenu extends AppCompatActivity implements TextToSpeech.OnInitListener{


    public static final String DATABASE_TAG = "DatabaseTest";
    public static final ArrayList<DBKey> braille_database = new ArrayList<>();
    public static TextToSpeech tts;
    private String lastBtnTouched;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tts = new TextToSpeech(this, this);
        Log.e("Log", "APP - Application started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        loadDataBase();
        lastBtnTouched = "";
    }

    private void loadDataBase() {

        DataBaseHelper mDBHelper = new DataBaseHelper(this);
        SQLiteDatabase mDb = null;


        try {
            mDBHelper.updateDataBase();
            mDb = mDBHelper.getWritableDatabase();

            Cursor cursor = mDb.query("brailleTable", null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {

                    DBKey key = new DBKey(cursor.getString(cursor.getColumnIndex("PlainText")),
                            cursor.getString(cursor.getColumnIndex("Braille")));
                    braille_database.add(key);

//                    Log.d(DATABASE_TAG, "Braille: " + cursor.getString(cursor.getColumnIndex("Braille"))
//                            + " - Text: " + braille_to_text.get(cursor.getString(cursor.getColumnIndex("Braille"))));
                } while (cursor.moveToNext());
            }

        } catch (SQLException mSQLException) {
            throw mSQLException;
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        } finally {
            mDb.close();
        }
    }

    private Context getContext() {
        return this;
    }

    public void toLearnMode(View view) {
        Log.e("Log", "APP - Learn button selected");
        if(lastBtnTouched != "learnMode"){
            lastBtnTouched = "learnMode";
            tts.speak(getResources().getString(R.string.tts_select_learn_mode), TextToSpeech.QUEUE_FLUSH, null);
            return;
        }else{
            Intent intent = new Intent(this, LearnMode.class);
            startActivity(intent);
        }
    }

    public void toPracticeMode(View view) {
        Log.e("Log", "APP - Practice button selected");
        if(lastBtnTouched != "practiceMode"){
            lastBtnTouched = "practiceMode";
            tts.speak(getResources().getString(R.string.tts_select_practice_mode), TextToSpeech.QUEUE_FLUSH, null);
        }else{
            Intent intent = new Intent(this, PracticeMode.class);
            startActivity(intent);
        }
    }

    public void exitButton(View view) {
        Log.e("Log", "APP - Exit button selected");
        if(lastBtnTouched != "exitBtn"){
            lastBtnTouched = "exitBtn";
            tts.speak(getResources().getString(R.string.tts_select_exit), TextToSpeech.QUEUE_FLUSH, null);
            return;
        }else{
            tts.speak(getResources().getString(R.string.tts_exiting_app), TextToSpeech.QUEUE_FLUSH, null);
            finish();
        }
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.getDefault());
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("Log", "TTS - Language not supported - Using US");
                tts.setLanguage(Locale.US);
                tts.speak(getResources().getString(R.string.tts_wellcome_message), TextToSpeech.QUEUE_FLUSH, null);
                tts.speak(getResources().getString(R.string.tts_main_menu_button_layout), TextToSpeech.QUEUE_ADD, null);
            }else{
                Log.e("Log", "TTS - Initialize succeeded");
                tts.speak(getResources().getString(R.string.tts_wellcome_message), TextToSpeech.QUEUE_FLUSH, null);
                tts.speak(getResources().getString(R.string.tts_main_menu_button_layout), TextToSpeech.QUEUE_ADD, null);
            }
        }

    }

    @Override
    public void onResume() {
        lastBtnTouched = "";
        tts.speak(getResources().getString(R.string.tts_main_menu_button_layout), TextToSpeech.QUEUE_FLUSH, null);
        Log.e("Log", "APP - Resumed to main menu");
        super.onResume();
    }


}