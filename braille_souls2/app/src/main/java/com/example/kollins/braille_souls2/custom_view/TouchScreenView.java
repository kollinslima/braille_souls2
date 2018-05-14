package com.example.kollins.braille_souls2.custom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.kollins.braille_souls2.R;

public class TouchScreenView extends View implements GestureDetector.OnGestureListener {

    private int rows, columns;
    GestureDetector gestureDetector;

    private int screenWidth, screenHeight;

    public TouchScreenView(Context context) {
        super(context);
        gestureDetector = new GestureDetector(context,this);
        rows = 1;
        columns = 1;
        setFocusable(true);
    }

    public TouchScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.TouchScreenView);
        gestureDetector = new GestureDetector(context,this);
        rows = arr.getInteger(R.styleable.TouchScreenView_rows, 1);
        columns = arr.getInteger(R.styleable.TouchScreenView_columns, 1);
        setFocusable(true);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.screenWidth = w;
        this.screenHeight = h;

    }

    //Desenha tela
//    @Override
//    protected void onDraw(Canvas canvas) {
//
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                Log.d("MOTION", "Estou em " + (int)(event.getX()/(screenWidth/columns)) + " " + (int)(event.getY()/(screenHeight/rows)));

                break;
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("MOTION", "onDown");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d("MOTION", "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("MOTION", "onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d("MOTION", "onScroll");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d("MOTION", "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("MOTION", "onFling");
        return false;
    }
}
