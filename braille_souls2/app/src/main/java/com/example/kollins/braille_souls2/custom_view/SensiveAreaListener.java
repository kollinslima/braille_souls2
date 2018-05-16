package com.example.kollins.braille_souls2.custom_view;

public interface SensiveAreaListener {
    void onSensiveArea();
    void onNonSensiveArea();
    void onChangeArea();
    void onDoubleTap();
    void onLongPress();
    void onDoubleFingerTap();
    void onTap(float posX, float posY);
}

