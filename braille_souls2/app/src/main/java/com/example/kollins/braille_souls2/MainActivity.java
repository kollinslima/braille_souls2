package com.example.kollins.braille_souls2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kollins.braille_souls2.custom_view.TouchScreenView_Test;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new TouchScreenView_Test(this));
    }
}
