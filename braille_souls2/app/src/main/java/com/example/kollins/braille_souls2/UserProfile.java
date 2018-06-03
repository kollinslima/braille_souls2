package com.example.kollins.braille_souls2;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class UserProfile {

    private final String FIRST_BEEP = "FirstBeep";
    private final String FIRST_VIVRATION = "FirstVibration";
    private final String FIRST_DOUBLE_TAP = "FirstDoubleTap";
    private final String FIRST_LONG_TAP = "FirstLongTap";
    private final String FIRST_TWO_FINGERS = "FirstTwoFingers";
    private final String TUTORIAL_END = "TutorialEnd";

    private SharedPreferences preferences;

    private boolean firstBeep, firstVibration, firstDoubleTap, firstLongTap, firstTwoFingers, tutorialEnd;

    public UserProfile(Context mainMenuContext) {
        preferences = mainMenuContext.getSharedPreferences("user", MODE_PRIVATE);

        firstBeep = preferences.getBoolean(FIRST_BEEP, false);
        firstVibration = preferences.getBoolean(FIRST_VIVRATION, false);
        firstDoubleTap = preferences.getBoolean(FIRST_DOUBLE_TAP, false);
        firstLongTap = preferences.getBoolean(FIRST_LONG_TAP, false);
        firstTwoFingers = preferences.getBoolean(FIRST_TWO_FINGERS, false);
        tutorialEnd = preferences.getBoolean(TUTORIAL_END, false);
    }

    public void saveData(){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(FIRST_BEEP, firstBeep);
        editor.putBoolean(FIRST_VIVRATION, firstVibration);
        editor.putBoolean(FIRST_DOUBLE_TAP, firstDoubleTap);
        editor.putBoolean(FIRST_LONG_TAP, firstLongTap);
        editor.putBoolean(FIRST_TWO_FINGERS, firstTwoFingers);
        editor.putBoolean(TUTORIAL_END, tutorialEnd);

        editor.apply();
    }

    public boolean isFirstBeep() {
        return firstBeep;
    }

    public void setFirstBeep(boolean firstBeep) {
        this.firstBeep = firstBeep;
    }

    public boolean isFirstVibration() {
        return firstVibration;
    }

    public void setFirstVibration(boolean firstVibration) {
        this.firstVibration = firstVibration;
    }

    public boolean isFirstDoubleTap() {
        return firstDoubleTap;
    }

    public void setFirstDoubleTap(boolean firstDoubleTap) {
        this.firstDoubleTap = firstDoubleTap;
    }

    public boolean isFirstLongTap() {
        return firstLongTap;
    }

    public void setFirstLongTap(boolean firstLongTap) {
        this.firstLongTap = firstLongTap;
    }

    public boolean isFirstTwoFingers() {
        return firstTwoFingers;
    }

    public void setFirstTwoFingers(boolean firstTwoFingers) {
        this.firstTwoFingers = firstTwoFingers;
    }

    public boolean isTutorialEnd() {
        return tutorialEnd;
    }

    public void setTutorialEnd(boolean tutorialEnd) {
        this.tutorialEnd = tutorialEnd;
    }
}
